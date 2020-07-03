/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.membershipfunction;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

import tech.tablesaw.columns.Column;

/**
 *
 * @author hp
 */
public abstract class MembershipFunction implements Cloneable, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -9006368296289781684L;

    public MembershipFunction() {
    }

    @Expose
    public MembershipFunctionType type;

    public boolean isValid(){
        throw new UnsupportedOperationException("["+this.type+"]: Not supported yet."); 
    }

    public MembershipFunctionType getType() {
        return type;
    }

    public void setType(MembershipFunctionType type) {
        this.type = type;
    }

    public double evaluate(double v){
        throw new UnsupportedOperationException("["+this.type+"]: Not supported yet."); 
    }

    public  double evaluate(String key){
        throw new UnsupportedOperationException("["+this.type+"]: Not supported yet."); 
    }
    public abstract Column yPoints();

    public abstract Column xPoints();
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

}