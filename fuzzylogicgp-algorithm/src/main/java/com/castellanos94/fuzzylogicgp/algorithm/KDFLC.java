package com.castellanos94.fuzzylogicgp.algorithm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.castellanos94.fuzzylogicgp.core.DiscoveryResult;
import com.castellanos94.fuzzylogicgp.core.GeneratorNode;
import com.castellanos94.fuzzylogicgp.core.IAlgorithm;
import com.castellanos94.fuzzylogicgp.core.Node;
import com.castellanos94.fuzzylogicgp.core.NodeTree;
import com.castellanos94.fuzzylogicgp.core.NodeType;
import com.castellanos94.fuzzylogicgp.core.OperatorException;
import com.castellanos94.fuzzylogicgp.core.ResultTask;
import com.castellanos94.fuzzylogicgp.core.StateNode;
import com.castellanos94.fuzzylogicgp.core.TournamentSelection;
import com.castellanos94.fuzzylogicgp.core.Utils;
import com.castellanos94.fuzzylogicgp.logic.GMBCFALogic;
import com.castellanos94.fuzzylogicgp.logic.Logic;
import com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunction;
import com.castellanos94.fuzzylogicgp.parser.MembershipFunctionSerializer;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.Destination;
import tech.tablesaw.io.json.JsonWriteOptions;
import tech.tablesaw.io.json.JsonWriter;

/**
 * fuzzy compensatory logical knowledge discovery
 *
 * @author Castellanos Alvarez, Alejandro.
 * @since Oct, 19.
 * @version 0.1.2
 * @see FPGOptimizer
 * @see EvaluatePredicate
 */
public class KDFLC implements IAlgorithm {

    private static final Logger logger = LogManager.getLogger(KDFLC.class);

    private NodeTree predicatePattern;
    private HashMap<String, GeneratorNode> generators;
    private Logic logic;
    private int num_pop;
    private int num_iter;
    private int num_result;
    private double min_truth_value;
    private double mut_percentage;

    // FPGOptimizer Params
    private int adj_num_pop;
    private int adj_num_iter;
    private double adj_min_truth_value;

    // Aux
    private static Random rand = new Random();
    private Table data;
    private ArrayList<NodeTree> resultList;

    private Table fuzzyData;
    private boolean parallelSupport = true;
    private ArrayList<Integer> integerIndex;
    private final long maxTime;
    private final PredicateGenerator predicateGenerator;
    private final ArrayList<String> logList;

    public KDFLC(Logic logic, int num_pop, int num_iter, int num_result, double min_truth_value, double mut_percentage,
            int adj_num_pop, int adj_num_iter, double adj_min_truth_value, Table data, long maxTime) {

        if (min_truth_value < 0.0 || min_truth_value > 1.0) {
            throw new IllegalArgumentException("Min truth value must be in [0,1].");
        }

        if (mut_percentage < 0.0 || mut_percentage > 1.0) {
            throw new IllegalArgumentException("Mut percentage must be in [0,1].");
        }

        if (adj_min_truth_value < 0.0 || adj_min_truth_value > 1.0) {
            throw new IllegalArgumentException("Adj min truth value must be in [0,1].");
        }
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
        this.generators = new HashMap<>();
        this.integerIndex = new ArrayList<>(IntStream.range(0, num_pop).boxed().collect(Collectors.toList()));
        this.predicateGenerator = new PredicateGenerator();
        this.maxTime = maxTime;
        this.logList = new ArrayList<>();
    }

