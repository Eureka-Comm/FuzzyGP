package com.castellanos94.fuzzylogicgp.algorithm;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.stream.Collectors;

import com.castellanos94.fuzzylogicgp.core.GeneratorNode;
import com.castellanos94.fuzzylogicgp.core.Node;
import com.castellanos94.fuzzylogicgp.core.NodeTree;
import com.castellanos94.fuzzylogicgp.core.NodeType;
import com.castellanos94.fuzzylogicgp.core.OperatorException;
import com.castellanos94.fuzzylogicgp.core.StateNode;
import com.castellanos94.fuzzylogicgp.core.Utils;

/**
 * Class for predicate generator
 * 
 * @see GeneratorNode
 */
public class PredicateGenerator {
    private final Random random = new Random();

    public Node generate(GeneratorNode generatorNode, boolean balanced) throws OperatorException {
        if (generatorNode.getMax_child_number() < 2) {
            generatorNode.setMax_child_number(
                    Math.max(generatorNode.getOperators().length + generatorNode.getVariables().size() / 2, 2));
        }

        ArrayList<StateNode> filteredStates = new ArrayList<>();
        generatorNode.getVariables().stream().filter(state -> state instanceof StateNode).collect(Collectors.toList())
                .forEach(var -> filteredStates.add((StateNode) var));
        ArrayList<GeneratorNode> filteredGenerators = new ArrayList<>();
        generatorNode.getVariables().stream().filter(var -> var instanceof GeneratorNode).collect(Collectors.toList())
                .forEach(var -> filteredGenerators.add((GeneratorNode) var));
        filteredGenerators.remove(generatorNode);
        return random.nextDouble() >= 0.48
                ? generate_child(generatorNode, null, filteredStates, filteredGenerators, 0, balanced)
                : generateChild(generatorNode, null, filteredStates, filteredGenerators, balanced);
    }

    private NodeTree generateChild(GeneratorNode generatorNode, NodeTree root, ArrayList<StateNode> filteredStates,
            ArrayList<GeneratorNode> filteredGenerators, boolean balanced) throws OperatorException {
        if (root == null) {
            root = new NodeTree(generatorNode.getOperators()[random.nextInt(generatorNode.getOperators().length)]);
            root.setEditable(true);
            root.setByGenerator(generatorNode.getId());
        }
        ArrayList<NodeTree> toProcess = new ArrayList<>();
        toProcess.add(root);
        Node _child;
        int currentLevel = 0;
        NodeTree currentRoot = toProcess.get(0);
        int index = 0;
        LinkedList<Node> children = currentRoot.getChildren();

        while (index < toProcess.size()) {
            children = toProcess.get(index).getChildren();
            currentRoot = toProcess.get(index++);
            if (!children.contains(currentRoot)) {
                currentLevel++;
            }
            int currentMaxChild = generateRandomMaxChildNumber(generatorNode, currentRoot);
            // for (int j = 0; j < currentMaxChild; j++) {
            // if (currentLevel < this.depth && (Utils.random.nextDouble() < 0.45 ||
            // balanced)) {
            switch (currentRoot.getType()) {
                case AND:
                case OR:
                    for (int i = 0; i < currentMaxChild; i++) {
                        if (currentLevel >= generatorNode.getDepth() || random.nextDouble() < 0.45) {
                            _child = this.generate_state(generatorNode, currentRoot, filteredStates, filteredGenerators,
                                    balanced);
                        } else {
                            _child = new NodeTree(
                                    generatorNode.getOperators()[random.nextInt(generatorNode.getOperators().length)]);
                            toProcess.add((NodeTree) _child);
                        }
                        _child.setEditable(true);
                        _child.setByGenerator(generatorNode.getId());
                        currentRoot.addChild(_child);
                    }
                    break;
                case IMP:
                case EQV:
                    for (int i = 0; i < currentMaxChild; i += 1) {
                        if (currentLevel >= generatorNode.getDepth() || random.nextDouble() < 0.45) {
                            _child = this.generate_state(generatorNode, currentRoot, filteredStates, filteredGenerators,
                                    balanced);
                        } else {
                            _child = new NodeTree(
                                    generatorNode.getOperators()[random.nextInt(generatorNode.getOperators().length)]);
                            toProcess.add((NodeTree) _child);
                        }
                        _child.setEditable(true);
                        _child.setByGenerator(generatorNode.getId());
                        currentRoot.addChild(_child);
                    }
                    break;
                case NOT:
                    if (currentLevel >= generatorNode.getDepth() || random.nextDouble() < 0.45) {
                        _child = this.generate_state(generatorNode, currentRoot, filteredStates, filteredGenerators,
                                balanced);
                    } else {
                        _child = new NodeTree(
                                generatorNode.getOperators()[random.nextInt(generatorNode.getOperators().length)]);
                        toProcess.add((NodeTree) _child);
                    }
                    _child.setEditable(true);
                    _child.setByGenerator(generatorNode.getId());
                    currentRoot.addChild(_child);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported type: " + currentRoot.getType());
            }
        }
        return root;
    }

