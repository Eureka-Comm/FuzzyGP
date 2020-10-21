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
import com.castellanos.fuzzylogicgp.base.Utils;
import com.castellanos.fuzzylogicgp.logic.Logic;
import com.castellanos.fuzzylogicgp.membershipfunction.MembershipFunction;
import com.castellanos.fuzzylogicgp.parser.MembershipFunctionSerializer;
import com.castellanos.fuzzylogicgp.parser.ParserPredicate;
import com.google.gson.GsonBuilder;

import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.Destination;
import tech.tablesaw.io.json.*;

/**
 * fuzzy compensatory logical knowledge discovery
 * 
 * @author Castellanos Alvarez, Alejandro.
 * @since Oct, 19.
 * @version 0.1.0
 */
public class KDFLC {
    private ParserPredicate parserPredicate;
    private NodeTree predicatePattern;
    private HashMap<String, List<StateNode>> statesByGenerators;
    private Logic logic;
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
    private static Random rand = Utils.random;
    private Table data;
    private ArrayList<NodeTree> resultList;

    private Table fuzzyData;

    public KDFLC(ParserPredicate pp, Logic logic, int num_pop, int num_iter, int num_result, double min_truth_value,
            double mut_percentage, int adj_num_pop, int adj_num_iter, double adj_min_truth_value, Table data)
            throws OperatorException, CloneNotSupportedException {

        if (min_truth_value < 0.0 || min_truth_value > 1.0)
            throw new IllegalArgumentException("Min truth value must be in [0,1].");

        if (mut_percentage < 0.0 || mut_percentage > 1.0)
            throw new IllegalArgumentException("Mut percentage must be in [0,1].");

        if (adj_min_truth_value < 0.0 || adj_min_truth_value > 1.0)
            throw new IllegalArgumentException("Adj min truth value must be in [0,1].");
        this.parserPredicate = pp;
        this.predicatePattern = pp.parser();
        this.logic = logic;
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
        Iterator<Node> iterator = NodeTree.getNodesByType(predicatePattern, NodeType.OPERATOR).iterator();
        while (iterator.hasNext()) {
            Node node = iterator.next();
            if (node instanceof GeneratorNode) {
                GeneratorNode gNode = (GeneratorNode) node;
                List<StateNode> states = new ArrayList<>();
                for (String var : gNode.getVariables()) {
                    for (StateNode s : parserPredicate.getStates()) {
                        if (s.getLabel().trim().equals(var.trim())) {
                            StateNode ss = (StateNode) s.copy();
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
        Arrays.sort(population, Collections.reverseOrder());
        boolean isToDiscovery = isToDiscovery(predicatePattern);
        ArrayList<Integer> toReplaceIndex = new ArrayList<>();
        for (int i = 0; i < population.length; i++) {
            if (isToDiscovery) {
                if (population[i].getFitness() >= min_truth_value) {
                    if (!check_result_contains(population[i]))
                        resultList.add((NodeTree) population[i].copy());
                    toReplaceIndex.add(i);
                }
            } else {
                if (population[i].getFitness() >= min_truth_value) {
                    resultList.add((NodeTree) population[i].copy());
                }
            }
        }

        if (isToDiscovery) {

            int iteration = 1;
            while (iteration < num_iter && resultList.size() < num_result) {
                for (Integer _index : toReplaceIndex) {
                    int intents = 0;
                    do {
                        population[_index] = createRandomInd(_index);
                        intents++;
                    } while (!valid_predicate(population[_index]) && intents < 20);
                    gomf.optimize(population[_index]);
                }
                toReplaceIndex.clear();
                System.out.println("Iteration " + iteration + " of " + num_iter + " ( " + resultList.size() + " )...");
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
                            population[j] = (NodeTree) offspring[i].copy();
                            lastFound = j + 1;
                            break;
                        }
                    }
                }

                Arrays.sort(population, Collections.reverseOrder());

                for (int i = 0; i < population.length; i++) {
                    if (population[i].getFitness() >= min_truth_value) {
                        if (!check_result_contains(population[i]))
                            resultList.add((NodeTree) population[i].copy());
                        toReplaceIndex.add(i);
                    }
                }
                iteration++;
                if (iteration % (num_iter / 10) == 0) {
                    int n = 0;
                    for (int i = 0; i < num_pop && n < num_pop / 10; i++) {
                        if (rand.nextDouble() < 0.5 && !toReplaceIndex.contains(i)) {
                            toReplaceIndex.add(i);
                            n++;
                        }
                    }
                }
            }
            Collections.sort(resultList, Collections.reverseOrder());

        }
        System.out.println("Result list " + resultList.size());
        for (int i = 0; i < resultList.size(); i++) {
            System.out.println(i + " " + resultList.get(i) + " " + resultList.get(i).getFitness());
        }

    }

    private boolean check_result_contains(NodeTree nodeTree) {
        for (int i = 0; i < resultList.size(); i++) {
            if (resultList.get(i).toString().equals(nodeTree.toString())) {
                if (Math.abs(nodeTree.getFitness() - resultList.get(i).getFitness()) >= 0.2
                        || nodeTree.getFitness() == 1.0) {
                    return resultList.contains(nodeTree);
                }
                if (Math.abs(nodeTree.getFitness() - resultList.get(i).getFitness()) < 0.2)
                    return true;
            }
        }
        return false;
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
            try {
                int intents = 0;
                do {
                    pop[i] = createRandomInd(i);
                    intents++;
                } while (!valid_predicate(pop[i]) && intents < 20);
            } catch (OperatorException e) {
                e.printStackTrace();
            }
        }
        return pop;
    }

    private boolean valid_predicate(NodeTree nodeTree) {
        ArrayList<String> labels = new ArrayList<>();
        for (Node node : nodeTree.getChildrens()) {
            if (node instanceof NodeTree) {
                if (!valid_predicate((NodeTree) node)) {
                    return false;
                }
            } else if (node instanceof StateNode) {
                String label = ((StateNode) node).getLabel();
                if (!labels.contains(label)) {
                    labels.add(label);
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    private NodeTree createRandomInd(int index) throws OperatorException {
        NodeTree p = (NodeTree) predicatePattern.copy();
        boolean flag = false;
        if (predicatePattern.getChildrens().size() == 1) {
            flag = predicatePattern.getChildrens().get(0) instanceof GeneratorNode;
        }
        Iterator<Node> iterator = NodeTree.getNodesByType(predicatePattern, NodeType.OPERATOR).iterator();
        while (iterator.hasNext()) {
            Node node = iterator.next();
            if (node instanceof GeneratorNode) {
                Node generate = ((GeneratorNode) node).generate(statesByGenerators.get(node.getId()),
                        index < num_pop / 2);
                if (p != node && p.getType() != NodeType.OPERATOR) {
                    NodeTree.replace(p, node, generate, flag);
                } else {
                    if (((GeneratorNode) node).getDepth() == 0) {
                        NodeTree root = new NodeTree(NodeType.NOT);
                        root.addChild(generate);
                        return root;
                    }
                    if (generate.getType() == NodeType.STATE) {
                        return createRandomInd(index);
                    }
                    return (NodeTree) generate;
                }
            }
        }

        return p;
    }

    private void mutation(NodeTree[] population) throws OperatorException {
        for (int i = 0; i < population.length; i++) {
            if (rand.nextDouble() < mut_percentage) {
                List<Node> editableNode = NodeTree.getEditableNodes(population[i]);
                Node n = editableNode.get(Utils.randInt(0, editableNode.size() - 1));

                int intents = 0;
                while (n.getType() == NodeType.NOT && intents < editableNode.size()) {
                    n = editableNode.get(Utils.randInt(0, editableNode.size() - 1));
                    intents++;
                }
                Node clon = (Node) n.copy();
                switch (n.getType()) {
                    case OR:
                        clon.setType(NodeType.AND);
                        NodeTree.replace(NodeTree.getNodeParent(population[i], n.getId()), n, clon, false);
                        break;
                    case AND:
                        clon.setType(NodeType.OR);
                        NodeTree.replace(NodeTree.getNodeParent(population[i], n.getId()), n, clon, false);
                        break;
                    case IMP:
                        clon.setType(NodeType.EQV);
                        NodeTree.replace(NodeTree.getNodeParent(population[i], n.getId()), n, clon, false);
                        break;
                    case EQV:
                        clon.setType(NodeType.IMP);
                        NodeTree.replace(NodeTree.getNodeParent(population[i], n.getId()), n, clon, false);
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
                        NodeTree.replace(NodeTree.getNodeParent(population[i], n.getId()), n, s, false);
                        break;
                    default:
                        break;
                }

            }
        }
    }

    private NodeTree[] crossover(NodeTree a, NodeTree b) throws OperatorException {
        NodeTree ac = (NodeTree) a.copy();
        NodeTree bc = (NodeTree) b.copy();

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
        NodeTree.replace(NodeTree.getNodeParent(ac, cand.getId()), cand, (Node) cand_b.copy(), false);

        if (nivel <= nivel_b) {
            NodeTree.replace(NodeTree.getNodeParent(bc, cand_b.getId()), cand_b, (Node) cand.copy(), false);
        } else {
            do {
                cand = a_editable.get(rand.nextInt(a_editable.size()));
                nivel = NodeTree.dfs(ac, cand);
            } while (nivel > nivel_b);
            NodeTree.replace(NodeTree.getNodeParent(bc, cand_b.getId()), cand_b, (Node) cand.copy(), false);
        }
        return new NodeTree[] { ac, bc };
    }

    public void exportToJSon(String file) throws IOException {
        outPutData();
        File f = new File(file.replace(".xlsx", ".csv").replace(".xls", ".csv"));
        JsonWriter jsonWriter = new JsonWriter();
        JsonWriteOptions options = JsonWriteOptions
                .builder(new Destination(new File(f.getAbsolutePath().replace(".csv", ".json")))).header(true).build();
        jsonWriter.write(fuzzyData, options);
    }

    private void outPutData() {
        fuzzyData = Table.create();
        ArrayList<Double> v = new ArrayList<>();
        ArrayList<String> p = new ArrayList<>();
        ArrayList<String> d = new ArrayList<>();
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(MembershipFunction.class, new MembershipFunctionSerializer());
        builder.excludeFieldsWithoutExposeAnnotation();

        for (int i = 0; i < resultList.size(); i++) {
            v.add(resultList.get(i).getFitness());
            p.add(resultList.get(i).toString());
            String st = "(";
            for (Node node : NodeTree.getNodesByType(resultList.get(i), NodeType.STATE)) {
                if (node instanceof StateNode) {
                    StateNode s = (StateNode) node;
                    st += s.toString() + " ";
                }
            }
            d.add(st.trim() + ")");
        }
        DoubleColumn value = DoubleColumn.create("truth-value", v.toArray(new Double[v.size()]));

        StringColumn predicates = StringColumn.create("predicate", p);
        StringColumn data = StringColumn.create("data", d);
        fuzzyData.addColumns(value, predicates, data);
    }

    public void exportToCsv(String file) throws IOException {
        outPutData();
        File f = new File(file.replace(".xlsx", ".csv").replace(".xls", ".csv"));
        fuzzyData.write().toFile(f);
    }

    /**
     * 
     * @return discovered predicate list
     */
    public ArrayList<NodeTree> getResultList() {
        return resultList;
    }

}