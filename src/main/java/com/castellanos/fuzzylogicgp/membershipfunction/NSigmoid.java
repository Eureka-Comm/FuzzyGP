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
public class NSigmoid extends AMembershipFunction {

    private double center;
    private double beta;

    public NSigmoid(double center, double beta) {
        this.center = center;
        this.beta = beta;
        this.setType(MembershipFunctionType.NSIGMOID);

    }

    public double getBeta() {
        return beta;
    }

    public double getCenter() {
        return center;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }

    public void setCenter(double center) {
        this.center = center;
    }

    @Override
    public boolean isValid() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        return "nsigmoid " + this.center + ", " + this.beta;
    }

    @Override
    public double evaluate(double v) {
        return (1-(1/(1+(Math.exp(-((Math.log(0.99)-Math.log(0.01))/(center-beta))*(v-center))))));
    }
}
