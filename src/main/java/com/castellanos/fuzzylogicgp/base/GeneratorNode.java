/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.annotations.Expose;

/**
 *
 * @author hp
 */
public class GeneratorNode extends Node {

    /**
     *
     */
    private static final long serialVersionUID = -5456955267782382254L;
    @Expose
    private String label;
    @Expose
    private int depth;
    @Expose
    private int max_child_number;
    @Expose
    private NodeType operators[];
    @Expose
    private List<String> variables;

    public GeneratorNode() {
        this.setType(NodeType.OPERATOR);
        this.setEditable(true);
        depth = 2;
        max_child_number = 2;
    }

    public GeneratorNode(String label, NodeType[] operators, List<String> variables, int depth) {
        this(label, operators, variables, depth, operators.length / 2 + variables.size());
    }

    public GeneratorNode(String label, NodeType[] operators, List<String> variables, int depth, int max_child_number) {
        this.label = label;
        this.operators = operators;
        this.variables = variables;
        this.depth = depth;
        this.max_child_number = (max_child_number > 0) ? max_child_number : 2;
        this.setType(NodeType.OPERATOR);
        this.setEditable(true);
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public NodeType[] getOperators() {
        return operators;
    }

    public void setOperators(NodeType[] operators) {
        this.operators = operators;
    }

    public int getMax_child_number() {
        return max_child_number;
    }

    public void setMax_child_number(int max_child_number) {
        this.max_child_number = max_child_number;
    }

    public List<String> getVariables() {
        return variables;
    }

    public void setVariables(List<String> variables) {
        this.variables = variables;
    }

    @Override
    public Object copy() {
        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + depth;
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        result = prime * result + Arrays.hashCode(operators);
        result = prime * result + ((variables == null) ? 0 : variables.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GeneratorNode other = (GeneratorNode) obj;
        if (depth != other.depth)
            return false;
        if (label == null) {
            if (other.label != null)
                return false;
        } else if (!label.equals(other.label))
            return false;
        if (!Arrays.equals(operators, other.operators))
            return false;
        if (variables == null) {
            if (other.variables != null)
                return false;
        } else if (!variables.equals(other.variables))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "GeneratorNode [depth=" + depth + ", label=" + label + ", operators=" + Arrays.toString(operators)
                + ", variables=" + variables + "]";
    }

    public Node generate(List<StateNode> states, boolean balanced) throws OperatorException {
        return generate_child(null, states, 0, balanced);

    }

    private Node generate_child(NodeTree root, List<StateNode> states, int current_depth, boolean balanced)
            throws OperatorException {
        if (current_depth < depth) {
            if (Utils.random.nextDouble() < 0.75 || balanced) {
                NodeTree tree = new NodeTree(operators[Utils.random.nextInt(operators.length)]);
                tree.setEditable(true);
                tree.setByGenerator(this.getId());
                switch (tree.getType()) {
                    case AND:
                    case OR:
                        for (int i = 0; i < Utils.randInt(2, max_child_number); i++) {
                            tree.addChild(this.generate_child(tree, states, current_depth + 1, balanced));
                        }
                        return tree;
                    case IMP:
                    case EQV:
                        for (int i = 0; i < 2; i++) {
                            tree.addChild(this.generate_child(tree, states, current_depth + 1, balanced));
                        }
                        return tree;
                    case NOT:
                        tree.addChild(this.generate_child(tree, states, current_depth + 1, balanced));
                        return tree;
                    default:
                        throw new IllegalArgumentException("Unsupported type: " + tree.getType());
                }
            } else {
                return this.generate_state(root, states);
            }
        }
        return this.generate_state(root, states);
    }

    private Node generate_state(NodeTree root, List<StateNode> states) {
        StateNode select;
        boolean isValid;
        int intents = 0;
        if (root != null) {
            do {
                select = states.get(Utils.random.nextInt(states.size()));
                isValid = true;
                for (int i = 0; i < root.getChildrens().size(); i++) {
                    if (root.getChildrens().get(i).getType() == NodeType.STATE) {
                        if (((StateNode) root.getChildrens().get(i)).getLabel().equals(select.getLabel())) {
                            isValid = false;
                            break;
                        }
                    }
                }
                intents++;
            } while (!isValid && intents < states.size());
        } else {
            select = states.get(Utils.random.nextInt(states.size()));
        }
        select = (StateNode) select.copy();
        select.setEditable(true);
        select.setByGenerator(this.getId());
        return select;
    }

    /*
     * private void complete_tree(NodeTree p, GeneratorNode gNode, Node father, int
     * arity, int currentDepth) throws OperatorException {
     * 
     * boolean isToReplace = false; if (father == null &&
     * !(p.getType().equals(NodeType.OPERATOR))) { NodeTree find =
     * NodeTree.getNodeParent(p, gNode.getId()); if (find != null) { father = find;
     * isToReplace = true; } }
     * 
     * if (currentDepth >= depth && !isToReplace) { int size =
     * statesByGenerators.get(gNode.getId()).size(); StateNode select =
     * statesByGenerators.get(gNode.getId()).get(rand.nextInt(size)); if (size >= 2
     * && father != null) { ArrayList<Node> childs = ((NodeTree)
     * father).getChildrens(); boolean contains = false; int intents = 1; do { for
     * (Node node : childs) { if (node instanceof StateNode) { StateNode st =
     * ((StateNode) node); if (st.getLabel().equals(select.getLabel())) { contains =
     * true; } } } if (contains) select =
     * statesByGenerators.get(gNode.getId()).get(rand.nextInt(size)); intents++; }
     * while (contains && intents < size); } StateNode s = (StateNode)
     * select.copy(); s.setByGenerator(gNode.getId()); s.setEditable(true); //
     * s.setFather(father.getId()); ((NodeTree) father).addChild(s);
     * 
     * } else { int max = gNode.getVariables().size(); max = (int) (((float) max / 2
     * >= 2) ? (float) max / 2 : max); arity = rand.nextInt(max); if (arity < 2) {
     * arity = 2; }
     * 
     * Node newFather; NodeType nType =
     * gNode.getOperators()[rand.nextInt(gNode.getOperators().length)]; switch
     * (nType) { case AND: newFather = new NodeTree(NodeType.AND); break; case OR:
     * newFather = new NodeTree(NodeType.OR); break; case IMP: newFather = new
     * NodeTree(NodeType.IMP); arity = 2; break; case EQV: newFather = new
     * NodeTree(NodeType.EQV); arity = 2; break; case NOT: newFather = new
     * NodeTree(NodeType.NOT); arity = 1; break; default: newFather = null; }
     * newFather.setEditable(true); newFather.setByGenerator(gNode.getId());
     * 
     * if (father == null || isToReplace) { NodeTree.replace(p, gNode, newFather,
     * !isToReplace); if (!isToReplace) { newFather = p; // currentDepth=-1; }
     * 
     * } else ((NodeTree) father).addChild(newFather); for (int i = 0; i < arity;
     * i++) complete_tree(p, gNode, newFather, arity, currentDepth + 1); } }
     * 
     * private void growTree(NodeTree p, GeneratorNode gNode, Node father, int
     * arity, int currentDepth) throws OperatorException {
     * 
     * boolean isToReplace = false; if (father == null &&
     * !p.getType().equals(NodeType.OPERATOR)) { NodeTree find =
     * NodeTree.getNodeParent(p, gNode.getId()); if (find != null) { father = find;
     * isToReplace = true; } }
     * 
     * if ((currentDepth >= depth || rand.nextDouble() < 0.65) && (father != null &&
     * currentDepth != 0)) { int size =
     * statesByGenerators.get(gNode.getId()).size(); StateNode select =
     * statesByGenerators.get(gNode.getId()).get(rand.nextInt(size)); if (size >= 2
     * && father != null) {
     * 
     * ArrayList<Node> childs = ((NodeTree) father).getChildrens(); boolean contains
     * = false; int intents = 1; do { for (Node node : childs) { if (node instanceof
     * StateNode) { StateNode st = ((StateNode) node); if
     * (st.getLabel().equals(select.getLabel())) { contains = true; } } } if
     * (contains) select =
     * statesByGenerators.get(gNode.getId()).get(rand.nextInt(size)); intents++; }
     * while (contains && intents < size); } StateNode s = (StateNode)
     * select.copy(); s.setByGenerator(gNode.getId()); s.setEditable(true);
     * 
     * if (isToReplace) { NodeTree.replace(p, gNode, s, !isToReplace); } else {
     * ((NodeTree) father).addChild(s); }
     * 
     * } else { int max = gNode.getVariables().size(); max = (int) (((float) max / 2
     * >= 2) ? (float) max / 2 : max); arity = rand.nextInt(max); if (arity < 2) {
     * arity = 2; }
     * 
     * Node newFather; NodeType nType =
     * gNode.getOperators()[rand.nextInt(gNode.getOperators().length)]; switch
     * (nType) { case AND: newFather = new NodeTree(NodeType.AND); break; case OR:
     * newFather = new NodeTree(NodeType.OR); break; case IMP: newFather = new
     * NodeTree(NodeType.IMP); arity = 2; break; case EQV: newFather = new
     * NodeTree(NodeType.EQV); arity = 2; break; case NOT: newFather = new
     * NodeTree(NodeType.NOT); arity = 1; break; default: newFather = null; }
     * newFather.setEditable(true); newFather.setByGenerator(gNode.getId()); if
     * (father == null || isToReplace) { NodeTree.replace(p, gNode, newFather,
     * !isToReplace); if (!isToReplace) { newFather = p; // currentDepth=-1; }
     * 
     * } else ((NodeTree) father).addChild(newFather); for (int i = 0; i < arity;
     * i++) growTree(p, gNode, newFather, arity, currentDepth + 1); } }
     */
    public static void main(String[] args) throws OperatorException {
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
        GeneratorNode generator = new GeneratorNode();
        generator.setLabel("todos los estados");
        ArrayList<String> variables = new ArrayList<>();
        for (StateNode stateNode : states) {
            variables.add(stateNode.getLabel());
        }
        generator.setVariables(variables);
        generator.setOperators(new NodeType[] { NodeType.AND, NodeType.OR, NodeType.IMP, NodeType.EQV, NodeType.NOT });
        Utils.random.setSeed(1);
        for (int i = 0; i < 20; i++) {
            System.out.println(i+" "+generator.generate(states, i < 20 / 2));
        }
    }
}
