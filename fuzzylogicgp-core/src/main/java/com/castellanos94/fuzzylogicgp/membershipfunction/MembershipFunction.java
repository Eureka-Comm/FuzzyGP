/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos94.fuzzylogicgp.membershipfunction;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;

/**
 *
 * @author hp
 */
public abstract class MembershipFunction implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -9006368296289781684L;
    protected boolean editable;

    public MembershipFunction(MembershipFunctionType type) {
        this.type = type;
    }

    public MembershipFunction() {
        this(null);
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isEditable() {
        return editable;
    }

    @Expose
    public MembershipFunctionType type;

    public abstract boolean isValid();

    public MembershipFunctionType getType() {
        return type;
    }

    public void setType(MembershipFunctionType type) {
        this.type = type;
    }

    public double evaluate(Number v) {
        throw new UnsupportedOperationException("[" + this.type + "]: Not supported yet for number values.");
    }

    public double evaluate(String key) {
        throw new UnsupportedOperationException("[" + this.type + "]: Not supported yet for string values.");
    }

    public abstract List<Point> getPoints();

    public Double partialDerivate(double value, String partial_params) {
        throw new UnsupportedOperationException("[" + this.type + "]: Not supported yet.");
    }

    public abstract MembershipFunction copy();

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    /**
     * method for assigning properties to an sorted array
     * 
     * @return array values
     */
    public Double[] toArray() {
        throw new UnsupportedOperationException("Not supported for " + type);
    }
}
