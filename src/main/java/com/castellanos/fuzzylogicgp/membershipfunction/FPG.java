/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.membershipfunction;

import static java.lang.Math.pow;

/**
 *
 * @author hp
 */
public class FPG extends AMembershipFunction {

    private Double gamma;
    private Double beta;
    private Double m;

    public FPG(Double gamma, Double beta, Double m) {
        this.gamma = gamma;
        this.beta = beta;
        this.m = m;
        this.setType(MembershipFunctionType.FPG);

    }

    public FPG() {

        this.setType(MembershipFunctionType.FPG);

    }

    public Double getGamma() {
        return gamma;
    }

    public void setGamma(Double gamma) {
        this.gamma = gamma;
    }

    public Double getBeta() {
        return beta;
    }

    public void setBeta(Double beta) {
        this.beta = beta;
    }

    public Double getM() {
        return m;
    }

    public void setM(Double m) {
        this.m = m;
    }

    @Override
    public boolean isValid() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        return "FPG " + this.beta + " " + this.gamma + " " + this.m;
    }

    @Override
    public double evaluate(double v) {
        double sigm, sigmm, M;

        sigm = pow(new Sigmoid(gamma, beta).evaluate(v), m);
        sigmm = pow(1.0 - new Sigmoid(gamma, beta).evaluate(v), 1.0 - m);
        M = pow(m, m) * pow((1 - m), (1 - m));

        return ((sigm * sigmm) / M);
    }

}
