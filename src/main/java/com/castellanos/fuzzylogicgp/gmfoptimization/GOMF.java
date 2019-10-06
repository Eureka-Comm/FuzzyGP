/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.gmfoptimization;

import com.castellanos.fuzzylogicgp.base.GeneratorNode;
import com.castellanos.fuzzylogicgp.base.IMPNode;
import com.castellanos.fuzzylogicgp.base.Node;
import com.castellanos.fuzzylogicgp.base.NodeType;
import com.castellanos.fuzzylogicgp.base.OperatorException;
import com.castellanos.fuzzylogicgp.base.Predicate;
import com.castellanos.fuzzylogicgp.base.StateNode;
import com.castellanos.fuzzylogicgp.evaluation.EvaluatePredicate;
import com.castellanos.fuzzylogicgp.logic.ALogic;
import com.castellanos.fuzzylogicgp.logic.GMBC;
import com.castellanos.fuzzylogicgp.membershipfunction.FPG;
import com.castellanos.fuzzylogicgp.parser.ParserPredicate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

/**
 * Generalized Optimizer of Membership Functions
 *
 * @author Castellanos Alvarez, Alejandro.
 * @since October 06, 2019.
 * @version 0.5.0
 */
public class GOMF {

    private final ALogic logic;
    private final double mut_percentage;
    private final int adj_num_pop;
    private final int adj_iter;
    private final double adj_truth_value;

    private Table data;
    private Table fuzzyData;
    private DoubleColumn resultColumn;

    private Predicate predicatePattern;
    private List<StateNode> sns;
    private final HashMap<String, Double[]> minPromMaxMapValues;

    private static final Random rand = new Random();
    private final Gson print = new GsonBuilder().setPrettyPrinting().create();
    private final ChromosomeComparator chromosomeComparator = new ChromosomeComparator();

    public GOMF(Table data, ALogic logic, double mut_percentage, int adj_num_pop, int adj_iter,
            double adj_truth_value) {
        this.data = data;
        this.logic = logic;
        this.mut_percentage = mut_percentage;
        this.adj_num_pop = adj_num_pop;
        this.adj_iter = adj_iter;
        this.adj_truth_value = adj_truth_value;
        this.minPromMaxMapValues = new HashMap<>();

    }

    public void optimize(Predicate p) {
        this.predicatePattern = p;
        sns = new ArrayList<>();
        System.out.println("Checking linguistic states...");
        predicatePattern.getNodes().forEach((k, v) -> {
            if (v.getType().equals(NodeType.STATE)) {
                StateNode c = (StateNode) v;
                if (c.getMembershipFunction() == null) {
                    sns.add(c);
                }
            }
        });
        System.out.println("Functions to optimize: " + sns.size());
        genetic();
    }

    private void genetic() {
        ChromosomePojo[] currentPop;

        int iteration = 0;

        currentPop = makePop();
        evaluatePredicate(currentPop);

        Arrays.sort(currentPop, chromosomeComparator);
        chromosomePrint(iteration, currentPop);

        while (iteration < adj_iter && currentPop[currentPop.length - 1].fitness < adj_truth_value) {

            iteration++;
            ChromosomePojo[] childList = chromosomeCrossover(currentPop);
            // Replace best
            System.out.println("child crossover: " + childList.length);
            if (childList != null) {
                evaluatePredicate(childList);
                for (int i = 0; i < childList.length; i++) {
                    ChromosomePojo get = childList[i];
                    for (int j = 0; j < currentPop.length; j++) {
                        ChromosomePojo parent = currentPop[j];
                        if (get.fitness > parent.fitness) {
                            System.out.println("Replace " + j + " > " + get.fitness + " - " + parent.fitness);
                            currentPop[j] = get;
                            break;
                        }
                    }

                }
            }
            chromosomeMutation(currentPop);
            evaluatePredicate(currentPop);

            Arrays.sort(currentPop, chromosomeComparator);
            chromosomePrint(iteration, currentPop);
        }

        ChromosomePojo bestFound = currentPop[currentPop.length - 1];
        System.out.println("Best solution: " + bestFound.fitness + " := " + Arrays.toString(bestFound.elements));
        for (FunctionWrap k : bestFound.elements) {
            Node node = predicatePattern.getNode(k.getOwner());
            if (node instanceof StateNode) {
                StateNode st = (StateNode) node;
                st.setMembershipFunction(k.getFpg());
                System.out.println(st);
            }
        }
        EvaluatePredicate evaluator = new EvaluatePredicate(logic, data);
        evaluator.setPredicate(predicatePattern);
        System.out.println("ForAll: " + evaluator.evaluate());
    }

