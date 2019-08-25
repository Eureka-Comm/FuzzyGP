/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.membershipfunction;

/**
 *
 * @author hp
 */
public class Singleton extends AMembershipFunction {

    private double a;

    public Singleton(double a) {
        this.a = a;
        this.setType(MembershipFunctionType.SINGLETON);

    }

    public double getA() {
        return a;
    }

    public void setA(double a) {
        this.a = a;
    }

    @Override
    public boolean isValid() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        return "singleton " + this.a;
    }

}