    /**
     *
     * @param predicate
     */
    @Override
    public void execute(NodeTree predicate) {
        final long initialTime = System.currentTimeMillis();
        logger.info("start time {}", initialTime);
        logList.add(String.format("Start time %d min.", (initialTime / 60000)));
        this.predicatePattern = predicate;
        int wasNotChanged = 0;
        Iterator<GeneratorNode> iterator = NodeTree.getNodesByType(predicatePattern, GeneratorNode.class).iterator();
        while (iterator.hasNext()) {
            GeneratorNode gNode = iterator.next();
            generators.put(gNode.getId(), gNode);
        }
        NodeTree[] population = makePopulation();
        logger.info("random generation finished");
        if (parallelSupport) {
            Arrays.parallelSetAll(population, _index -> {
                FPGOptimizer optimizer = new FPGOptimizer(logic, data, adj_num_iter, adj_num_pop, adj_min_truth_value,
                        0.95, null);
                return optimizer.execute(population[_index].copy());
            });
        } else {
            Arrays.setAll(population, _index -> {
                FPGOptimizer optimizer = new FPGOptimizer(logic, data, adj_num_iter, adj_num_pop, adj_min_truth_value,
                        0.95, null);
                return optimizer.execute(population[_index].copy());
            });
        }

        Arrays.sort(population, Collections.reverseOrder());
        logger.info("End time for evaluation of random generation: {}",
                (System.currentTimeMillis() - initialTime) / 1000);
        boolean isToDiscovery = isToDiscovery(predicatePattern);
        ArrayList<Integer> toReplaceIndex = new ArrayList<>();
        for (int i = 0; i < population.length; i++) {
            if (isToDiscovery) {
                if (population[i].getFitness() >= min_truth_value) {
                    if (!check_result_contains(population[i])) {
                        resultList.add((NodeTree) population[i].copy());
                    }
                    toReplaceIndex.add(i);
                }
            } else {
                if (population[i].getFitness() >= min_truth_value) {
                    resultList.add((NodeTree) population[i].copy());
                }
            }
        }
        long currentTime = System.currentTimeMillis();
        if (isToDiscovery) {
            boolean isTheSameGenerator = isTheSameGenerator();
            int iteration = 1;

            while ((((currentTime = System.currentTimeMillis()) - initialTime) <= maxTime)
                    && resultList.size() < num_result) {
                Stream<Integer> stream = (parallelSupport) ? toReplaceIndex.parallelStream() : toReplaceIndex.stream();
                logger.info("To replace {} at iteration {}", toReplaceIndex.size(), iteration);
                stream.forEach(_index -> {
                    int intents = 0;
                    do {
                        try {
                            population[_index] = createRandomInd(_index);
                        } catch (OperatorException e) {
                            logger.error("Replace:", e);
                        }
                        intents++;
                    } while (!valid_predicate(population[_index]) && intents < 20);

                    FPGOptimizer _optimizer = new FPGOptimizer(logic, data, adj_num_iter, adj_num_pop,
                            adj_min_truth_value,
                            0.95, null);
                    population[_index] = _optimizer.execute(population[_index].copy());
                });
                toReplaceIndex.clear();
                logger.info("Iteration {} of {} ({}), time {}s of {}s...", iteration, this.num_iter, resultList.size(),
                        ((currentTime - initialTime) / 1000), (maxTime / 1000));
                logList.add(String.format("Time elapsed %d min. Results %d", ((currentTime - initialTime) / 60000),
                        resultList.size()));
                int offspring_size = population.length / 2;
                if (offspring_size % 2 != 0) {
                    offspring_size++;
                }
                NodeTree[] offspring = new NodeTree[offspring_size];
                if (!isTheSameGenerator) {
                    TournamentSelection tournamentSelection = new TournamentSelection(population, offspring_size);
                    tournamentSelection.execute();
                    for (int i = 0; i < offspring.length; i++) {
                        try {
                            NodeTree a = tournamentSelection.getNext();
                            NodeTree b = tournamentSelection.getNext();
                            NodeTree[] cross = crossover(a, b);
                            offspring[i] = cross[0];
                            offspring[i + 1] = cross[1];
                            i++;
                        } catch (OperatorException ex) {
                            logger.error("Crossosver:", ex);
                        }
                    }
                } else {
                    for (int i = 0; i < offspring.length; i++) {
                        try {
                            int intents = 0;
                            do {
                                offspring[i] = createRandomInd(i);
                                intents++;
                            } while (!valid_predicate(offspring[i]) && intents < 20);
                        } catch (OperatorException e) {
                            logger.error("random individual:" + e);
                        }
                    }

                }
                try {
                    mutation(offspring);
                } catch (OperatorException ex) {
                    logger.error("Mutation:", ex);
                }
                if (parallelSupport) {
                    Arrays.parallelSetAll(offspring, _index -> {
                        FPGOptimizer optimizer = new FPGOptimizer(logic, data, adj_num_iter, adj_num_pop,
                                adj_min_truth_value,
                                0.95, null);
                        return optimizer.execute(offspring[_index].copy());
                    });
                } else {
                    Arrays.setAll(offspring, _index -> {
                        FPGOptimizer optimizer = new FPGOptimizer(logic, data, adj_num_iter, adj_num_pop,
                                adj_min_truth_value,
                                0.95, null);
                        return optimizer.execute(offspring[_index].copy());
                    });
                }
                List<Integer> ignore = new ArrayList<>();
                for (int i = 0; i < offspring.length; i++) {
                    int index = checkContains(offspring[i], population);
                    if (index != -1) {
                        if (offspring[i].getFitness().compareTo(population[index].getFitness()) > 0)
                            population[index] = offspring[i].copy();
                        ignore.add(i);
                    }

                }
                logger.info("equals offspring population {} of {}", ignore.size(), offspring.length);
                for (int i = 0; i < offspring.length; i++) {
                    if (!ignore.contains(i)) {
                        for (int j = 0; j < population.length; j++) {
                            if (offspring[i].getFitness().compareTo(population[j].getFitness()) > 0) {
                                population[j] = (NodeTree) offspring[i].copy();
                                ignore.add(i);
                                break;
                            }
                        }
                    }
                }
                logger.info("after replace population {}", ignore.size());

                boolean flag = false;
                for (int i = 0; i < population.length; i++) {
                    double f = population[i].getFitness();
                    if (f >= min_truth_value) {
                        if (!check_result_contains(population[i])) {
                            resultList.add((NodeTree) population[i].copy());
                            flag = true;
                        }
                        toReplaceIndex.add(i);
                    }
                }
                if (!flag) {
                    wasNotChanged++;
                }
                iteration++;
                // Esto se cambio antes incluia a
                // if (wasNotChanged >= num_iter / 10 && !isTheSameGenerator) {
                if (wasNotChanged >= (int) (num_iter * 0.10) && iteration > 9) {
                    int n = 0;
                    wasNotChanged = 0;
                    int index;
                    for (int i = 0; i < num_pop; i++) {
                        logger.error("{} - {}", i, population[i]);
                    }
                    Collections.shuffle(integerIndex);
                    for (int i = 0; i < num_pop && n < num_pop / 2; i++) {
                        index = integerIndex.get(i);
                        if (!toReplaceIndex.contains(index)) {
                            toReplaceIndex.add(index);
                            n++;
                        }
                    }
                }
            }
            Collections.sort(resultList, Collections.reverseOrder());

        }

        logger.info("Result list " + resultList.size());

        if (resultList.isEmpty()) {
            if (isForEvaluate()) {
                FPGOptimizer optimizer = new FPGOptimizer(logic, data, adj_num_iter, adj_num_pop, adj_min_truth_value,
                        0.95, null);
                resultList.add(optimizer.execute(this.predicatePattern));
            }

        }

        if (resultList.isEmpty()) {
            logger.info("Added min" + resultList.isEmpty());
            Arrays.sort(population, Collections.reverseOrder());
            int size = population.length > 10 ? 10 : population.length;
            for (int i = 0; i < size; i++) {
                resultList.add((NodeTree) population[i].copy());
            }
            logger.info("Best found " + population[0].getFitness());
        }
        logList.add(String.format("Discover completed in %d min. Results %d",
                ((System.currentTimeMillis() - initialTime) / 60000),
                resultList.size()));
    }