    private ChromosomePojo[] makePop() {
        ChromosomePojo[] pop = new ChromosomePojo[adj_num_pop];
        for (int i = 0; i < adj_num_pop; i++) {
            FunctionWrap m[];

            m = new FunctionWrap[sns.size()];

            for (int j = 0; j < sns.size(); j++) {
                m[j] = randomChromosome(sns.get(j));
            }
            ChromosomePojo cp = new ChromosomePojo();
            cp.elements = m;
            pop[i] = cp;
        }
        return pop;
    }

    private FunctionWrap randomChromosome(StateNode _item) {
        FPG f = new FPG();
        Double[] r = minPromMaxValues(_item.getColName());
        minPromMaxMapValues.put(_item.getId(), r);
        f.setGamma(randomValue(r[0], r[2]));
        Double value;
        do {
            value = randomValue(r[0], f.getGamma());
        } while (value >= f.getGamma());

        f.setBeta(value);
        f.setM(rand.nextDouble());

        FunctionWrap map = new FunctionWrap(_item.getId(), f);
        return map;
    }

    private Double[] minPromMaxValues(String colname) {
        Double[] v = new Double[3];
        Column<?> column = data.column(colname);
        v[0] = Double.parseDouble(column.getString(0));
        v[2] = Double.parseDouble(column.getString(1));
        double current, prom = 0;

        for (int i = 0; i < column.size(); i++) {
            current = Double.valueOf(column.getString(i));
            if (v[0] > current) {
                v[0] = current;
            }
            if (v[2] < current) {
                v[2] = current;
            }
            prom += current;
        }
        v[1] = prom / column.size();
        return v;
    }

    private Double randomValue(double min, double max) {
        return rand.doubles(min, max + 1).findFirst().getAsDouble();
    }

    public void optimize(Table data, Predicate p) {
        this.data = data;
        this.predicatePattern = p;
        genetic();
    }

    private void evaluatePredicate(ChromosomePojo[] currentPop) {

        for (int i = 0; i < currentPop.length; i++) {
            ChromosomePojo mf = currentPop[i];
            Predicate predicate = new Predicate(predicatePattern);
            for (FunctionWrap k : mf.elements) {
                Node node = predicate.getNode(k.getOwner());
                if (node instanceof StateNode) {
                    StateNode st = (StateNode) node;
                    st.setMembershipFunction(k.getFpg());
                }
            }

            mf.fitness = evaluate(predicate);
        }
        // return fitPop;
    }

    public static void main(String[] args) throws IOException, OperatorException {
        Table d = Table.read().csv("src/main/resources/datasets/tinto.csv");
        GOMF gomf = new GOMF(d, new GMBC(), 0.05, 10, 1, 0.9);
        StateNode sa = new StateNode("alcohol", "alcohol");
        StateNode sph = new StateNode("pH", "pH");
        StateNode sq = new StateNode("quality", "quality");
        StateNode sfa = new StateNode("fixed_acidity", "fixed_acidity");
        List<StateNode> states = new ArrayList<>();
        states.add(sa);
        states.add(sph);
        states.add(sq);
        states.add(sfa);

        GeneratorNode g = new GeneratorNode("*", new NodeType[] {}, new ArrayList<>());
        List<GeneratorNode> gs = new ArrayList<>();
        gs.add(g);
        String expression = "(IMP \"alcohol\" \"quality\")";

        ParserPredicate pp = new ParserPredicate(expression, states, gs);
        gomf.optimize(pp.parser());
    }

    private void chromosomePrint(int iteration, ChromosomePojo[] currentPop) {
        System.out.println("*****-*****-*****-*****-*****-*****-*****");
        for (int i = 0; i < currentPop.length; i++) {
            ChromosomePojo m = currentPop[i];
            String st = "";
            for (FunctionWrap fmap : m.elements) {
                st += " " + fmap.getFpg().toString();
            }
            System.out.println(iteration + "[" + i + "]- Fit: " + m.fitness + " - " + st);
        }
    }

    private int maxFitValue(double[] fitPop) {
        int index = -1;
        double value = 0;
        for (int i = 0; i < fitPop.length; i++) {
            if (fitPop[i] > value) {
                value = fitPop[i];
                index = i;
            }
        }
        return index;
    }

    public double evaluate(Predicate p) {
        fuzzyData = null;
        dataFuzzy(p);
        fitCompute(p);
        if (p.getIdFather() != null && !p.getNode(p.getIdFather()).getType().equals(NodeType.STATE)) {
            StringColumn fa = StringColumn.create("For All");
            double forAllValue = logic.forAll(resultColumn.asList());
            p.setFitness(forAllValue);
            fa.append("" + p.getFitness());

            StringColumn ec = StringColumn.create("Exist");
            ec.append("" + logic.exist(resultColumn.asList()));
            for (int i = 1; i < fuzzyData.rowCount(); i++) {
                fa.append("");
                ec.append("");
            }
            fuzzyData.addColumns(fa, ec, resultColumn);
            return forAllValue;
        }
        return Double.NaN;
    }

