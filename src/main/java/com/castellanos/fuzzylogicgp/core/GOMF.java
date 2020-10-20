/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.castellanos.fuzzylogicgp.base.Node;
import com.castellanos.fuzzylogicgp.base.NodeTree;
import com.castellanos.fuzzylogicgp.base.NodeType;
import com.castellanos.fuzzylogicgp.base.StateNode;
import com.castellanos.fuzzylogicgp.base.Utils;
import com.castellanos.fuzzylogicgp.logic.Logic;
import com.castellanos.fuzzylogicgp.membershipfunction.FPG;

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

    private final Logic logic;
    private final double mut_percentage;
    private final int adj_num_pop;
    private final int adj_iter;
    private final double adj_truth_value;

    private Table data;

    private NodeTree predicatePattern;
    private List<StateNode> sns;
    private final HashMap<String, Double[]> minPromMaxMapValues;

    private static final Random rand = Utils.random;
    private final ChromosomeComparator chromosomeComparator = new ChromosomeComparator();

    public GOMF(Table data, Logic logic, double mut_percentage, int adj_num_pop, int adj_iter, double adj_truth_value) {
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
        /*
         * predicatePattern.getNodes().forEach((k, v) -> { if
         * (v.getType().equals(NodeType.STATE)) { StateNode c = (StateNode) v; if
         * (c.getMembershipFunction() == null) { sns.add(c); } } });
         */
        NodeTree.getNodesByType(predicatePattern, NodeType.STATE).forEach(v -> {
            if (v instanceof StateNode) {
                StateNode c = (StateNode) v;
                if (c.getMembershipFunction() == null) {
                    sns.add(c);
                }
            }
        });
        if (sns.size() > 0) {
            genetic();
        } else {
            EvaluatePredicate evaluator = new EvaluatePredicate(logic, data);
            evaluator.setPredicate(p);
            p.setFitness(evaluator.evaluate());
        }
    }

    private void genetic() {
        // ChromosomePojo[] currentPop;
        ArrayList<ChromosomePojo> currentPop;
        int iteration = 0;
        currentPop = makePop();
        evaluatePredicate(currentPop);

        // chromosomePrint(iteration, currentPop);
        // BigDecimal truth_value = new BigDecimal(adj_truth_value);
        while (iteration < adj_iter
                && currentPop.get(currentPop.size() - 1).getFitness().compareTo(adj_truth_value) < 0) {
            // while (iteration < adj_iter) {
            iteration++;
            ArrayList<ChromosomePojo> childList = chromosomeCrossover(currentPop);

            // Replace best
            // System.out.println("child crossover: " + childList.size());
            if (childList != null) {
                evaluatePredicate(childList);
                for (int i = 0; i < childList.size(); i++) {
                    ChromosomePojo get = childList.get(i);
                    for (int j = 0; j < currentPop.size(); j++) {
                        ChromosomePojo parent = currentPop.get(i);
                        if (get.getFitness().compareTo(parent.getFitness()) > 0) {
                            // System.out.println("Replace " + j + " > " + get.getFitness() + " - " +
                            // parent.getFitness());
                            currentPop.set(j, get);
                            break;
                        }
                    }

                }
            }
            chromosomeMutation(currentPop);
            evaluatePredicate(currentPop);

            // Arrays.sort(currentPop, chromosomeComparator);
            currentPop.sort(chromosomeComparator);
            // chromosomePrint(iteration, currentPop);
        }

        ChromosomePojo bestFound = currentPop.get(0);
        for (ChromosomePojo chromosomePojo : currentPop) {
            if (chromosomePojo.fitness.compareTo(bestFound.fitness) > 0) {
                bestFound = chromosomePojo;
            }
        }
        // System.out.println("Best solution: " + bestFound.getFitness() + " := " +
        // Arrays.toString(bestFound.getElements()));
        predicatePattern.setFitness(bestFound.getFitness());
        for (HashMap<String, Object> k : bestFound.getElements()) {
            // Node node = predicatePattern.getNode(k.getOwner());
            Node node = predicatePattern.findById(k.get("owner").toString());
            if (node instanceof StateNode) {
                StateNode st = (StateNode) node;

                st.setMembershipFunction(new FPG((double) k.get("beta"), (double) k.get("gamma"), (double) k.get("m")));

                // System.out.println(st);
            }
        }

    }

    @SuppressWarnings("unchecked")
    private ArrayList<ChromosomePojo> makePop() {
        ArrayList<ChromosomePojo> pop = new ArrayList<>();
        for (int i = 0; i < adj_num_pop; i++) {
            HashMap<String, Object>[] m = new HashMap[sns.size()];

            for (int j = 0; j < sns.size(); j++) {
                m[j] = randomChromosome(sns.get(j));
            }
            ChromosomePojo cp = new ChromosomePojo();
            cp.setElements(m);
            pop.add(cp);
        }
        return pop;
    }

    private HashMap<String, Object> randomChromosome(StateNode _item) {
        Double[] r = minPromMaxToleranceValues(_item.getColName());
        HashMap<String, Object> map = new HashMap<>();

        minPromMaxMapValues.put(_item.getId(), r);
        double gamma_double = randomValue(r[0], r[2]);
        double beta;
        do {
            beta = randomValue(r[0], gamma_double);
            if (Math.abs(gamma_double - beta) <= r[3]) {
                gamma_double = randomValue(r[0], r[2]);
            }
        } while (beta >= gamma_double || Math.abs(gamma_double - beta) <= r[3]);
        map.put("owner", _item.getId());
        map.put("gamma", gamma_double);
        map.put("beta", beta);
        map.put("m", rand.nextDouble());
        return map;
    }

    private Double[] minPromMaxToleranceValues(String colname) {
        Double[] v = new Double[4];
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
        v[3] = Math.min(Math.abs(v[0] - v[1]), 0.00006);
        return v;
    }

    private Double randomValue(double min, double max) {
        return (rand.doubles(min, max + 1).findFirst().getAsDouble());
    }

    private void evaluatePredicate(ArrayList<ChromosomePojo> currentPop) {
        // System.out.println(currentPop);
        currentPop.forEach(mf -> {
            ArrayList<StateNode> toclean = new ArrayList<>();
            for (HashMap<String, Object> k : mf.getElements()) {
                Node node = predicatePattern.findById(k.get("owner").toString());
                if (node instanceof StateNode) {
                    StateNode st = (StateNode) node;
                    st.setMembershipFunction(
                            new FPG((double) k.get("beta"), (double) k.get("gamma"), (double) k.get("m")));
                    toclean.add(st);
                }
            }

            EvaluatePredicate evaluator = new EvaluatePredicate(logic, data);
            evaluator.setPredicate(predicatePattern);
            mf.setFitness(evaluator.evaluate());
            toclean.forEach(s -> s.setMembershipFunction(null));

        });

    }

    /*
     * private void chromosomePrint(int iteration, ArrayList<ChromosomePojo>
     * currentPop) {
     * System.out.println("*****-*****-*****-*****-*****-*****-*****"); for (int i =
     * 0; i < currentPop.size(); i++) { ChromosomePojo m = currentPop.get(i); //
     * String st = ""; System.out.println(iteration + "[ " + i + "]" +
     * m.getFitness()); for (HashMap<String, Object> fmap : m.getElements()) { // st
     * += " " + fmap.getFpg().toString(); Node node =
     * predicatePattern.findById(fmap.get("owner").toString()); if (node instanceof
     * StateNode) { StateNode stt = (StateNode) node; stt.setMembershipFunction( new
     * FPG((double) fmap.get("beta"), (double) fmap.get("gamma"), (double)
     * fmap.get("m"))); System.out.println(stt); stt.setMembershipFunction(null); }
     * 
     * }
     * 
     * // System.out.println(iteration + "[" + i + "]- Fit: " + m.getFitness() +
     * " - " // + st); } }
     */

    private void chromosomeMutation(ArrayList<ChromosomePojo> pop) {
        for (int i = 0; i < pop.size(); i++) {
            ChromosomePojo next = pop.get(i);

            if (rand.nextFloat() < mut_percentage && next.getElements().length >= 1) {
                int index = (int) Math.floor(randomValue(0, next.getElements().length - 1).doubleValue());
                // System.out.println("Element <" + i + ">mutated: " + index + " Param : " +
                // indexParam);
                HashMap<String, Object> element = next.getElements()[index];
                Double[] r = minPromMaxMapValues.get(next.getElements()[index].get("owner"));
                Double value;
                int intents;
                switch (index) {
                    case 0:
                        Double gamma = (double) element.get("gamma");
                        intents = 0;
                        do {
                            value = randomValue(r[0], gamma);
                            intents++;

                        } while ((Math.abs(gamma - value) <= r[3] || gamma == value) && intents < 100);
                        element.put("beta", value);
                        break;
                    case 1:
                        Double beta = (double) element.get("beta");
                        intents = 0;
                        do {
                            value = randomValue(beta, r[2]);
                            intents++;

                        } while ((value <= beta || Math.abs(beta - value) <= r[3]) && intents < 100);
                        element.put("gamma", value);
                        break;
                    case 2:
                        element.put("m", rand.nextDouble());
                        break;
                }
                next.getElements()[index] = repair(element);
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
        // System.out.println("Crossover with best parents: " + parentSize + ", other
        // parents : " + parents.length);

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
            // System.out.println("Nothing to do.");
            return null;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
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
            child[childIndex].setElements(new HashMap[p1.elements.length]);
            for (int j = 0; j < p1.elements.length; j++) {
                HashMap<String, Object> p1Map = p1.getElements()[j];
                HashMap<String, Object> p2Map = p2.getElements()[j];
                child[childIndex].getElements()[j] = new HashMap<>();

                HashMap<String, Object> map = new HashMap<>();
                Iterator<String> key = p1Map.keySet().iterator();
                while (key.hasNext()) {
                    String k = key.next();
                    map.put(k, (rand.nextDouble() <= 0.5) ? p1Map.get(k) : p2Map.get(k));
                }

                child[childIndex].getElements()[j] = repair(map);
            }

            childIndex++;
        }
    }

    private HashMap<String, Object> repair(HashMap<String, Object> map) {
        Double[] minMaxPromT = minPromMaxMapValues.get(map.get("owner").toString());
        double beta = (double) map.get("beta"), gamma = (double) map.get("gamma");
        if (Double.isNaN(beta)) {
            beta = randomValue(minMaxPromT[0], gamma);
        }
        if (Double.isNaN(gamma)) {
            gamma = randomValue(beta, minMaxPromT[2]);
        }
        while (Math.abs(beta - gamma) <= minMaxPromT[3] || gamma == beta) {
            gamma = randomValue(minMaxPromT[0], minMaxPromT[2]);
            beta = randomValue(minMaxPromT[0], gamma);
        }
        map.put("beta", beta);
        map.put("gamma", gamma);
        return map;
    }

    private class ChromosomePojo {

        protected Double fitness;
        protected HashMap<String, Object>[] elements;

        /**
         * @param elements the elements to set
         */
        public void setElements(HashMap<String, Object>[] elements) {
            this.elements = elements;
        }

        /**
         * @param fitness the fitness to set
         */
        public void setFitness(Double fitness) {
            this.fitness = fitness;
        }

        public HashMap<String, Object>[] getElements() {
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
}
