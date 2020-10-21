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
    private Integer max_child_number;
    @Expose
    private NodeType operators[];
    @Expose
    private List<String> variables;

    public GeneratorNode() {
        this.setType(NodeType.OPERATOR);
        this.setEditable(true);
    }

    public GeneratorNode(String label, NodeType[] operators, List<String> variables, int depth) {
        this(label, operators, variables, depth, operators.length + variables.size() / 2);
    }

    public GeneratorNode(String label, NodeType[] operators, List<String> variables, int depth, int max_child_number) {
        this.label = label;
        this.operators = operators;
        if (this.operators.length == 0) {
            this.operators = new NodeType[] { NodeType.AND, NodeType.OR, NodeType.IMP, NodeType.EQV, NodeType.NOT };
        }
        this.variables = variables;
        this.depth = depth;
        this.max_child_number = Math.max(max_child_number, 3);
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
        if (this.operators.length == 0) {
            this.operators = new NodeType[] { NodeType.AND, NodeType.OR, NodeType.IMP, NodeType.EQV, NodeType.NOT };
        }
    }

    public int getMax_child_number() {
        return max_child_number;
    }

    public void setMax_child_number(int max_child_number) {
        this.max_child_number = Math.max(max_child_number, 3);
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
        if (max_child_number == null) {
            max_child_number = Math.max(operators.length + variables.size() / 2, 3);
        }
        return generate_child(null, states, 0, balanced);

    }

    private Node generate_child(NodeTree root, List<StateNode> states, int current_depth, boolean balanced)
            throws OperatorException {

        if (current_depth < this.depth) {
            if (Utils.random.nextDouble() < 0.45 || balanced) {
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
        generator.setDepth(0);
        Utils.random.setSeed(1);
        for (int i = 0; i < 50; i++) {
            System.out.println(i + " " + generator.generate(states, i < 20 / 2));
        }
    }
}
