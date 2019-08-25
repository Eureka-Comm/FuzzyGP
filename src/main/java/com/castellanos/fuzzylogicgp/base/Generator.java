/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.base;

import java.util.List;

/**
 *
 * @author hp
 */
public class Generator extends Node {

    private String label;
    private NodeType operators[];
    private List<String> variables;

    public Generator() {
        this.setType(NodeType.OPERATOR);
        this.setEditable(true);
    }

    public Generator(String label, NodeType[] operators, List<String> variables) {
        this.label = label;
        this.operators = operators;
        this.variables = variables;
        this.setType(NodeType.OPERATOR);
        this.setEditable(true);
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

    public List<String> getVariables() {
        return variables;
    }

    public void setVariables(List<String> variables) {
        this.variables = variables;
    }

    @Override
    public String toString() {
        return this.label;
    }

}
