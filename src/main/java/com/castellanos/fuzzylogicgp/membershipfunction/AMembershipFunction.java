/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.membershipfunction;

import java.io.Serializable;

/**
 *
 * @author hp
 */
public abstract class AMembershipFunction implements Cloneable , Serializable{

    /**
     *
     */
    private static final long serialVersionUID = -9006368296289781684L;
    public AMembershipFunction(){}
    public MembershipFunctionType type;

    public abstract boolean isValid();
  
    public MembershipFunctionType getType() {
        return type;
    }

    public void setType(MembershipFunctionType type) {
        this.type = type;
    }
        
    public abstract double evaluate(double v);
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
