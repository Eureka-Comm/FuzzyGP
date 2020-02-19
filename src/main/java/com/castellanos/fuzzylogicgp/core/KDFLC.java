package com.castellanos.fuzzylogicgp.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.castellanos.fuzzylogicgp.base.GeneratorNode;
import com.castellanos.fuzzylogicgp.base.Node;
import com.castellanos.fuzzylogicgp.base.NodeType;
import com.castellanos.fuzzylogicgp.base.OperatorException;
import com.castellanos.fuzzylogicgp.base.OperatorNode;
import com.castellanos.fuzzylogicgp.base.Predicate;
import com.castellanos.fuzzylogicgp.base.StateNode;
import com.castellanos.fuzzylogicgp.logic.ALogic;
import com.castellanos.fuzzylogicgp.logic.GMBC;
import com.castellanos.fuzzylogicgp.membershipfunction.AMembershipFunction;
import com.castellanos.fuzzylogicgp.parser.ParserPredicate;

import tech.tablesaw.api.Table;

/**
 * fuzzy compensatory logical knowledge discovery
 * 
 * @author Castellanos Alvarez, Alejandro.
 * @since Oct, 19.
 * @version 0.0.1
 */
public class KDFLC {
    private ParserPredicate parserPredicate;
    private Predicate predicatePattern;
    private HashMap<String, List<StateNode>> statesByGenerators;
    private ALogic logic;
    private int depth;
    private int num_pop;
    private int num_iter;
    private int num_result;
    private double min_truth_value;
    private double mut_percentage;

    // GOMF Params
    private int adj_num_pop;
    private int adj_num_iter;
    private double adj_min_truth_value;

    // Aux
    private static final Random rand = new Random();
    private Table data;

    public KDFLC(ParserPredicate pp, ALogic logic, int depth, int num_pop, int num_iter, int num_result,
            double min_truth_value, double mut_percentage, int adj_num_pop, int adj_num_iter,
            double adj_min_truth_value, Table data) throws OperatorException, CloneNotSupportedException {
        this.parserPredicate = pp;
        this.predicatePattern = pp.parser();
        this.logic = logic;
        this.depth = depth;
        this.num_pop = num_pop;
        this.num_iter = num_iter;
        this.num_result = num_result;
        this.min_truth_value = min_truth_value;
        this.mut_percentage = mut_percentage;
        this.adj_num_pop = adj_num_pop;
        this.adj_num_iter = adj_num_iter;
        this.adj_min_truth_value = adj_min_truth_value;
        this.data = data;
    }

    public void execute() throws CloneNotSupportedException {
        statesByGenerators = new HashMap<>();
        Iterator<Node> iterator = predicatePattern.getNodes().values().iterator();
        while (iterator.hasNext()) {
            Node node = iterator.next();
            if (node instanceof GeneratorNode) {
                GeneratorNode gNode = (GeneratorNode) node;
                List<StateNode> states = new ArrayList<>();
                for (String var : gNode.getVariables()) {
                    for (StateNode s : parserPredicate.getStates()) {
                        if (s.getLabel().equals(var)) {
                            // states.add(new StateNode(s.getLabel(), s.getColName(),
                            // s.getMembershipFunction()));
                            StateNode ss = (StateNode) s.clone();
                            ss.setByGenerator(gNode.getId());
                            states.add(ss);
                            break;
                        }
                    }
                }
                statesByGenerators.put(gNode.getId(), states);
            }
        }
        /*
         * predicatePattern.getNodes().forEach((k, v) -> { if (v instanceof
         * GeneratorNode) { GeneratorNode gNode = (GeneratorNode) v; List<StateNode>
         * states = new ArrayList<>(); for (String var : gNode.getVariables()) { for
         * (StateNode s : parserPredicate.getStates()) { if (s.getLabel().equals(var)) {
         * states.add(new StateNode(s.getLabel(), s.getColName(),
         * s.getMembershipFunction())); break; } } }
         * statesByGenerators.put(gNode.getId(), states); } });
         */
        Predicate[] population = makePopulation();
        GOMF gomf = new GOMF(data, logic, mut_percentage, adj_num_pop, adj_num_iter, adj_min_truth_value);

       /* for (int i = 0; i < population.length; i++) {
            Predicate current_predicate = population[i];
            System.out.println("Optimizando predicando");
            gomf.optimize(current_predicate);
        }*/
        mutation(population);
        System.out.println("After mutation");
        for (int i = 0; i < population.length; i++) {
            System.out.println((i+1)+": "+population[i]);
        }

    }