    private int generateRandomMaxChildNumber(GeneratorNode generatorNode, NodeTree tree) {
        switch (tree.getType()) {
            case AND:
            case OR:
                return Utils.randInt(random, 2, generatorNode.getMax_child_number());
            case IMP:
            case EQV:
                return 2;
            case NOT:
                return 1;
            default:
                throw new IllegalArgumentException("Unsupported type: " + tree.getType());
        }
    }

    private Node generate_child(GeneratorNode generatorNode, NodeTree root, ArrayList<StateNode> filteredStates,
            ArrayList<GeneratorNode> filteredGenerators, int current_depth, boolean balanced) throws OperatorException {
        Node _child = null;
        if (current_depth < generatorNode.getDepth()) {
            if (random.nextDouble() < 0.45 || balanced) {
                NodeTree tree = new NodeTree(
                        generatorNode.getOperators()[random.nextInt(generatorNode.getOperators().length)]);
                tree.setEditable(true);
                tree.setByGenerator(generatorNode.getId());

                switch (tree.getType()) {
                    case AND:
                    case OR:
                        for (int i = 0; i < Utils.randInt(random, 2, generatorNode.getMax_child_number()); i++) {
                            _child = this.generate_child(generatorNode, tree, filteredStates, filteredGenerators,
                                    current_depth + 1,
                                    balanced);
                            _child.setEditable(true);
                            tree.addChild(_child);
                        }
                        return tree;
                    case IMP:
                    case EQV:
                        for (int i = 0; i < 2; i++) {
                            _child = this.generate_child(generatorNode, tree, filteredStates, filteredGenerators,
                                    current_depth + 1,
                                    balanced);
                            _child.setEditable(true);
                            tree.addChild(_child);
                        }
                        return tree;
                    case NOT:
                        _child = this.generate_child(generatorNode, tree, filteredStates, filteredGenerators,
                                current_depth + 1,
                                balanced);
                        _child.setEditable(true);
                        tree.addChild(_child);
                        return tree;
                    default:
                        throw new IllegalArgumentException("Unsupported type: " + tree.getType());
                }
            } else {
                _child = this.generate_state(generatorNode, root, filteredStates, filteredGenerators, balanced);
                _child.setEditable(true);
                return _child;
            }
        }
        _child = this.generate_state(generatorNode, root, filteredStates, filteredGenerators, balanced);
        _child.setEditable(true);
        return _child;
    }

