package com.castellanos.fuzzylogicgp.parser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import com.castellanos.fuzzylogicgp.base.NodeType;
import com.google.gson.annotations.Expose;

public class DummGenerator implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @Expose
    private ArrayList<String> variables;
    @Expose
    private NodeType[] operators;
    @Expose
    private String label;

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

    public ArrayList<String> getVariables() {
        return variables;
    }

    public void setVariables(ArrayList<String> variables) {
        this.variables = variables;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
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
        DummGenerator other = (DummGenerator) obj;
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
        return "DummGenerator [label=" + label + ", operators=" + Arrays.toString(operators) + ", variables="
                + variables + "]";
    }
    
}