package com.castellanos.fuzzylogicgp.core;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

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
        Predicate[] population = makePopulation();
        GOMF gomf = new GOMF(data, logic, mut_percentage, adj_num_pop, adj_num_iter, adj_min_truth_value);
        for (int i = 0; i < population.length; i++) {
            gomf.optimize(population[i]);
        }
        Arrays.sort(population);
        for (int i = 0; i < population.length; i++) {
            System.out.println(i + " " + population[i] + " " + population[i].getFitness());
            /*
             * Iterator<String> k =population[i].getNodes().keys().asIterator();
             * while(k.hasNext()){ Node n = population[i].getNode(k.next()); if(n instanceof
             * StateNode){ System.out.println(((StateNode) n)); } }
             */
        }
        
        int iteration = 1;
        // TODO: falta seleccion y cruza
        BigDecimal truth_value = new BigDecimal(min_truth_value);
        while (iteration < num_iter && population[population.length - 1].getFitness().compareTo(truth_value) < 0) {

            Predicate[] offspring = new Predicate[population.length / 2];
            TournamentSelection tournamentSelection = new TournamentSelection(population, population.length / 2);
            tournamentSelection.execute();
            System.out.println("Selection");
            for (Predicate predicate : tournamentSelection.getAll()) {
                System.out.println(predicate);
            }
            for (int i = 0; i < offspring.length; i++) {
                Predicate a = tournamentSelection.getNext();
                Predicate b = tournamentSelection.getNext();
                offspring[i] = crossover(a, b);
                offspring[i++] = crossover(b, a);
            }
            mutation(offspring);
            for (int i = 0; i < offspring.length; i++) {
                gomf.optimize(offspring[i]);
            }
            for (int i = 0; i < offspring.length; i++) {
                for (int j = 0; j < population.length; j++) {
                    if (offspring[i].getFitness().compareTo(population[j].getFitness()) > 0) {
                        population[j] = (Predicate) offspring[i].clone();
                        break;
                    }
                }
            }
            Arrays.sort(population);
            System.out.println(iteration + " " + population[population.length - 1] + " "
                    + population[population.length - 1].getFitness());
            iteration++;
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
            StateNode s = null;
            try {
                s = (StateNode) select.clone();
            } catch (CloneNotSupportedException e) {
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
        for (int i = 0; i < population.length; i++) {
            if (rand.nextDouble() < mut_percentage) {
                ConcurrentMap<String, Node> editableNode = population[i].getNodes().entrySet().stream()
                        .filter(n -> n.getValue().isEditable())
                        .collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue));
                String[] keys = editableNode.keySet().toArray(new String[editableNode.size()]);

                String k = keys[rand.nextInt(keys.length)];
                Node n = population[i].getNode(k);
                int intents = 0;
                while(n.getType() == NodeType.NOT && intents < keys.length){
                    k = keys[rand.nextInt(keys.length)];
                    n = population[i].getNode(k);
                    intents++;
                }
                switch (n.getType()) {
                    case OR:
                        n.setType(NodeType.AND);
                        population[i].getNodes().put(k, n);
                        break;
                    case AND:
                        n.setType(NodeType.OR);
                        population[i].getNodes().put(k, n);
                        break;
                    case IMP:
                        n.setType(NodeType.EQV);
                        population[i].getNodes().put(k, n);
                        break;
                    case EQV:
                        n.setType(NodeType.IMP);
                        population[i].getNodes().put(k, n);
                    case STATE:
                        List<StateNode> ls = statesByGenerators.get(n.getByGenerator());
                        StateNode state = ls.get(rand.nextInt(ls.size()));
                        StateNode s = (StateNode) n;
                        s.setColName(state.getColName());
                        s.setLabel(state.getLabel());
                        if (s.getMembershipFunction() != null) {
                            s.setMembershipFunction(s.getMembershipFunction());
                        }
                        population[i].getNodes().put(k, s);

                    default:
                        break;
                }

            }
        }
    }

    private Predicate crossover(Predicate a, Predicate b) {
        Predicate child = new Predicate();

        return child;
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

        GeneratorNode g = new GeneratorNode("first_nivel",
                new NodeType[] { NodeType.AND, NodeType.OR, NodeType.NOT, NodeType.IMP }, vars);
        List<GeneratorNode> gs = new ArrayList<>();
        gs.add(g);
        String expression = "(NOT (AND \"*\" \"quality\") )";
        expression = "(OR \"*\" \"quality\")";
        expression = "(NOT \"*\")";
        expression = "\"*\"";
        expression = "(IMP \"first_nivel\" \"quality\")";

        ParserPredicate pp = new ParserPredicate(expression, states, gs);

        KDFLC discovery = new KDFLC(pp, new GMBC(), 2, 10, 20, 10, 1f, 0.1, 2, 1, 0.0, d);
        /*
         * Predicate p = pp.parser(); p.getNodes().forEach((k,v)->{
         * System.out.println(v+", father = "+v.getFather()+" , level: "+p.dfs(v)); });
         */
        long startTime = System.nanoTime();
        discovery.execute();

        long endTime = System.nanoTime();

        long duration = (endTime - startTime); // divide by 1000000 to get milliseconds.
        System.out.println("That took " + (duration / 1000000) + " milliseconds");
    }

}