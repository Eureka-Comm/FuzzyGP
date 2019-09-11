/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.gmfoptimization;

import com.castellanos.fuzzylogicgp.base.GeneratorNode;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

/**
 * Generalized Optimizer of Membership Functions
 *
 * @author hp
 */
public class GOMF {

    private final ALogic logic;
    private final double mut_percentage;
    private int adj_num_pop;
    private final int adj_iter;
    private final double adj_truth_value;

    private Table data;

    private Predicate predicatePattern;
    private List<StateNode> sns;
    private final HashMap<String, Double[]> minPromMaxMapValues;

    private static final Random rand = new Random();
    private final Gson print = new GsonBuilder().setPrettyPrinting().create();
    private final ChromosomeComparator chromosomeComparator = new ChromosomeComparator();
    private EvaluatePredicate evaluator;

    public GOMF(Table data, ALogic logic, double mut_percentage, int adj_num_pop, int adj_iter, double adj_truth_value) {
        this.data = data;
        this.logic = logic;
        this.mut_percentage = mut_percentage;
        this.adj_num_pop = adj_num_pop;
        this.adj_iter = adj_iter;
        this.adj_truth_value = adj_truth_value;
        this.evaluator = new EvaluatePredicate(logic, data);
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
        List<ChromosomePojo> currentPop;
        List<ChromosomePojo> lastPop = null;

        int iteration = 0;

        currentPop = makePop();
        evaluatePredicate(currentPop);
        Collections.sort(currentPop, chromosomeComparator);
        while (iteration < adj_iter && currentPop.get(currentPop.size() - 1).fitness < adj_truth_value) {

            iteration++;

            //selection
            //crossover
            chromosomeMutation(currentPop);
            evaluatePredicate(currentPop);
            Collections.sort(currentPop, chromosomeComparator);

            lastPop = currentPop;
            chromosomePrint(iteration, currentPop);
        }

        ChromosomePojo bestFound = currentPop.get(currentPop.size() - 1);
        System.out.println("Best solution: " + bestFound.fitness + " := " + Arrays.toString(bestFound.elements));
        for (HashMap<String, FPG> hashMap : bestFound.elements) {
            hashMap.forEach((k, v) -> {
                Node node = predicatePattern.getNode(k);
                if (node instanceof StateNode) {
                    StateNode st = (StateNode) node;
                    st.setMembershipFunction(v);
                }
            });

        }
        evaluator.setPredicate(predicatePattern);
        System.out.println("ForAll: " + evaluator.evaluate());
    }

    private List<ChromosomePojo> makePop() {
        List<ChromosomePojo> pop = new ArrayList<>();
        for (int i = 0; i < adj_num_pop; i++) {
            HashMap<String, FPG> m[];

            m = new HashMap[sns.size()];

            for (int j = 0; j < sns.size(); j++) {
                m[j] = new HashMap<>(randomChromosome(sns.get(j)));
            }
            ChromosomePojo cp = new ChromosomePojo();
            cp.elements = m;
            pop.add(cp);
        }
        return pop;
    }

    private HashMap<String, FPG> randomChromosome(StateNode _item) {
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

        HashMap<String, FPG> map = new HashMap<>();
        map.put(_item.getId(), f);
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

    private void evaluatePredicate(List<ChromosomePojo> currentPop) {
        for (int i = 0; i < currentPop.size(); i++) {
            ChromosomePojo mf = currentPop.get(i);

            for (HashMap<String, FPG> hashMap : mf.elements) {
                hashMap.forEach((k, v) -> {
                    Node node = predicatePattern.getNode(k);
                    if (node instanceof StateNode) {
                        StateNode st = (StateNode) node;
                        st.setMembershipFunction(v);
                    }
                });

            }
            evaluator.setPredicate(predicatePattern);
            mf.fitness = evaluator.evaluate();

        }
        //return fitPop;
    }

    public static void main(String[] args) throws IOException, OperatorException {
        Table d = Table.read().csv("src/main/resources/datasets/tinto.csv");
        GOMF gomf = new GOMF(d, new GMBC(), 0.05, 10, 5, 1);
        StateNode sa = new StateNode("alcohol", "alcohol");
        StateNode sph = new StateNode("pH", "pH");
        StateNode sq = new StateNode("quality", "quality");
        StateNode sfa = new StateNode("fixed_acidity", "fixed_acidity");
        List<StateNode> states = new ArrayList<>();
        states.add(sa);
        states.add(sph);
        states.add(sq);
        states.add(sfa);

        GeneratorNode g = new GeneratorNode("*", new NodeType[]{}, new ArrayList<>());
        List<GeneratorNode> gs = new ArrayList<>();

        String expression = "(IMP \"quality\" \"pH\")";

        ParserPredicate pp = new ParserPredicate(expression, states, gs);
        gomf.optimize(pp.parser());
    }

    private void chromosomePrint(int iteration, List<ChromosomePojo> currentPop) {
        System.out.println("*****-*****-*****-*****-*****-*****-*****");
        for (int i = 0; i < currentPop.size(); i++) {
            ChromosomePojo m = currentPop.get(i);
            String st = "";
            for (HashMap<String, FPG> fmap : m.elements) {
                st += " " + fmap.values().toString();
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

    private void chromosomeMutation(List<ChromosomePojo> pop) {
        Iterator<ChromosomePojo> iterator = pop.iterator();
        while (iterator.hasNext()) {
            ChromosomePojo next = iterator.next();
            
            if (rand.nextFloat() < mut_percentage) {
                int index = (int) Math.floor(randomValue(0, next.elements.length - 1));
                int indexParam = (int) Math.floor(randomValue(0, 2));
                System.out.println("Element <"+ pop.indexOf(next)+">mutated: " + index + " Param : " + indexParam);
                FPG element = next.elements[index].values().iterator().next();
                Double[] r = minPromMaxMapValues.get(next.elements[index].keySet().iterator().next());
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
                            value = randomValue(0, element.getGamma());
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

    private class ChromosomePojo {

        double fitness;
        HashMap<String, FPG>[] elements;
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
