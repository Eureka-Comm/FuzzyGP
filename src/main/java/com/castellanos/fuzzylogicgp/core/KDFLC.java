package com.castellanos.fuzzylogicgp.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.castellanos.fuzzylogicgp.base.GeneratorNode;
import com.castellanos.fuzzylogicgp.base.Node;
import com.castellanos.fuzzylogicgp.base.NodeTree;
import com.castellanos.fuzzylogicgp.base.NodeType;
import com.castellanos.fuzzylogicgp.base.OperatorException;
import com.castellanos.fuzzylogicgp.base.StateNode;
import com.castellanos.fuzzylogicgp.base.TournamentSelection;
import com.castellanos.fuzzylogicgp.logic.ALogic;
import com.castellanos.fuzzylogicgp.parser.ParserPredicate;

import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
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
    private ArrayList<NodeTree> resultList;

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
        this.resultList = new ArrayList<>();
        this.min_truth_value = (min_truth_value <= 1) ? (min_truth_value > 0.0005) ? min_truth_value - 0.005 : 0.0
                : 0.5;
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
            //System.out.println(i+" "+population[i]+" "+population[i].getFitness());
        }
        Arrays.sort(population, Collections.reverseOrder());
        boolean isToDiscovery = isToDiscovery(predicatePattern);

        for (int i = 0; i < population.length; i++) {
            if (isToDiscovery) {
                if (population[i].getFitness() >= min_truth_value && !resultList.contains(population[i])) {
                    resultList.add((NodeTree) population[i].clone());
                }
            } else {
                if (population[i].getFitness() >= min_truth_value) {
                    resultList.add((NodeTree) population[i].clone());
                }
            }
        }
        if (isToDiscovery) {
            int iteration = 1;
            while (iteration < num_iter && resultList.size() < num_result) {
                System.out.println("Iteration "+iteration+" of "+num_iter+" ...");
                int offspring_size = population.length / 2;
                if (offspring_size % 2 != 0) {
                    offspring_size++;
                }
                NodeTree[] offspring = new NodeTree[offspring_size];
                TournamentSelection tournamentSelection = new TournamentSelection(population, offspring_size);
                tournamentSelection.execute();

                for (int i = 0; i < offspring.length; i++) {
                    NodeTree a = tournamentSelection.getNext();
                    NodeTree b = tournamentSelection.getNext();
                    NodeTree[] cross = crossover(a, b);
                    offspring[i] = cross[0];
                    offspring[i + 1] = cross[1];
                    i++;
                }
                mutation(offspring);
                gomf = new GOMF(data, logic, mut_percentage, adj_num_pop, adj_num_iter, adj_min_truth_value);
                for (int i = 0; i < offspring.length; i++) {
                    gomf.optimize(offspring[i]);
                }
                Arrays.sort(offspring);

                int lastFound = 0;
                for (int i = 0; i < offspring.length; i++) {
                    for (int j = lastFound; j < population.length; j++) {
                        if (offspring[i].getFitness().compareTo(population[j].getFitness()) > 0) {
                            //System.out.println(offspring[i].getFitness());
                            population[j] = (NodeTree) offspring[i].clone();
                            lastFound= j +1;
                            break;
                        }
                    }
                }
                Arrays.sort(population, Collections.reverseOrder());
                System.out.println("Iteration " + iteration + " ( " + resultList.size() + " ) ");
                for (int i = 0; i < population.length; i++) {
                    if (population[i].getFitness() >= min_truth_value && !resultList.contains(population[i])) {
                        resultList.add((NodeTree) population[i].clone());
                    }
                }
                iteration++;
            }
            Collections.sort(resultList, Collections.reverseOrder());
            System.out.println("post execution: ");
            for (int i = 0; i < population.length; i++) {
                System.out.println(i + " " + population[i] + " " + population[i].getFitness());
            }
        }
        System.out.println("Result list " + resultList.size());
        for (int i = 0; i < resultList.size(); i++) {
            System.out.println(i + " " + resultList.get(i) + " " + resultList.get(i).getFitness());
        }

    }

    private boolean isToDiscovery(NodeTree predicate) {
        ArrayList<Node> operators = NodeTree.getNodesByType(predicate, NodeType.OPERATOR);
        String representation = predicate.toString();
        for (Node node : operators) {
            if (node instanceof GeneratorNode) {
                GeneratorNode gn = (GeneratorNode) node;
                if (representation.contains(gn.getLabel()))
                    return true;
            }
        }
        return false;
    }

    private NodeTree[] makePopulation() {
        NodeTree[] pop = new NodeTree[num_pop];
        for (int i = 0; i < pop.length; i++) {
            pop[i] = createRandomInd();
            //System.out.printf("ind %3d: %s\n", i, pop[i]);
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
                        complete_tree(p, (GeneratorNode) node, null, 0, NodeTree.dfs(p, node));
                    else
                        growTree(p, (GeneratorNode) node, null, 0, NodeTree.dfs(p, node));
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
                ArrayList<Node> childs = ((NodeTree) father).getChildrens();
                boolean contains = false;
                int intents = 1;
                do {
                    for (Node node : childs) {
                        if (node instanceof StateNode) {
                            StateNode st = ((StateNode) node);
                            if (st.getLabel().equals(select.getLabel())) {
                                contains = true;
                            }
                        }
                    }
                    if (contains)
                        select = statesByGenerators.get(gNode.getId()).get(rand.nextInt(size));
                    intents++;
                } while (contains && intents < size);
            }
            StateNode s = null;
            try {
                s = (StateNode) select.clone();
                s.setByGenerator(gNode.getId());
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
        if ((currentDepth >= depth || rand.nextDouble() < 0.65) && (father != null && currentDepth != 0)) {
            int size = statesByGenerators.get(gNode.getId()).size();
            StateNode select = statesByGenerators.get(gNode.getId()).get(rand.nextInt(size));
            if (size >= 2 && father != null) {

                ArrayList<Node> childs = ((NodeTree) father).getChildrens();
                boolean contains = false;
                int intents = 1;
                do {
                    for (Node node : childs) {
                        if (node instanceof StateNode) {
                            StateNode st = ((StateNode) node);
                            if (st.getLabel().equals(select.getLabel())) {
                                contains = true;
                            }
                        }
                    }
                    if (contains)
                        select = statesByGenerators.get(gNode.getId()).get(rand.nextInt(size));
                    intents++;
                } while (contains && intents < size);
            }
            StateNode s = null;
            try {
                s = (StateNode) select.clone();
                s.setByGenerator(gNode.getId());
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
                List<Node> editableNode = NodeTree.getEditableNodes(population[i]);
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
                        break;
                    case STATE:
                        List<StateNode> ls = statesByGenerators.get(n.getByGenerator());
                        StateNode state = ls.get(rand.nextInt(ls.size()));
                        StateNode s = (StateNode) clon;
                        s.setColName(state.getColName());
                        s.setLabel(state.getLabel());
                        if (s.getMembershipFunction() != null) {
                            s.setMembershipFunction(state.getMembershipFunction());
                        }
                        NodeTree.replace(NodeTree.getNodeParent(population[i], n.getId()), n, s);
                        break;
                    default:
                        break;
                }

            }
        }
    }

    private NodeTree[] crossover(NodeTree a, NodeTree b) throws CloneNotSupportedException, OperatorException {
        NodeTree ac = (NodeTree) a.clone();
        NodeTree bc = (NodeTree) b.clone();

        ArrayList<Node> a_editable = NodeTree.getEditableNodes(ac);
        ArrayList<Node> b_editable = NodeTree.getEditableNodes(bc);
        Node cand = a_editable.get(rand.nextInt(a_editable.size()));
        int nivel = NodeTree.dfs(ac, cand);
        int nivel_b;
        Node cand_b;
        do {
            cand_b = b_editable.get(rand.nextInt(b_editable.size()));
            nivel_b = NodeTree.dfs(bc, cand_b);
        } while (nivel_b > nivel);
        NodeTree.replace(NodeTree.getNodeParent(ac, cand.getId()), cand, (Node) cand_b.clone());

        if (nivel <= nivel_b) {
            NodeTree.replace(NodeTree.getNodeParent(bc, cand_b.getId()), cand_b, (Node) cand.clone());
        } else {
            do {
                cand = a_editable.get(rand.nextInt(a_editable.size()));
                nivel = NodeTree.dfs(ac, cand);
            } while (nivel > nivel_b);
            NodeTree.replace(NodeTree.getNodeParent(bc, cand_b.getId()), cand_b, (Node) cand.clone());
        }
        return new NodeTree[] { ac, bc };
    }

    public void exportToCsv(String file) throws IOException {
        Table fuzzyData = Table.create();
        ArrayList<Double> v = new ArrayList<>();
        ArrayList<String> p = new ArrayList<>();
        ArrayList<String> d = new ArrayList<>();
        for (int i = 0; i < resultList.size(); i++) {
            v.add(resultList.get(i).getFitness());
            p.add("'" + resultList.get(i).toString() + "'");
            ArrayList<String> st = new ArrayList<>();
            for (Node node : NodeTree.getNodesByType(resultList.get(i), NodeType.STATE)) {
                if (node instanceof StateNode) {
                    StateNode s = (StateNode) node;
                    st.add(s.toString());
                }
            }
            d.add("'" + st.toString() + "'");
        }
        DoubleColumn value = DoubleColumn.create("truth-value", v.toArray(new Double[v.size()]));

        StringColumn predicates = StringColumn.create("predicate", p);
        StringColumn data = StringColumn.create("data", d);
        fuzzyData.addColumns(value, predicates, data);
        File f = new File(file.replace(".xlsx", ".csv").replace(".xls", ".csv"));
        fuzzyData.write().toFile(f);
    }

   
}