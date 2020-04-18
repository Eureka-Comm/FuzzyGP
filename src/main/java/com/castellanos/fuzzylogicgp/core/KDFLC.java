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
import java.util.stream.Stream;

import com.castellanos.fuzzylogicgp.base.GeneratorNode;
import com.castellanos.fuzzylogicgp.base.Node;
import com.castellanos.fuzzylogicgp.base.NodeTree;
import com.castellanos.fuzzylogicgp.base.NodeType;
import com.castellanos.fuzzylogicgp.base.OperatorException;
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
    private NodeTree predicatePattern;
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

    public void execute() throws CloneNotSupportedException, OperatorException {
        statesByGenerators = new HashMap<>();
        // Iterator<Node> iterator = predicatePattern.getNodes().values().iterator();
        Iterator<Node> iterator = NodeTree.getNodesByType(predicatePattern, NodeType.OPERATOR).iterator();
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
        NodeTree[] population = makePopulation();
        GOMF gomf = new GOMF(data, logic, mut_percentage, adj_num_pop, adj_num_iter, adj_min_truth_value);
        for (int i = 0; i < population.length; i++) {
            gomf.optimize(population[i]);
        }
        Arrays.sort(population);
        for (int i = 0; i < population.length; i++) {
            System.out.println(i + " " + population[i] + " " + population[i].getFitness());
        }
        System.out.println("mutate");

        mutation(population);
        for (int i = 0; i < population.length; i++) {
            gomf.optimize(population[i]);
        }
       // Arrays.sort(population);

        for (int i = 0; i < population.length; i++) {
            System.out.println(i + " " + population[i] + " " + population[i].getFitness());
        }
        int iteration = 1;
        // TODO: falta seleccion y cruza
        // BigDecimal truth_value = new BigDecimal(min_truth_value);

        /*
         * while (iteration < num_iter && population[population.length -
         * 1].getFitness().compareTo(min_truth_value) < 0) {
         * 
         * NodeTree[] offspring = new NodeTree[population.length / 2];
         * TournamentSelection tournamentSelection = new TournamentSelection(population,
         * population.length / 2); tournamentSelection.execute();
         * System.out.println("Selection"); for (NodeTree predicate :
         * tournamentSelection.getAll()) { System.out.println(predicate); }
         * System.out.println("Offspring"); for (int i = 0; i < offspring.length; i++) {
         * NodeTree a = tournamentSelection.getNext(); NodeTree b =
         * tournamentSelection.getNext(); offspring[i] = crossover(a, b);
         * System.out.println(offspring[i]); offspring[i++] = crossover(b, a);
         * System.out.println(offspring[i]); } System.out.println("mutate");
         * mutation(offspring); for (int i = 0; i < offspring.length; i++) {
         * gomf.optimize(offspring[i]); } for (int i = 0; i < offspring.length; i++) {
         * for (int j = 0; j < population.length; j++) { if
         * (offspring[i].getFitness().compareTo(population[j].getFitness()) > 0) {
         * population[j] = (NodeTree) offspring[i].clone(); break; } } }
         * Arrays.sort(population); System.out.println(iteration + " " +
         * population[population.length - 1] + " " + population[population.length -
         * 1].getFitness()); iteration++; }
         */

    }

    private NodeTree[] makePopulation() {
        NodeTree[] pop = new NodeTree[num_pop];
        for (int i = 0; i < pop.length; i++) {
            pop[i] = createRandomInd();
            System.out.printf("ind %3d: %s\n", i + 1, pop[i]);
        }
        return pop;
    }

    private NodeTree createRandomInd() {
        NodeTree p = null;
        try {
            p = (NodeTree) predicatePattern.clone();
        } catch (CloneNotSupportedException e1) {
            e1.printStackTrace();
        }
        // Iterator<Node> iterator = p.getNodes().values().iterator();
        Iterator<Node> iterator = NodeTree.getNodesByType(predicatePattern, NodeType.OPERATOR).iterator();
        while (iterator.hasNext()) {
            Node node = iterator.next();
            if (node instanceof GeneratorNode) {
                try {
                    if (rand.nextDouble() < 0.5)
                        complete_tree(p, (GeneratorNode) node, null, -1, NodeTree.dfs(p, node));
                    else
                        growTree(p, (GeneratorNode) node, null, -1, NodeTree.dfs(p, node));
                } catch (OperatorException e) {
                    e.printStackTrace();
                }

            }
        }

        return p;
    }

    private void complete_tree(NodeTree p, GeneratorNode gNode, Node father, int arity, int currentDepth)
            throws OperatorException {
        boolean isToReplace = false;
        if (father == null) {
            NodeTree find = NodeTree.getNodeParent(p, gNode.getId());
            if (find != null) {
                father = find;
                isToReplace = true;
            }
        }
        if (currentDepth >= depth) {
            int size = statesByGenerators.get(gNode.getId()).size();
            StateNode select = statesByGenerators.get(gNode.getId()).get(rand.nextInt(size));
            if (size >= 2 && father != null) {
                ArrayList<Node> childs = p.getChildrens();
                while (childs.toString().contains(select.toString())) {
                    select = statesByGenerators.get(gNode.getId()).get(rand.nextInt(size));
                }
            }
            StateNode s = null;
            try {
                s = (StateNode) select.clone();

                s.setEditable(true);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            // s.setFather(father.getId());
            if (isToReplace) {
                NodeTree.replace(((NodeTree) father), gNode, s);
            } else {
                ((NodeTree) father).addChild(s);
            }
        } else {

            arity = rand.nextInt(gNode.getVariables().size() / 2);
            if (arity < 2) {
                arity = 2;
            }

            Node newFather;
            NodeType nType = gNode.getOperators()[rand.nextInt(gNode.getOperators().length)];
            switch (nType) {
                case AND:
                    newFather = new NodeTree(NodeType.AND);
                    break;
                case OR:
                    newFather = new NodeTree(NodeType.OR);
                    break;
                case IMP:
                    newFather = new NodeTree(NodeType.IMP);
                    arity = 2;
                    break;
                case EQV:
                    newFather = new NodeTree(NodeType.EQV);
                    arity = 2;
                    break;
                case NOT:
                    newFather = new NodeTree(NodeType.NOT);
                    arity = 1;
                    break;
                default:
                    newFather = null;
            }
            newFather.setEditable(true);
            newFather.setByGenerator(gNode.getId());

            if (isToReplace)
                NodeTree.replace(p, gNode, newFather);
            else
                ((NodeTree) father).addChild(newFather);
            for (int i = 0; i < arity; i++)
                complete_tree(p, gNode, newFather, arity, currentDepth + 1);
        }
    }

    private void growTree(NodeTree p, GeneratorNode gNode, Node father, int arity, int currentDepth)
            throws OperatorException {
        boolean isToReplace = false;
        if (father == null) {
            NodeTree find = NodeTree.getNodeParent(p, gNode.getId());
            if (find != null) {
                father = find;
                isToReplace = true;
            }
        }
        if (currentDepth >= depth || rand.nextDouble() < 0.65) {
            int size = statesByGenerators.get(gNode.getId()).size();
            StateNode select = statesByGenerators.get(gNode.getId()).get(rand.nextInt(size));
            if (size >= 2 && father != null) {
                ArrayList<Node> childs = p.getChildrens();
                while (childs.toString().contains(select.toString())) {
                    select = statesByGenerators.get(gNode.getId()).get(rand.nextInt(size));
                }
            }
            StateNode s = null;
            try {
                s = (StateNode) select.clone();
                s.setEditable(true);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            // s.setFather(father.getId());

            if (isToReplace) {
                NodeTree.replace(((NodeTree) father), gNode, s);
            } else {
                ((NodeTree) father).addChild(s);
            }
        } else {

            arity = rand.nextInt(gNode.getVariables().size() / 2);
            if (arity < 2) {
                arity = 2;
            }

            Node newFather;
            NodeType nType = gNode.getOperators()[rand.nextInt(gNode.getOperators().length)];
            switch (nType) {
                case AND:
                    newFather = new NodeTree(NodeType.AND);
                    break;
                case OR:
                    newFather = new NodeTree(NodeType.OR);
                    break;
                case IMP:
                    newFather = new NodeTree(NodeType.IMP);
                    arity = 2;
                    break;
                case EQV:
                    newFather = new NodeTree(NodeType.EQV);
                    arity = 2;
                    break;
                case NOT:
                    newFather = new NodeTree(NodeType.NOT);
                    arity = 1;
                    break;
                default:
                    newFather = null;
            }
            newFather.setEditable(true);
            newFather.setByGenerator(gNode.getId());

            if (isToReplace)
                NodeTree.replace(p, gNode, newFather);
            else
                ((NodeTree) father).addChild(newFather);
            for (int i = 0; i < arity; i++)
                growTree(p, gNode, newFather, arity, currentDepth + 1);
        }
    }

    private void mutation(NodeTree[] population) throws OperatorException, CloneNotSupportedException {
        for (int i = 0; i < population.length; i++) {
            if (rand.nextDouble() < mut_percentage) {
                List<Node> editableNode = NodeTree.getNodesByType(population[i], null);
                Iterator <Node> iter = editableNode.iterator();
                while(iter.hasNext()){
                    Node iNode = iter.next();
                    if(!iNode.isEditable()){
                        iter.remove();
                    }
                }
                Node n = editableNode.get(rand.nextInt(editableNode.size()));

                int intents = 0;
                while (n.getType() == NodeType.NOT && intents < editableNode.size()) {
                    n = editableNode.get(rand.nextInt(editableNode.size()));
                    intents++;
                }
                Node clon = (Node) n.clone();
                switch (n.getType()) {
                    case OR:
                        clon.setType(NodeType.AND);
                        NodeTree.replace(NodeTree.getNodeParent(population[i], n.getId()), n, clon);
                        break;
                    case AND:
                        clon.setType(NodeType.OR);
                        NodeTree.replace(NodeTree.getNodeParent(population[i], n.getId()), n, clon);
                        break;
                    case IMP:
                        clon.setType(NodeType.EQV);
                        NodeTree.replace(NodeTree.getNodeParent(population[i], n.getId()), n, clon);
                        break;
                    case EQV:
                        clon.setType(NodeType.IMP);
                        NodeTree.replace(NodeTree.getNodeParent(population[i], n.getId()), n, clon);
                    case STATE:
                        List<StateNode> ls = statesByGenerators.get(n.getByGenerator());
                        StateNode state = ls.get(rand.nextInt(ls.size()));
                        StateNode s = (StateNode) clon;
                        s.setColName(state.getColName());
                        s.setLabel(state.getLabel());
                        if (s.getMembershipFunction() != null) {
                            s.setMembershipFunction(s.getMembershipFunction());
                        }
                        NodeTree.replace(NodeTree.getNodeParent(population[i], n.getId()), n, clon);

                    default:
                        break;
                }

            }
        }
    }
    /*
     * 
     * 
     * private Predicate crossover(Predicate a, Predicate b) { Predicate child =
     * null;
     * 
     * ConcurrentMap<String, Node> a_en = a.getNodes().entrySet().stream().filter(n
     * -> n.getValue().isEditable())
     * .collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue));
     * ConcurrentMap<String, Node> b_en = b.getNodes().entrySet().stream().filter(n
     * -> n.getValue().isEditable())
     * .collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue));
     * if (a_en.size() <= 2 || b_en.size() <= 2) { if
     * (a.getFitness().compareTo(b.getFitness()) > 0) { try { child = (Predicate)
     * a.clone(); } catch (CloneNotSupportedException e) { e.printStackTrace(); } }
     * else { try { child = (Predicate) b.clone(); } catch
     * (CloneNotSupportedException e) { e.printStackTrace(); } } return child; } try
     * { child = (Predicate) a.clone(); } catch (CloneNotSupportedException e) {
     * e.printStackTrace(); } String[] ak = a_en.keySet().toArray(new
     * String[a_en.size()]); String bk[] = b_en.keySet().toArray(new
     * String[b_en.size()]); int intents = 0; String akey, bkey = null; boolean flag
     * = false; do { intents++; akey = ak[rand.nextInt(ak.length)]; int a_deep =
     * child.dfs(child.getNode(akey)); for (int i = 0; i < bk.length; i++) { bkey =
     * bk[rand.nextInt(bk.length)]; if (a_deep <= b.dfs(b.getNode(bkey))) { flag =
     * true; break; } } } while (intents < ak.length && !flag); if (bkey != null &&
     * flag) { Node a_sub = child.getNode(akey); Node b_sub = b.getNode(bkey); if
     * (a_sub.getFather() != null) { child.remove(a_sub); Node subtree; try {
     * subtree = (Node) b_sub.clone();
     * child.addNode(child.getNode(a_sub.getFather()), subtree); if (b_sub
     * instanceof OperatorNode) { connectSubTree(child, subtree, b, b_sub); } return
     * child; } catch (CloneNotSupportedException | OperatorException e) {
     * e.printStackTrace(); }
     * 
     * } else { Predicate p = new Predicate(); Node subtree; try { subtree = (Node)
     * b_sub.clone(); p.addNode(null, subtree); if (b_sub instanceof OperatorNode) {
     * connectSubTree(p, subtree, b, b_sub); } return p; } catch
     * (CloneNotSupportedException | OperatorException e) { e.printStackTrace(); }
     * 
     * }
     * 
     * }
     * 
     * return child; }
     * 
     * private void connectSubTree(Predicate child, Node fatherFRomChild, Predicate
     * b, Node subTreeFromB) throws OperatorException, CloneNotSupportedException {
     * if (subTreeFromB instanceof OperatorNode) { if (subTreeFromB.getType() ==
     * NodeType.IMP) { Node[] childs = b.findChild(subTreeFromB); for (int i = 0; i
     * < childs.length; i++) { Node father = (Node) childs[i].clone();
     * child.addNode(fatherFRomChild, father); connectSubTree(child, father, b,
     * childs[i]); } } else { List<Node> childs = b.searchChilds(subTreeFromB); for
     * (int i = 0; i < childs.size(); i++) { Node father = (Node)
     * childs.get(i).clone(); child.addNode(fatherFRomChild, father);
     * connectSubTree(child, father, b, childs.get(i)); } } } else {
     * child.addNode(fatherFRomChild, (Node) subTreeFromB.clone()); }
     * 
     * }
     */

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
        // expression = "(NOT \"*\")";
        // expression = "\"*\"";
        // expression = "(IMP \"first_nivel\" \"quality\")";

        ParserPredicate pp = new ParserPredicate(expression, states, gs);

        KDFLC discovery = new KDFLC(pp, new GMBC(), 2, 20, 20, 10, 1f, 0.1, 2, 1, 0.0, d);
        // new KDFLC(pp, logic, depth, num_pop, num_iter, num_result, min_truth_value,
        // mut_percentage, adj_num_pop, adj_num_iter, adj_min_truth_value, data)
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