/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.castellanos.fuzzylogicgp.base.GeneratorNode;
import com.castellanos.fuzzylogicgp.base.Node;
import com.castellanos.fuzzylogicgp.base.NodeTree;
import com.castellanos.fuzzylogicgp.base.NodeType;
import com.castellanos.fuzzylogicgp.base.OperatorException;
import com.castellanos.fuzzylogicgp.base.StateNode;
import com.castellanos.fuzzylogicgp.logic.ALogic;
import com.castellanos.fuzzylogicgp.logic.GMBC;
import com.castellanos.fuzzylogicgp.membershipfunction.AMembershipFunction;
import com.castellanos.fuzzylogicgp.membershipfunction.FPG;
import com.castellanos.fuzzylogicgp.parser.ParserPredicate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

    private NodeTree predicatePattern;
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

    public void optimize(NodeTree p) {
        this.predicatePattern = p;
        sns = new ArrayList<>();
        /*predicatePattern.getNodes().forEach((k, v) -> {
            if (v.getType().equals(NodeType.STATE)) {
                StateNode c = (StateNode) v;
                if (c.getMembershipFunction() == null) {
                    sns.add(c);
                }
            }
        });*/
        NodeTree.getNodesByType(predicatePattern, NodeType.STATE).forEach(v->{
            if(v instanceof StateNode){
                StateNode c = (StateNode) v;
                if (c.getMembershipFunction() == null) {
                    sns.add(c);
                }
            }
        });
        genetic();
    }

    private void genetic() {
        // ChromosomePojo[] currentPop;
        ArrayList<ChromosomePojo> currentPop;
        int iteration = 0;

        currentPop = makePop();
        evaluatePredicate(currentPop);

        // chromosomePrint(iteration, currentPop);
        //BigDecimal truth_value = new BigDecimal(adj_truth_value);
        while (iteration < adj_iter && currentPop.get(currentPop.size() - 1).getFitness().compareTo(adj_truth_value) < 0) {
            // while (iteration < adj_iter) {
            iteration++;
            ArrayList<ChromosomePojo> childList = chromosomeCrossover(currentPop);

            // Replace best
            //System.out.println("child crossover: " + childList.size());
            if (childList != null) {
                evaluatePredicate(childList);
                for (int i = 0; i < childList.size(); i++) {
                    ChromosomePojo get = childList.get(i);
                    for (int j = 0; j < currentPop.size(); j++) {
                        ChromosomePojo parent = currentPop.get(i);
                        if (get.getFitness().compareTo(parent.getFitness()) > 0) {
                            //System.out.println("Replace " + j + " > " + get.getFitness() + " - " + parent.getFitness());
                            currentPop.set(j, get);
                            break;
                        }
                    }

                }
            }
            chromosomeMutation(currentPop);
            evaluatePredicate(currentPop);

            //Arrays.sort(currentPop, chromosomeComparator);
             currentPop.sort(chromosomeComparator);
           //chromosomePrint(iteration, currentPop);
        }

        ChromosomePojo bestFound = currentPop.get(0);
        for (ChromosomePojo chromosomePojo : currentPop) {
            if (chromosomePojo.fitness.compareTo(bestFound.fitness) > 0) {
                bestFound = chromosomePojo;
            }
        }
        //System.out.println("Best solution: " + bestFound.getFitness() + " := " + Arrays.toString(bestFound.getElements()));
        predicatePattern.setFitness(bestFound.getFitness());
        for (FunctionWrap k : bestFound.getElements()) {
            //Node node = predicatePattern.getNode(k.getOwner());
            Node node = predicatePattern.findById(k.getOwner());
            if (node instanceof StateNode) {
                StateNode st = (StateNode) node;
                try {
                    st.setMembershipFunction((AMembershipFunction) k.getFpg().clone());
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
                //System.out.println(st);
            }
        }
       
    }

    private ArrayList<ChromosomePojo> makePop() {
        // ChromosomePojo[] pop = new ChromosomePojo[adj_num_pop];
        ArrayList<ChromosomePojo> pop = new ArrayList<>();
        for (int i = 0; i < adj_num_pop; i++) {
            FunctionWrap m[];

            m = new FunctionWrap[sns.size()];

            for (int j = 0; j < sns.size(); j++) {
                m[j] = randomChromosome(sns.get(j));
            }
            ChromosomePojo cp = new ChromosomePojo();
            cp.setElements(m);
            pop.add(cp);
        }
        return pop;
    }

    private FunctionWrap randomChromosome(StateNode _item) {
        FPG f = new FPG();
        Double[] r = minPromMaxValues(_item.getColName());
        minPromMaxMapValues.put(_item.getId(), r);
        double gamma_double = randomValue(r[0], r[2]);
        f.setGamma((randomValue(r[0], r[2])));
        double value;
        do {
            value = randomValue(r[0], f.getGamma());

        } while (value >= gamma_double);
        // } while( value.compareTo(f.getGamma()) != -1);

        f.setBeta((value));
        f.setM((rand.nextDouble()));

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
        return (rand.doubles(min, max + 1).findFirst().getAsDouble());
    }

    public void optimize(Table data, NodeTree p)  {
        this.data = data;
        this.predicatePattern = p;
        genetic();
    }

    private void evaluatePredicate(ArrayList<ChromosomePojo> currentPop) {
        //System.out.println(currentPop);
        currentPop.forEach(mf -> {
            NodeTree predicate = null;
            try {
                predicate = (NodeTree) predicatePattern.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }

            for (FunctionWrap k : mf.getElements()) {
                Node node = predicate.findById(k.getOwner());
                if (node instanceof StateNode) {
                    StateNode st = (StateNode) node;
                    try {
                        st.setMembershipFunction((AMembershipFunction) k.getFpg().clone());
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                }
            }

            // mf.setFitness(evaluate(predicate));
            EvaluatePredicate evaluator = new EvaluatePredicate(logic, data);
            evaluator.setPredicate(predicate);
            mf.setFitness(evaluator.evaluate());
        });

    }

    private void chromosomePrint(int iteration, ArrayList<ChromosomePojo> currentPop) {
        System.out.println("*****-*****-*****-*****-*****-*****-*****");
        for (int i = 0; i < currentPop.size(); i++) {
            ChromosomePojo m = currentPop.get(i);
            // String st = "";
            System.out.println(iteration + "[ " + i + "]" + m.getFitness());
            for (FunctionWrap fmap : m.getElements()) {
                // st += " " + fmap.getFpg().toString();
                Node node = predicatePattern.findById(fmap.getOwner());
                if (node instanceof StateNode) {
                    StateNode stt = (StateNode) node;
                    stt.setMembershipFunction(fmap.getFpg());
                    System.out.println(stt);
                    stt.setMembershipFunction(null);
                }

            }

            // System.out.println(iteration + "[" + i + "]- Fit: " + m.getFitness() + " - "
            // + st);
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

    private void chromosomeMutation(ArrayList<ChromosomePojo> pop) {
        for (int i = 0; i < pop.size(); i++) {
            ChromosomePojo next = pop.get(i);

            if (rand.nextFloat() < mut_percentage) {
                int index = (int) Math.floor(randomValue(0, next.getElements().length - 1).doubleValue());
                int indexParam = (int) Math.floor(randomValue(0, 2).doubleValue());
                //System.out.println("Element <" + i + ">mutated: " + index + " Param : " + indexParam);
                FPG element = next.getElements()[index].getFpg();
                Double[] r = minPromMaxMapValues.get(next.getElements()[index].getOwner());
                Double value;

                switch (index) {
                    case 0:
                        Double element_Beta = element.getBeta();
                        do {
                            value = randomValue(element.getBeta().doubleValue(), r[2]);
                        } while (value <= element_Beta);
                        // }while (value.compareTo(element.getBeta()) != 1);
                        element.setGamma((value));
                        break;
                    case 1:
                        Double element_gamma = element.getGamma();
                        do {
                            value = randomValue(r[0], element.getGamma().doubleValue());
                        } while (value >= element_gamma);
                        // } while(value.compareTo(element.getGamma()) != 1);
                        element.setBeta((value));
                        break;
                    case 2:
                        element.setM((rand.nextDouble()));
                        break;
                }
            }
        }
    }

    private ArrayList<ChromosomePojo> chromosomeCrossover(ArrayList<ChromosomePojo> pop) {
        int parentSize = (adj_num_pop < 10) ? (adj_num_pop < 2) ? adj_num_pop : 2 : adj_num_pop / 5;
        ChromosomePojo[] bestParents = new ChromosomePojo[parentSize];
        ChromosomePojo[] parents = new ChromosomePojo[adj_num_pop - parentSize];
        ChromosomePojo[] child = new ChromosomePojo[(parentSize % 2 == 0) ? parentSize / 2 : (parentSize + 1) / 2];
        ChromosomePojo[] otherChild = new ChromosomePojo[(parents.length % 2 == 0) ? parents.length / 2
                : (parents.length + 1) / 2];
        //System.out.println("Crossover with best parents: " + parentSize + ", other parents : " + parents.length);

        for (int i = 0; i < bestParents.length; i++) {
            bestParents[i] = pop.get(pop.size() - i - 1);
        }

        for (int i = 0; i < child.length; i++) {
            child[i] = new ChromosomePojo();
        }
        for (int i = 0; i < parents.length; i++) {
            parents[i] = pop.get(i);
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
            ArrayList<ChromosomePojo> list = new ArrayList<>();
            for (ChromosomePojo chromosomePojo : childrens) {
                list.add(chromosomePojo);
            }
            return list;
        } else if (bestParents.length == 1) {
            //System.out.println("Nothing to do.");
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
            child[childIndex].setElements(new FunctionWrap[p1.elements.length]);
            for (int j = 0; j < p1.elements.length; j++) {
                FunctionWrap p1Map = p1.getElements()[j];
                FunctionWrap p2Map = p2.getElements()[j];
                child[childIndex].getElements()[j] = new FunctionWrap();

                String next = p1Map.getOwner();
                FPG fp1 = p1Map.getFpg();
                FPG fp2 = p2Map.getFpg();
                FPG childFpg = null;

                int select = (int) Math.floor(randomValue(0, 2).doubleValue());
                switch (select) {
                    case 0:
                        // if (fp2.getGamma() > fp1.getBeta()) {
                        if (fp2.getGamma().compareTo(fp1.getBeta()) == 1) {
                            childFpg = new FPG(fp1.getBeta().toString(), fp2.getGamma().toString(),
                                    fp1.getM().toString());
                        } else {
                            /*
                             * r = minPromMaxMapValues.get(p1Map.getOwner()); do { value = randomValue(r[0],
                             * fp2.getGamma()); } while (value >= fp2.getGamma());
                             */
                            childFpg = new FPG(fp1.getGamma().toString(), fp2.getBeta().toString(),
                                    fp1.getM().toString());
                        }
                        break;
                    case 1:
                        // if (fp2.getBeta() < fp1.getGamma()) {
                        if (fp2.getBeta().compareTo(fp1.getGamma()) == -1) {
                            childFpg = new FPG(fp2.getBeta().toString(), fp1.getGamma().toString(),
                                    fp1.getM().toString());
                        } else {
                            /*
                             * r = minPromMaxMapValues.get(p1Map.getOwner()); do { value = randomValue(
                             * fp2.getBeta(), r[2]); } while (value <= fp2.getBeta());
                             */
                            childFpg = new FPG(fp1.getGamma().toString(), fp2.getBeta().toString(),
                                    fp1.getM().toString());
                        }
                        break;
                    case 2:
                        childFpg = new FPG(fp1.getBeta().toString(), fp1.getGamma().toString(), fp2.getM().toString());
                        break;
                }
                child[childIndex].getElements()[j] = new FunctionWrap(next, childFpg);
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

        protected Double fitness;
        protected FunctionWrap[] elements;

        /**
         * @param elements the elements to set
         */
        public void setElements(FunctionWrap[] elements) {
            this.elements = elements;
        }

        /**
         * @param fitness the fitness to set
         */
        public void setFitness(Double fitness) {
            this.fitness = fitness;
        }

        /**
         * @return the elements
         */
        public FunctionWrap[] getElements() {
            return elements;
        }

        /**
         * @return the fitness
         */
        public Double getFitness() {
            return fitness;
        }

        @Override
        public String toString() {
            return Arrays.toString(elements).replaceAll(",", "");
        }
    }

    private class ChromosomeComparator implements Comparator<ChromosomePojo> {

        @Override
        public int compare(ChromosomePojo t, ChromosomePojo t1) {
            return t.getFitness().compareTo(t1.getFitness());
        }

    }

    public static void main(String[] args) throws IOException, OperatorException, CloneNotSupportedException {
        Table d = Table.read().csv("src/main/resources/datasets/tinto.csv");
        GOMF gomf = new GOMF(d, new GMBC(), 0.15, 5, 5, 1);
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
        String expression = "(IMP (AND \"alcohol\" \"fixed_acidity\") \"quality\")";
        long startTime = System.nanoTime();
        ParserPredicate pp = new ParserPredicate(expression, states, gs);
        NodeTree p = pp.parser();
        gomf.optimize(p);
        System.out.println(p+" "+p.getFitness());
        long endTime = System.nanoTime();

        long duration = (endTime - startTime); // divide by 1000000 to get milliseconds.
        System.out.println("That took " + (duration / 1000000) + " milliseconds");
    }
}