    private void fitCompute(Predicate p) {
        double result;
        if (resultColumn == null) {
            resultColumn = DoubleColumn.create("result");
        } else {
            resultColumn.clear();
        }
        for (int i = 0; i < fuzzyData.rowCount(); i++) {
            try {
                result = fitValue(p, p.getNode(p.getIdFather()), i);
                resultColumn.append(result);
            } catch (OperatorException ex) {
                Logger.getLogger(GOMF.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private Double fitValue(Predicate p, Node node, int index) throws OperatorException {
        double aux = 1;
        List<Node> child;
        switch (node.getType()) {
        case AND:
            child = p.searchChilds(node);
            for (int i = 1; i < child.size(); i++) {
                aux *= fitValue(p, child.get(i), index);
            }
            return logic.and(aux, fitValue(p, child.get(0), index));
        case OR:
            child = p.searchChilds(node);
            for (int i = 0; i < child.size(); i++) {
                aux *= (1 - fitValue(p, child.get(i), index));
            }
            // return logic.or(aux, fitValue(child.get(0), index));
            return logic.or(aux, (double) child.size());
        case NOT:
            return logic.not(fitValue(p, p.searchChilds(node).get(0), index));
        case IMP:
            IMPNode imp = (IMPNode) node;
            return logic.imp(fitValue(p, p.getNode(imp.getLeftID()), index),
                    fitValue(p, p.getNode(imp.getRighID()), index));
        case EQV:
            child = p.searchChilds(node);
            return logic.eqv(fitValue(p, child.get(0), index), fitValue(p, child.get(1), index));
        case STATE:
            StateNode st = (StateNode) node;
            return Double.valueOf(fuzzyData.getString(index, st.getLabel()));
        default:
            throw new UnsupportedOperationException("Dont supported: " + node.getType() + " : " + node.getId());                                                                                                    // Templates.
        }

    }

    private void dataFuzzy(Predicate p) {
        if (fuzzyData == null) {
            fuzzyData = Table.create();
        } else {
            fuzzyData.clear();
        }
        p.getNodes().forEach((String k, Node v) -> {
            if (v instanceof StateNode) {
                StateNode s = (StateNode) v;
                if (!fuzzyData.columnNames().contains(s.getColName())) {
                    ColumnType type = data.column(s.getColName()).type();

                    DoubleColumn dc = DoubleColumn.create(s.getLabel());

                    if (type == ColumnType.DOUBLE) {
                        Column<Double> column = (Column<Double>) data.column(s.getColName());

                        for (Double cell : column) {
                            dc.append(s.getMembershipFunction().evaluate(cell));
                        }

                    } else if (type == ColumnType.FLOAT) {
                        Column<Float> column = (Column<Float>) data.column(s.getColName());
                        for (Float cell : column) {
                            dc.append(s.getMembershipFunction().evaluate(cell));
                        }

                    } else if (type == ColumnType.INTEGER) {
                        Column<Integer> column = (Column<Integer>) data.column(s.getColName());
                        for (Integer cell : column) {
                            dc.append(s.getMembershipFunction().evaluate(cell));
                        }

                    } else if (type == ColumnType.LONG) {
                        Column<Long> column = (Column<Long>) data.column(s.getColName());
                        for (Long cell : column) {
                            dc.append(s.getMembershipFunction().evaluate(cell));
                        }

                    } else {
                        System.out.println("ColumnType: " + type);
                    }
                    fuzzyData.addColumns(dc);
                }
            }
        });
        // System.out.println(fuzzyData);
    }

    private void chromosomeMutation(ChromosomePojo[] pop) {
        for (int i = 0; i < pop.length; i++) {
            ChromosomePojo next = pop[i];

            if (rand.nextFloat() < mut_percentage) {
                int index = (int) Math.floor(randomValue(0, next.elements.length - 1));
                int indexParam = (int) Math.floor(randomValue(0, 2));
                System.out.println("Element <" + i + ">mutated: " + index + " Param : " + indexParam);
                FPG element = next.elements[index].getFpg();
                Double[] r = minPromMaxMapValues.get(next.elements[index].getOwner());
                Double value;

                switch (index) {
                case 0:
                    do {
                        value = randomValue(element.getBeta(), r[2]);
                    } while (value <= element.getBeta());
                    element.setGamma(value);
                    break;
                case 1:
                    do {
                        value = randomValue(r[0], element.getGamma());
                    } while (value >= element.getGamma());
                    element.setBeta(value);
                    break;
                case 2:
                    element.setM(rand.nextDouble());
                    break;
                }
            }
        }
    }

    private ChromosomePojo[] chromosomeCrossover(ChromosomePojo[] pop) {
        int parentSize = (adj_num_pop < 10) ? (adj_num_pop < 2) ? adj_num_pop : 2 : adj_num_pop / 5;
        ChromosomePojo[] bestParents = new ChromosomePojo[parentSize];
        ChromosomePojo[] parents = new ChromosomePojo[adj_num_pop - parentSize];
        ChromosomePojo[] child = new ChromosomePojo[(parentSize % 2 == 0) ? parentSize / 2 : (parentSize + 1) / 2];
        ChromosomePojo[] otherChild = new ChromosomePojo[(parents.length % 2 == 0) ? parents.length / 2
                : (parents.length + 1) / 2];
        System.out.println("Crossover with best parents: " + parentSize + ", other parents : " + parents.length);

        for (int i = 0; i < bestParents.length; i++) {
            bestParents[i] = pop[pop.length - i - 1];
        }

        for (int i = 0; i < child.length; i++) {
            child[i] = new ChromosomePojo();
        }
        for (int i = 0; i < parents.length; i++) {
            parents[i] = pop[i];
        }
        for (int i = 0; i < otherChild.length; i++) {
            otherChild[i] = new ChromosomePojo();
        }

        if (bestParents.length != 1) {
            Crossover(parentSize, bestParents, child);
            Crossover(parents.length, parents, otherChild);
            ChromosomePojo[] childrens = new ChromosomePojo[child.length + otherChild.length];
            System.arraycopy(child, 0, childrens, 0, child.length);
            System.arraycopy(otherChild, 0, childrens, child.length, otherChild.length);
            return childrens;
        } else if (bestParents.length == 1) {
            System.out.println("Nothing to do.");
            return null;
        }
        return null;
    }

    private void Crossover(int parentSize, ChromosomePojo[] parents, ChromosomePojo[] child) {
        int childIndex = 0;
        for (int i = 0; i < parents.length; i++) {
            ChromosomePojo p1 = parents[i];
            ChromosomePojo p2;
            if (i + 1 < parents.length) {
                p2 = parents[i++];
            } else {
                p2 = parents[0];
            }
            child[childIndex].elements = new FunctionWrap[p1.elements.length];
            for (int j = 0; j < p1.elements.length; j++) {
                FunctionWrap p1Map = p1.elements[j];
                FunctionWrap p2Map = p2.elements[j];
                child[childIndex].elements[j] = new FunctionWrap();

                String next = p1Map.getOwner();
                FPG fp1 = p1Map.getFpg();
                FPG fp2 = p2Map.getFpg();
                FPG childFpg = null;
                Double value;
                Double[] r;

                int select = (int) Math.floor(randomValue(0, 2));
                switch (select) {
                case 0:
                    if (fp2.getGamma() > fp1.getBeta()) {
                        childFpg = new FPG(fp2.getGamma(), fp1.getBeta(), fp1.getM());
                    } else {
                        r = minPromMaxMapValues.get(p1Map.getOwner());
                        do {
                            value = randomValue(r[0], fp2.getGamma());
                        } while (value >= fp2.getGamma());
                        childFpg = new FPG(fp2.getGamma(), value, fp1.getM());
                    }
                    break;
                case 1:
                    if (fp2.getBeta() < fp1.getGamma()) {
                        childFpg = new FPG(fp1.getGamma(), fp2.getBeta(), fp1.getM());
                    } else {
                        r = minPromMaxMapValues.get(p1Map.getOwner());
                        do {
                            value = randomValue( fp2.getBeta(), r[2]);
                        } while (value <= fp2.getBeta());
                        childFpg = new FPG(value, fp2.getBeta(), fp1.getM());
                    }
                    break;
                case 2:
                    childFpg = new FPG(fp1.getGamma(), fp1.getBeta(), fp2.getM());
                    break;
                }
                child[childIndex].elements[j] = new FunctionWrap(next, childFpg);
            }

            childIndex++;
        }
    }

    private class FunctionWrap {

        private FPG fpg;
        private String owner;

        public FunctionWrap(String owner, FPG fpg) {
            this.fpg = fpg;
            this.owner = owner;
        }

        private FunctionWrap() {
        }

        public FPG getFpg() {
            return fpg;
        }

        public String getOwner() {
            return owner;
        }

        public void setFpg(FPG fpg) {
            this.fpg = fpg;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        @Override
        public String toString() {
            return this.owner + " - " + this.fpg.toString();
        }

    }

    private class ChromosomePojo {

        double fitness;
        FunctionWrap[] elements;

        @Override
        public String toString() {
            return Arrays.toString(elements).replaceAll(",", "");
        }
    }

    private class ChromosomeComparator implements Comparator<ChromosomePojo> {

        @Override
        public int compare(ChromosomePojo t, ChromosomePojo t1) {
            if (t.fitness < t1.fitness) {
                return -1;
            } else if (t.fitness > t1.fitness) {
                return 1;
            } else {
                return 0;
            }

        }

    }
}
