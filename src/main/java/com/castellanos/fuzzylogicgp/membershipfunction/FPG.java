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
public class FPG extends AMembershipFunction {

    private Double gamma;
    private Double beta;
    private Double m;

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

    public FPG(Double gamma, Double beta, Double m) {
        this.gamma = gamma;
        this.beta = beta;
        this.m = m;
    }


    @Override
    public boolean isValid() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        if(this.beta == null)
            return "fpg";
        return "fpg " + this.gamma + ", " + this.beta + ", " + this.m;

    }

}