    private Predicate[] makePopulation() {
        Predicate[] pop = new Predicate[num_pop];
        for (int i = 0; i < pop.length; i++) {
            pop[i] = createRandomInd();
            System.out.printf("ind %3d: %s\n", i + 1, pop[i]);
        }
        return pop;
    }

    private Predicate createRandomInd() {
        Predicate p = new Predicate(predicatePattern);
        Iterator<Node> iterator = p.getNodes().values().iterator();
        while (iterator.hasNext()) {
            Node node = iterator.next();
            if (node instanceof GeneratorNode) {
                try {
                    complete_tree(p, (GeneratorNode) node, null, -1, p.dfs(node));
                } catch (OperatorException e) {
                    e.printStackTrace();
                }

            }
        }
        /*
         * subTrees.forEach((k, v) -> { p.remove(p.getNode(k)); v.forEach((s, n) -> {
         * try { p.addNode(p.getNode(s), n); } catch (OperatorException e) { // TODO
         * Auto-generated catch block e.printStackTrace(); } }); });
         */

        return p;
    }

    private void complete_tree(Predicate p, GeneratorNode gNode, Node father, int arity, int currentDepth)
            throws OperatorException {
        boolean isToReplace = false;
        if (father == null) {
            if (gNode.getFather() != null)
                father = p.getNode(gNode.getFather());
            isToReplace = true;
        }
        if (currentDepth == depth) {
            int size = statesByGenerators.get(gNode.getId()).size();
            StateNode select = statesByGenerators.get(gNode.getId()).get(rand.nextInt(size));
            if (size >= 2 && father != null) {
                List<Node> childs = p.searchChilds(father);
                while (childs.toString().contains(select.toString())) {
                    select = statesByGenerators.get(gNode.getId()).get(rand.nextInt(size));
                }
            }
            // StateNode s = new StateNode(select.getLabel(), select.getColName(),
            // select.getMembershipFunction());
            StateNode s = null;
            try {
                s = (StateNode) select.clone();
            } catch (CloneNotSupportedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // s.setFather(father.getId());
            s.setEditable(true);
            
            if (isToReplace) {
                p.replace(gNode, s);
            } else {
                p.addNode(father, s);
            }
        } else {

            arity = rand.nextInt(gNode.getVariables().size());

            Node newFather;
            NodeType nType = gNode.getOperators()[rand.nextInt(gNode.getOperators().length)];
            switch (nType) {
            case AND:
                newFather = new OperatorNode(NodeType.AND);
                if (arity < 2) {
                    arity = 2;
                }
                break;
            case OR:
                newFather = new OperatorNode(NodeType.OR);
                if (arity < 2)
                    arity = 2;
                break;
            case IMP:
                newFather = new OperatorNode(NodeType.IMP);
                arity = 2;
                break;
            case EQV:
                newFather = new OperatorNode(NodeType.EQV);

                arity = 2;
                break;
            case NOT:
                newFather = new OperatorNode(NodeType.NOT);

                arity = 1;
                break;
            default:
                newFather = null;
            }
            newFather.setEditable(true);
            newFather.setByGenerator(gNode.getId());
            if (father != null && father.getId() != null)
                newFather.setFather(father.getId());
            if (isToReplace)
                p.replace(gNode, newFather);
            else
                p.addNode(father, newFather);
            for (int i = 0; i < arity; i++)
                complete_tree(p, gNode, newFather, arity, currentDepth + 1);
        }

    }

    private void mutation(Predicate[] population) {
        for (Predicate predicate : population) {
            predicate.getNodes().forEachEntry(50, (entry)->{
                Node v = entry.getValue();
                
            });
            Iterator<String> iterator = predicate.getNodes().keySet().iterator();
            while(iterator.hasNext()){
                String id = iterator.next();
                Node v = predicate.getNode(id);
                if (v.isEditable() && rand.nextDouble() <= mut_percentage) {
                    switch (v.getType()) {
                    case OR:
                        v.setType(NodeType.AND);
                        break;
                    case AND:
                        v.setType(NodeType.OR);
                        break;
                    case IMP:
                        v.setType(NodeType.EQV);
                        break;
                    case EQV:
                        v.setType(NodeType.IMP);
                    case STATE:
                        List<StateNode> ls = statesByGenerators.get(v.getByGenerator());
                        StateNode ns = ls.get(rand.nextInt(ls.size()));
                        StateNode state = (StateNode) v;
                        state.setColName(ns.getColName());
                        state.setLabel(ns.getLabel());
                        if (ns.getMembershipFunction() != null) {
                            try {
                                state.setMembershipFunction((AMembershipFunction) ns.getMembershipFunction().clone());
                            } catch (CloneNotSupportedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                        break;                        
                    }
                    predicate.getNodes().computeIfPresent(id, (k,vv)->v);
                }

            }
        }
    }

    private Predicate[] crossover(Predicate[] population) {
        Predicate[] childs = new Predicate[(population.length % 2 == 0) ? population.length / 2
                : population.length / 2 + 1];

        return childs;
    }

    private void evaluationChromosome(Predicate[] population) {
    }

    public static void main(String[] args) throws IOException, OperatorException, CloneNotSupportedException {
        Table d = Table.read().csv("src/main/resources/datasets/tinto.csv");
        StateNode sa = new StateNode("alcohol", "alcohol");
        StateNode sph = new StateNode("pH", "pH");
        StateNode sq = new StateNode("quality", "quality");
        StateNode sfa = new StateNode("fixed_acidity", "fixed_acidity");
        StateNode sugar = new StateNode("residual_sugar", "residual_sugar");
        StateNode volatile_acidity = new StateNode("volatile_acidity", "volatile_acidity");
        StateNode citric_acid = new StateNode("citric_acid", "citric_acid");
        StateNode chlorides = new StateNode("chlorides", "chlorides");
        StateNode free_sulfur_dioxide = new StateNode("free_sulfur_dioxide", "free_sulfur_dioxide");
        StateNode total_sulfur_dioxide = new StateNode("total_sulfur_dioxide", "total_sulfur_dioxide");
        StateNode density = new StateNode("density", "density");

        List<StateNode> states = new ArrayList<>();
        states.add(density);
        states.add(volatile_acidity);
        states.add(citric_acid);
        states.add(chlorides);
        states.add(free_sulfur_dioxide);
        states.add(total_sulfur_dioxide);
        states.add(sa);
        states.add(sph);
        states.add(sq);
        states.add(sfa);
        states.add(sugar);
        List<String> vars = new ArrayList<>();
        vars.add("alcohol");
        vars.add("pH");
        vars.add("fixed_acidity");
        vars.add("sugar");
        vars.add("density");
        vars.add("total_sulfur_dioxide");
        vars.add("chlorides");
        vars.add("free_sulfur_dioxide");
        vars.add("citric_acid");
        vars.add("volatile_acidity");
        // vars.add("quality");

        GeneratorNode g = new GeneratorNode("*",
                new NodeType[] { NodeType.AND, NodeType.OR, NodeType.NOT, NodeType.IMP }, vars);
        List<GeneratorNode> gs = new ArrayList<>();
        gs.add(g);
        String expression = "(NOT (AND \"*\" \"quality\") )";
        expression = "(OR \"*\" \"quality\")";
        expression = "(NOT \"*\")";
        expression = "\"*\"";
        expression = "(IMP \"*\" \"quality\")";

        ParserPredicate pp = new ParserPredicate(expression, states, gs);

        KDFLC discovery = new KDFLC(pp, new GMBC(), 2, 5, 20, 10, 0.85, 0.05, 2, 1, 0.0, d);
        /*
         * Predicate p = pp.parser(); p.getNodes().forEach((k,v)->{
         * System.out.println(v+", father = "+v.getFather()+" , level: "+p.dfs(v)); });
         */
        long startTime = System.nanoTime();
        discovery.execute();

        long endTime = System.nanoTime();

        long duration = (endTime - startTime); // divide by 1000000 to get milliseconds.
        System.out.println("That took " + (duration/1000000) + " milliseconds");
    }

}