    private Node generate_state(GeneratorNode g, NodeTree root, ArrayList<StateNode> filteredStates,
            ArrayList<GeneratorNode> filteredGenerators, boolean balanced) throws OperatorException {
        StateNode select;
        boolean isValid;
        int intents = 0;
        if (filteredGenerators != null && !filteredGenerators.isEmpty() && random.nextDouble() >= 0.5) {
            GeneratorNode generatorNode = filteredGenerators.get(random.nextInt(filteredGenerators.size()));
            Node generate = this.generate(generatorNode, balanced);
            generate.setByGenerator(generatorNode.getId());
            generate.setEditable(true);
            return generate;
        }
        if (root != null) {
            do {
                select = (StateNode) filteredStates.get(random.nextInt(filteredStates.size()));
                isValid = true;
                for (Node node : root) {
                    if (node.getType() == NodeType.STATE) {
                        if (((StateNode) node).getLabel().equals(select.getLabel())) {
                            isValid = false;
                            break;
                        }
                    }
                }
                intents++;
            } while (!isValid && intents < filteredStates.size());
        } else {
            select = (StateNode) filteredStates.get(random.nextInt(filteredStates.size()));
        }
        select = (StateNode) select.copy();
        select.setEditable(true);
        select.setByGenerator(g.getId());
        return select;
    }

    public static void main(String[] args) throws OperatorException {
        PredicateGenerator predicateGenerator = new PredicateGenerator();
        ArrayList<StateNode> states = new ArrayList<>();
        states.add(new StateNode("citric_acid", "citric_acid"));
        states.add(new StateNode("volatile_acidity", "volatile_acidity"));
        states.add(new StateNode("fixed_acidity", "fixed_acidity"));
        states.add(new StateNode("free_sulfur_dioxide", "free_sulfur_dioxide"));
        states.add(new StateNode("sulphates", "sulphates"));
        states.add(new StateNode("alcohol", "alcohol"));
        states.add(new StateNode("residual_sugar", "residual_sugar"));
        states.add(new StateNode("pH", "pH"));
        states.add(new StateNode("total_sulfur_dioxide", "total_sulfur_dioxide"));
        states.add(new StateNode("quality", "quality"));
        states.add(new StateNode("density", "density"));
        states.add(new StateNode("chlorides", "chlorides"));

        ArrayList<Node> var_acid = new ArrayList<>();
        for (StateNode string : states) {
            if (string.getLabel().equalsIgnoreCase("citric_acid")) {
                var_acid.add(string);
            }
            if (string.getLabel().equalsIgnoreCase("volatile_acidity")) {
                var_acid.add(string);
            }
            if (string.getLabel().equalsIgnoreCase("fixed_acidity")) {
                var_acid.add(string);
            }
        }
        GeneratorNode acidos = new GeneratorNode("acidos", new NodeType[] { NodeType.AND, NodeType.EQV }, var_acid, 2);

        ArrayList<Node> variables = new ArrayList<>();
        variables.add(acidos);
        for (StateNode stateNode : states) {
            if (!var_acid.contains(stateNode))
                variables.add(stateNode);
        }
        GeneratorNode all = new GeneratorNode("todos los estados",
                new NodeType[] { NodeType.IMP, NodeType.OR }, variables, 2);
        variables.add(acidos);
        ArrayList<Node> trees = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Node _g = predicateGenerator.generate(all,i < 2 / 2);
            trees.add(_g);
        }
        ArrayList<Node> copies = new ArrayList<>();

        for (int i = 0; i < trees.size(); i++) {
            Node n = trees.get(i);
            System.out.println(n);
            copies.add((Node) n.copy());
        }
        System.out.println("check copy");

        for (int i = 0; i < copies.size(); i++) {
            Node c = copies.get(i);
            Node o = trees.get(i);

            if (o instanceof NodeTree) {
                ArrayList<Node> a = NodeTree.getEditableNodes((NodeTree) o);
                ArrayList<Node> a_c = NodeTree.getEditableNodes((NodeTree) c);
                if (a.size() != a_c.size()) {
                    System.out.println(a.size() + " " + a_c.size());
                    System.out.println(o + " <> " + c);
                }

            }
        }
    }
}
