package com.castellanos94.fuzzylogicgp.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.annotations.Expose;

public class DummyGenerator extends Node {
    @Expose
    private int depth;
    @Expose
    private Integer max_child_number;
    @Expose
    private NodeType operators[];
    @Expose
    private List<String> variables;

    public DummyGenerator() {
        this.setType(NodeType.DUMMYGENERATOR);
        this.setEditable(true);
    }

    protected DummyGenerator(String id,String label, NodeType[] operators, List<String> variables, int depth) {
        super(id);
        this.label = label;
        this.operators = operators;
        if (this.operators.length == 0) {
            this.operators = new NodeType[] { NodeType.AND, NodeType.OR, NodeType.IMP, NodeType.EQV, NodeType.NOT };
        }
        this.variables = variables;
        this.depth = depth;
        this.max_child_number = Math.max(max_child_number, 3);
        this.setType(NodeType.DUMMYGENERATOR);
        this.setEditable(true);       
    }
    @Override
    DummyGenerator withId(String id) {
        return new DummyGenerator(id, this.label, this.operators, this.variables, this.depth);
    }

    public DummyGenerator(String label, NodeType[] operators, List<String> variables, int depth) {
        this(label, operators, variables, depth, operators.length + variables.size() / 2);
    }

    public DummyGenerator(String label, NodeType[] operators, List<String> variables, int depth, int max_child_number) {
        this.label = label;
        this.operators = operators;
        if (this.operators.length == 0) {
            this.operators = new NodeType[] { NodeType.AND, NodeType.OR, NodeType.IMP, NodeType.EQV, NodeType.NOT };
        }
        this.variables = variables;
        this.depth = depth;
        this.max_child_number = Math.max(max_child_number, 3);
        this.setType(NodeType.DUMMYGENERATOR);
        this.setEditable(true);
    }

    @Override
    public DummyGenerator copy() {
        return this;
    }

    @Override
    public String toString() {
        return "DummyGeneratorNode [depth=" + depth + ", label=" + label + ", operators=" + Arrays.toString(operators)
                + ", variables=" + variables + "]";
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public Integer getMax_child_number() {
        return max_child_number;
    }

    public void setMax_child_number(Integer max_child_number) {
        this.max_child_number = max_child_number;
    }

    public NodeType[] getOperators() {
        return operators;
    }

    public void setOperators(NodeType[] operators) {
        this.operators = operators;
    }

    public List<String> getVariables() {
        return variables;
    }

    public void setVariables(List<String> variables) {
        this.variables = variables;
    }

    public GeneratorNode toGeneratorNode(ArrayList<Node> nodes) {
        ArrayList<Node> filter = new ArrayList<>();
        nodes.stream().filter(node -> variables.contains(node.getLabel())).collect(Collectors.toList())
                .forEach(filter::add);
        return new GeneratorNode(label, operators, filter, depth, (max_child_number != null) ? max_child_number : -1);
    }
}
