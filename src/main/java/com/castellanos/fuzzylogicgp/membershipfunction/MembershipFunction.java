/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.membershipfunction;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;

import tech.tablesaw.columns.Column;

/**
 *
 * @author hp
 */
public abstract class MembershipFunction implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -9006368296289781684L;

    public MembershipFunction(MembershipFunctionType type) {
        this.type = type;
    }

    public MembershipFunction() {
        this(null);
    }

    @Expose
    public MembershipFunctionType type;

    public boolean isValid() {
        throw new UnsupportedOperationException("[" + this.type + "]: Not supported yet.");
    }

    public MembershipFunctionType getType() {
        return type;
    }

    public void setType(MembershipFunctionType type) {
        this.type = type;
    }

    public double evaluate(Number v) {
        throw new UnsupportedOperationException("[" + this.type + "]: Not supported yet.");
    }

    public double evaluate(String key) {
        throw new UnsupportedOperationException("[" + this.type + "]: Not supported yet.");
    }

    public abstract List<Point> getPoints();

    public Double partialDerivate(double value, String partial_params) {
        throw new UnsupportedOperationException("[" + this.type + "]: Not supported yet.");
    }

    public MembershipFunction copy() {
        throw new UnsupportedOperationException("[" + this.type + "]: Not supported yet.");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

}
