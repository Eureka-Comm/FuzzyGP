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
    private final int adj_num_pop;
    private final int adj_iter;
    private final double adj_truth_value;
    
    private Table data;
    
    private Predicate predicatePattern;
    private List<StateNode> sns;
    private final HashMap<String, Double[]> minPromMaxMapValues;
    
    private static final Random rand = new Random();
    private final Gson print = new GsonBuilder().setPrettyPrinting().create();
    private final ChromosomeComparator chromosomeComparator = new ChromosomeComparator();
    private final EvaluatePredicate evaluator;
    
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
            List<ChromosomePojo> childList = Arrays.asList(chromosomeCrossover(currentPop));
            //Replace best
            evaluatePredicate(childList);
            for (int i = 0; i < childList.size(); i++) {
                ChromosomePojo get = childList.get(i);
                ChromosomePojo parent = currentPop.get(i);
                if(get.fitness>parent.fitness){
                    System.out.println("Replace "+i+" > "+get.fitness+" - "+parent.fitness);
                    currentPop.set(i, get);
                }
                
            }
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
        GOMF gomf = new GOMF(d, new GMBC(), 0.05, 10, 100, (double)1.0);
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
                System.out.println("Element <" + pop.indexOf(next) + ">mutated: " + index + " Param : " + indexParam);
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
    
    private ChromosomePojo[] chromosomeCrossover(List<ChromosomePojo> pop) {
        int parentSize = (adj_num_pop < 10) ? (adj_num_pop < 2) ? adj_num_pop : 2 : adj_num_pop / 5;
        System.out.println("Crossover with best parents: " + parentSize);
        ChromosomePojo[] bestParents = new ChromosomePojo[parentSize];
        ChromosomePojo[] child = new ChromosomePojo[(parentSize % 2 == 0) ? parentSize / 2 : (parentSize + 1) / 2];
        for (int i = 0; i < bestParents.length; i++) {
            bestParents[i] = pop.get(pop.size() - i - 1);
            
        }
        for (int i = 0; i < child.length; i++) {
            child[i] = new ChromosomePojo();
        }
        int childIndex = 0;
        if (bestParents.length != 1) {
            for (int i = 0; i < bestParents.length; i++) {
                ChromosomePojo p1 = bestParents[i];
                ChromosomePojo p2;
                if (i + 1 < bestParents.length) {
                    p2 = bestParents[i++];
                } else {
                    p2 = bestParents[0];
                }
                child[childIndex].elements = new HashMap[p1.elements.length];
                for (int j = 0; j < p1.elements.length; j++) {
                    HashMap<String, FPG> p1Map = p1.elements[j];
                    HashMap<String, FPG> p2Map = p2.elements[j];
                    Iterator<String> iterator = p1Map.keySet().iterator();
                    child[childIndex].elements[j] = new HashMap<>();
                    while (iterator.hasNext()) {
                        String next = iterator.next();
                        FPG fp1 = p1Map.get(next);
                        FPG fp2 = p2Map.get(next);
                        FPG childFpg = null;
                        int select = (int) Math.floor(randomValue(0, 2));
                        switch (select) {
                            case 0:
                                if (fp2.getGamma() > fp1.getBeta()) {
                                    childFpg = new FPG(fp2.getGamma(), fp1.getBeta(), fp1.getM());
                                } else {
                                    childFpg = new FPG(fp1.getBeta(), fp2.getGamma(), fp1.getM());
                                }
                                break;
                            case 1:
                                if (fp2.getBeta() < fp1.getGamma()) {
                                    childFpg = new FPG(fp1.getGamma(), fp2.getBeta(), fp1.getM());
                                } else {
                                    childFpg = new FPG(fp2.getBeta(), fp1.getGamma(), fp1.getM());
                                }
                                break;
                            case 2:
                                childFpg = new FPG(fp1.getGamma(), fp1.getBeta(), fp2.getM());
                                break;
                        }
                        child[childIndex].elements[j].put(next, childFpg);
                    }
                }
                childIndex++;
            }
            return child;
        } else if (bestParents.length == 1) {
            System.out.println("Nothing to do.");
            return null;
        }
        return null;
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