    public ArrayList<String> getLogList() {
        return logList;
    }

    private boolean isTheSameGenerator() {
        ArrayList<GeneratorNode> _nodesByType = NodeTree.getNodesByType(this.predicatePattern, GeneratorNode.class);
        ArrayList<Node> fList = new ArrayList<>();
        for (Node n : _nodesByType) {
            if (!fList.contains(n)) {
                fList.add(n);
            }
        }
        Iterator<Node> iterator = fList.iterator();
        ArrayList<Integer> count = new ArrayList<>();
        String expression = this.predicatePattern.toString();
        while (iterator.hasNext()) {
            Node node = iterator.next();
            if (node instanceof GeneratorNode) {
                GeneratorNode generator = (GeneratorNode) node;
                count.add(StringUtils.countMatches(expression, String.format("\"%s\"", generator.getLabel())));
            }
        }
        for (Integer i : count) {
            if (i > 1) {
                return true;
            }
        }
        return false;
    }

    private boolean isForEvaluate() {
        ArrayList<StateNode> states = NodeTree.getNodesByType(this.predicatePattern, StateNode.class);
        for (StateNode state : states) {
            if (state.getMembershipFunction() == null) {
                return false;
            }
        }
        ArrayList<GeneratorNode> genes = NodeTree.getNodesByType(this.predicatePattern, GeneratorNode.class);
        if (!genes.isEmpty()) {
            return false;
        }
        return true;
    }

