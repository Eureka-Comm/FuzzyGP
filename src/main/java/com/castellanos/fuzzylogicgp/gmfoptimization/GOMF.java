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
import java.util.HashMap;
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

    private static final Random rand = new Random();
    private final Gson print = new GsonBuilder().setPrettyPrinting().create();

    public GOMF(Table data, ALogic logic, double mut_percentage, int adj_num_pop, int adj_iter, double adj_truth_value) {
        this.data = data;
        this.logic = logic;
        this.mut_percentage = mut_percentage;
        this.adj_num_pop = adj_num_pop;
        this.adj_iter = adj_iter;
        this.adj_truth_value = adj_truth_value;

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
        List<HashMap<String, FPG>[]> currentPop;
        List<HashMap<String, FPG>[]> lastPop = null;

        double fitPop[];
        int indexMaxValue;

        int iteration = 0;
        
        do {
            currentPop = makePop();
            fitPop = evaluatePredicate(currentPop);
            indexMaxValue = maxFitValue(fitPop);

            iteration++;
            lastPop = currentPop;
            chromosomePrint(iteration, fitPop, currentPop);

        } while (iteration < adj_iter && fitPop[indexMaxValue] <= adj_truth_value);
        System.out.println("Best solution: "+fitPop[indexMaxValue]+" := "+Arrays.toString(currentPop.get(indexMaxValue)));
    }

    public List<HashMap<String, FPG>[]> makePop() {
        List<HashMap<String, FPG>[]> pop = new ArrayList<>();
        for (int i = 0; i < adj_num_pop; i++) {
            HashMap<String, FPG> m[];

            m = new HashMap[sns.size()];

            for (int j = 0; j < sns.size(); j++) {
                m[j] = new HashMap<>(randomChromosome(sns.get(j)));
            }
            pop.add(m);
        }
        return pop;
    }

    private HashMap<String, FPG> randomChromosome(StateNode _item) {
        FPG f = new FPG();
        double[] r = minPromMaxValues(_item.getColName());
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

    private double[] minPromMaxValues(String colname) {
        double[] v = new double[3];
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

    public void mutation(Predicate p) {

    }

    public Predicate crossover(Predicate parent1, Predicate parent2) {
        Predicate p = new Predicate();

        return p;
    }

    public void optimize(Table data, Predicate p) {
        this.data = data;
        this.predicatePattern = p;
        genetic();
    }

    private double calculateProm(double[] fitPop) {
        double sum = 0;
        for (double ind : fitPop) {
            sum += ind;
        }
        return sum / fitPop.length;
    }

    private double[] evaluatePredicate(List<HashMap<String, FPG>[]> currentPop) {
        double fitPop[] = new double[currentPop.size()];
        EvaluatePredicate eval;
        for (int i = 0; i < currentPop.size(); i++) {
            HashMap<String, FPG>[] mf = currentPop.get(i);

            for (HashMap<String, FPG> hashMap : mf) {
                hashMap.forEach((k, v) -> {
                    Node node = predicatePattern.getNode(k);
                    if (node instanceof StateNode) {
                        StateNode st = (StateNode) node;
                        st.setMembershipFunction(v);
                    }
                });

            }
            eval = new EvaluatePredicate(predicatePattern, logic, data);
            fitPop[i] = eval.evaluate();

        }
        return fitPop;
    }

    public static void main(String[] args) throws IOException, OperatorException {
        Table d = Table.read().csv("src/main/resources/datasets/tinto.csv");
        GOMF gomf = new GOMF(d, new GMBC(), 0.05, 10, 10, 0.9);
        StateNode fa = new StateNode("high alcohol", "alcohol");
        StateNode va = new StateNode("low pH", "pH");
        StateNode ca = new StateNode("high quality", "quality");
        List<StateNode> states = new ArrayList<>();
        states.add(fa);
        states.add(va);
        states.add(ca);

        GeneratorNode g = new GeneratorNode("*", new NodeType[]{}, new ArrayList<>());
        List<GeneratorNode> gs = new ArrayList<>();

        String expression = "(AND \"high quality\" \"low pH\")";

        ParserPredicate pp = new ParserPredicate(expression, states, gs);
        gomf.optimize(pp.parser());
    }

    private void chromosomePrint(int iteration, double[] fitPop, List<HashMap<String, FPG>[]> currentPop) {
        System.out.println("*****-*****-*****-*****-*****-*****-*****");
        for (int i = 0; i < currentPop.size(); i++) {
            HashMap<String, FPG>[] m = currentPop.get(i);
            String st = "";
            for (HashMap<String, FPG> fmap : m) {
                st += " " + fmap.values().toString();
            }
            System.out.println(iteration + "- Fit: " + fitPop[i] + " - " + st);
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

}
