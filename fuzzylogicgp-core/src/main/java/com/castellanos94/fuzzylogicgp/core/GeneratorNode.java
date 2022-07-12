/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos94.fuzzylogicgp.core;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
    private int depth;
    @Expose
    private int max_child_number;
    @Expose
    private NodeType operators[];
    @Expose
    private List<Node> variables;

    private Random random;

    public GeneratorNode() {
        this.setType(NodeType.OPERATOR);
        this.setEditable(true);
    }

    public GeneratorNode(String label, NodeType[] operators, List<Node> variables, int depth) {
        this(label, operators, variables, depth, operators.length + variables.size() / 2);
    }

    protected GeneratorNode(String id, String label, NodeType[] operators, List<Node> variables, int depth,
            int max_child_number) {
        super(id);
        this.label = label;
        this.operators = operators;
        if (this.operators.length == 0) {
            this.operators = new NodeType[] { NodeType.AND, NodeType.OR, NodeType.IMP, NodeType.EQV, NodeType.NOT };
        }
        this.variables = variables;
        this.depth = depth;
        this.max_child_number = Math.max(this.operators.length + variables.size() / 2, 2);
        this.setType(NodeType.OPERATOR);
        this.setEditable(true);
    }

    @Override
    Node withId(String id) {
        return new GeneratorNode(id, this.label, this.operators, this.variables, this.depth, this.max_child_number);
    }

    public GeneratorNode(String label, NodeType[] operators, List<Node> variables, int depth, int max_child_number) {
        this.label = label;
        this.operators = operators;
        if (this.operators.length == 0) {
            this.operators = new NodeType[] { NodeType.AND, NodeType.OR, NodeType.IMP, NodeType.EQV, NodeType.NOT };
        }
        this.variables = variables;
        this.depth = depth;
        this.max_child_number = Math.max(this.operators.length + variables.size() / 2, 2);
        this.setType(NodeType.OPERATOR);
        this.setEditable(true);
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
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
        this.max_child_number = max_child_number;
    }

    public List<Node> getVariables() {
        return variables;
    }

    public void setVariables(List<Node> variables) {
        this.variables = variables;
    }

    @Override
    public GeneratorNode copy() {
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

}