    private int checkContains(NodeTree nodeTree, NodeTree[] pop) {
        int flag = -1;
        for (int i = 0; i < pop.length && flag == -1; i++) {
            if (pop[i].toString().equals(nodeTree.toString())) {
                flag = i;
            }
        }
        return flag;
    }

    private boolean check_result_contains(NodeTree nodeTree) {
        for (int i = 0; i < resultList.size(); i++) {
            if (resultList.get(i).toString().equals(nodeTree.toString())) {
                if (Math.abs(nodeTree.getFitness() - resultList.get(i).getFitness()) >= 0.2) {
                    if (nodeTree.getFitness() == 1.0) {
                        return resultList.contains(nodeTree);
                    }
                    return false;
                }
                if (Math.abs(nodeTree.getFitness() - resultList.get(i).getFitness()) < 0.2) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isToDiscovery(NodeTree predicate) {
        ArrayList<GeneratorNode> operators = NodeTree.getNodesByType(predicate, GeneratorNode.class);
        String representation = predicate.toString();
        for (GeneratorNode gn : operators) {
            if (representation.contains(gn.getLabel())) {
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
        for (Node node : nodeTree) {
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

        if (predicatePattern.getChildren().size() == 1) {
            flag = predicatePattern.getChildren().get(0) instanceof GeneratorNode;
        }
        Iterator<GeneratorNode> iterator = NodeTree.getNodesByType(p, GeneratorNode.class).iterator();
        while (iterator.hasNext()) {
            Node node = iterator.next();
            Node generate = predicateGenerator.generate((GeneratorNode) node, index < num_pop / 2);
            generate.setEditable(true);
            if (p != node && p.getType() != NodeType.OPERATOR) {
                NodeTree _parent = null;
                do {
                    _parent = NodeTree.getNodeParent(p, node.getId());
                    if (_parent != null) {
                        NodeTree.replace(_parent, node, generate, flag);
                    }
                } while (_parent != null);
            } else {
                if (((GeneratorNode) node).getDepth() == 0) {
                    NodeTree root = new NodeTree(NodeType.NOT);
                    root.setEditable(true);
                    root.addChild(generate);
                    return root;
                }
                if (generate.getType() == NodeType.STATE) {
                    return createRandomInd(index);
                }
                return (NodeTree) generate;
            }
        }
        return p;
    }

    private void mutation(NodeTree[] population) throws OperatorException {
        for (int i = 0; i < population.length; i++) {
            if (rand.nextDouble() < mut_percentage) {
                List<Node> editableNode = NodeTree.getEditableNodes(population[i]);
                if (!editableNode.isEmpty()) {

                    Node n = editableNode.get(Utils.randInt(rand, 0, editableNode.size() - 1));

                    int intents = 0;
                    while (n.getType() == NodeType.NOT && intents < editableNode.size()) {
                        n = editableNode.get(Utils.randInt(rand, 0, editableNode.size() - 1));
                        intents++;
                    }
                    Node clon = (Node) n.copy();
                    Node parent = null;
                    boolean isValidChange = false;
                    NodeType[] _types = this.generators.get(n.getByGenerator()).getOperators();
                    switch (n.getType()) {
                        case OR:
                            for (NodeType t : _types) {
                                if (t == NodeType.AND) {
                                    isValidChange = true;
                                    break;
                                }
                            }
                            if (isValidChange) {
                                clon.setType(NodeType.AND);
                                do {
                                    parent = NodeTree.getNodeParent(population[i], n.getId());
                                    if (parent != null) {
                                        NodeTree.replace((NodeTree) parent, n, clon, false);
                                    }
                                } while (parent != null);
                            }
                            break;
                        case AND:
                            for (NodeType t : _types) {
                                if (t == NodeType.OR) {
                                    isValidChange = true;
                                    break;
                                }
                            }
                            if (isValidChange) {
                                clon.setType(NodeType.OR);
                                do {
                                    parent = NodeTree.getNodeParent(population[i], n.getId());
                                    if (parent != null) {
                                        NodeTree.replace((NodeTree) parent, n, clon, false);
                                    }
                                } while (parent != null);
                            }
                            break;
                        case IMP:
                            for (NodeType t : _types) {
                                if (t == NodeType.EQV) {
                                    isValidChange = true;
                                    break;
                                }
                            }
                            if (isValidChange) {
                                clon.setType(NodeType.EQV);
                                do {
                                    parent = NodeTree.getNodeParent(population[i], n.getId());
                                    if (parent != null) {
                                        NodeTree.replace((NodeTree) parent, n, clon, false);
                                    }
                                } while (parent != null);
                            }
                            break;
                        case EQV:
                            for (NodeType t : _types) {
                                if (t == NodeType.IMP) {
                                    isValidChange = true;
                                    break;
                                }
                            }
                            if (isValidChange) {
                                clon.setType(NodeType.IMP);
                                do {
                                    parent = NodeTree.getNodeParent(population[i], n.getId());
                                    if (parent != null) {
                                        NodeTree.replace((NodeTree) parent, n, clon, false);
                                    }
                                } while (parent != null);
                            }
                            break;
                        case STATE:
                            List<Node> ls = generators.get(n.getByGenerator()).getVariables().stream()
                                    .filter(node -> node instanceof StateNode).collect(Collectors.toList());
                            StateNode state = (StateNode) ls.get(rand.nextInt(ls.size()));
                            NodeTree p = NodeTree.getNodeParent(population[i], n.getId());
                            ArrayList<String> labels = new ArrayList<>();
                            for (Node _c : p) {
                                if (_c instanceof StateNode) {
                                    labels.add(((StateNode) _c).getLabel());
                                }
                            }
                            int i_ = 0;
                            while (i_ < ls.size() && labels.contains(state.getLabel())) {
                                state = (StateNode) ls.get(rand.nextInt(ls.size()));
                                i_++;
                            }
                            if (!labels.contains(state.getLabel())) {
                                do {
                                    parent = NodeTree.getNodeParent(population[i], n.getId());
                                    if (parent != null) {
                                        StateNode _s = (StateNode) state.copy();
                                        _s.setEditable(true);
                                        _s.setByGenerator(n.getByGenerator());
                                        NodeTree.replace((NodeTree) parent, n, _s, false);
                                    }
                                } while (parent != null);
                            }
                            break;
                        default:
                            break;
                    }

                } else {
                    logger.debug("\t? " + editableNode.size());
                    logger.debug("\t" + population[i]);
                }
            }
        }
    }

    private NodeTree[] crossover(NodeTree a, NodeTree b) throws OperatorException {
        NodeTree ac = (NodeTree) a.copy();
        NodeTree bc = (NodeTree) b.copy();

        ArrayList<Node> a_editable = NodeTree.getEditableNodes(ac);
        ArrayList<Node> b_editable = NodeTree.getEditableNodes(bc);
        if (a_editable.isEmpty() || b_editable.isEmpty()) {
            /*
             * System.out.println("\tCrossover? " + a_editable.size() + ", " +
             * b_editable.size()); System.out.println("\tCrossover " + ac + ", " + bc);
             * System.out.println("\tCrossover Parents? " +
             * NodeTree.getEditableNodes(a).size() + ", " +
             * NodeTree.getEditableNodes(b).size());
             * System.out.println("\tCrossover Parents? " + a + ", " + b);
             */
            return new NodeTree[] { ac, bc };
        }
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
        // builder.excludeFieldsWithoutExposeAnnotation();
        builder.setExclusionStrategies(new ExclusionStrategy() {

            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return f.getName().equalsIgnoreCase("editable");
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }

        });
        Gson gson = builder.create();
        ArrayList<Double> f0 = null;
        FPGOptimizer optimizer = null;

        if (this.logic instanceof GMBCFALogic) {
            f0 = new ArrayList<>();
            GMBCFALogic lFa_Logic = (GMBCFALogic) this.logic;
            lFa_Logic.setExponent(0);
            optimizer = new FPGOptimizer(logic, data, adj_num_iter, adj_num_pop, adj_min_truth_value,
                    0.95, null);
        }
        ArrayList<NodeTree> rs = this.getResultList();
        for (int i = 0; i < rs.size(); i++) {
            EvaluatePredicate evaluatePredicate = new EvaluatePredicate(logic, data);
            Double fv = evaluatePredicate.evaluate(rs.get(i));
            rs.get(i).setFitness(fv);
        }
        rs.sort(Collections.reverseOrder());
        for (int i = 0; i < rs.size(); i++) {
            Double fv = rs.get(i).getFitness();
            v.add(fv);
            p.add(rs.get(i).toString());
            NodeTree nodeTree = rs.get(i);
            nodeTree.setEditable(false);
            d.add(gson.toJson(nodeTree));

            if (f0 != null) {
                NodeTree execute = optimizer.execute(nodeTree.copy());
                f0.add(execute.getFitness());
            }
        }
        DoubleColumn value = null;
        DoubleColumn f1 = null;

        if (f0 != null) {
            f1 = DoubleColumn.create("truth-value", f0.toArray(new Double[f0.size()]));
            value = DoubleColumn.create("truth-value-f", v.toArray(new Double[v.size()]));
        } else {
            value = DoubleColumn.create("truth-value", v.toArray(new Double[v.size()]));
        }
        StringColumn predicates = StringColumn.create("predicate", p);
        StringColumn data = StringColumn.create("data", d);
        if (f0 == null) {
            fuzzyData.addColumns(value, predicates, data);
        } else {
            fuzzyData.addColumns(value, f1, predicates, data);
        }
    }

    @Override
    public void exportResult(File file) {
        outPutData();

        File f = new File(file.getAbsolutePath().replace(".xlsx", ".csv").replace(".xls", ".csv"));
        try {
            fuzzyData.write().toFile(f);
        } catch (IOException ex) {
            logger.error("Errot to export:", ex);
        }
    }

    /**
     *
     * @return discovered predicate list
     */
    public ArrayList<NodeTree> getResultList() {
        resultList.forEach(p -> {
            HashMap<String, Integer> map = new HashMap<>();
            for (StateNode node : NodeTree.getNodesByType(p, StateNode.class)) {
                if (map.containsKey(node.getLabel())) {
                    int count = map.get(node.getLabel()) + 1;
                    String label = String.format("%s-%d", node.getLabel(), count);
                    node.setLabel(label);
                    map.put(node.getLabel(), count);
                } else {
                    map.put(node.getLabel(), 1);
                }
            }
        });
        return resultList;
    }

    public void setParallelSupport(boolean parallelSupport) {
        this.parallelSupport = parallelSupport;
    }

    public boolean isParallelSupport() {
        return parallelSupport;
    }

    @Override
    public String toString() {
        return "KDFLC [adj_min_truth_value=" + adj_min_truth_value + ", adj_num_iter=" + adj_num_iter + ", adj_num_pop="
                + adj_num_pop + ", logic=" + logic + ", min_truth_value=" + min_truth_value + ", mut_percentage="
                + mut_percentage + ", num_iter=" + num_iter + ", num_pop=" + num_pop + ", num_result=" + num_result
                + ", parallelSupport=" + parallelSupport + "]";
    }

    @Override
    public ResultTask getResult() {
        ArrayList<DiscoveryResult.Record> values = new ArrayList<>();

        ArrayList<NodeTree> rs = this.getResultList();

        for (int i = 0; i < rs.size(); i++) {
            Logic lFa_Logic = null;
            if (this.logic instanceof GMBCFALogic) {
                lFa_Logic = (GMBCFALogic) this.logic;
                ((GMBCFALogic) lFa_Logic).setExponent(0);
            } else {
                lFa_Logic = this.logic;
            }
            EvaluatePredicate evaluatePredicate = new EvaluatePredicate(logic, data);
            Double fv = evaluatePredicate.evaluate(rs.get(i));
            rs.get(i).setFitness(fv);
        }
        rs.sort(Collections.reverseOrder());
        for (int i = 0; i < rs.size(); i++) {
            Double fv = rs.get(i).getFitness();
            values.add(new DiscoveryResult.Record(fv, rs.get(i).toString(), rs.get(i).toJson()));
        }
        return new DiscoveryResult(values);
    }

}
