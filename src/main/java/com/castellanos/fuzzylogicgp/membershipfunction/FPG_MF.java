/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.membershipfunction;

import static java.lang.Math.pow;

import com.google.gson.annotations.Expose;

import tech.tablesaw.api.DoubleColumn;

/**
 *
 * @author hp
 */
public class FPG_MF extends MembershipFunction {

    /**
     *
     */
    private static final long serialVersionUID = -7249037051439575667L;
    @Expose
    private Double gamma;
    @Expose
    private Double beta;
    @Expose
    private Double m;

    public FPG_MF(String beta, String gamma, String m) {
        this.gamma = Double.parseDouble(gamma);
        this.beta = Double.parseDouble(beta);
        this.m = Double.parseDouble(m);
        this.setType(MembershipFunctionType.FPG);
    }

    public FPG_MF(Double beta, Double gamma, Double m) {
        this.beta = beta;
        this.gamma = gamma;
        this.m = m;
        this.setType(MembershipFunctionType.FPG);
    }

    public FPG_MF() {
        this.setType(MembershipFunctionType.FPG);
    }


    @Override
    public String toString() {
        return "FPG " + this.beta + " " + this.gamma + " " + this.m;
    }

    @Override
    public double evaluate(Number v) {
        // BigDecimal sigm, sigmm, M;

        double sigm, sigmm, M;
        sigm = pow(new Sigmoid_MF(gamma, beta).evaluate(v), m);
        sigmm = pow(1.0 - new Sigmoid_MF(gamma, beta).evaluate(v), 1.0 - m);
        M = pow(m, m) * pow((1 - m), (1 - m));

        return ((sigm * sigmm) / M);

        /*
         * BigDecimal one = new BigDecimal("1", MathContext.DECIMAL128); BigDecimal
         * calc_sig = new Sigmoid(gamma, beta).evaluate(v);
         * 
         * // Apfloat apsigm = ApfloatMath.pow(new Apfloat(calc_sig),new Apfloat(m));
         * BigDecimal bgsigm = BigDecimalMath.pow(calc_sig, m, MathContext.DECIMAL128);
         * 
         * // Apfloat apsigmm = ApfloatMath.pow(new Apfloat(one.subtract(calc_sig)), new
         * // Apfloat(one.subtract(m))); BigDecimal bgsigmm =
         * BigDecimalMath.pow(one.subtract(calc_sig), one.subtract(m),
         * MathContext.DECIMAL128);
         * 
         * // Apfloat M = ApfloatMath.pow(new Apfloat(m), new //
         * Apfloat(m)).multiply(ApfloatMath.pow(new Apfloat(one.subtract(m)), new //
         * Apfloat(one.subtract(m)))); BigDecimal bgM = BigDecimalMath.pow(m, m,
         * MathContext.DECIMAL128) .multiply(BigDecimalMath.pow(one.subtract(m),
         * one.subtract(m), MathContext.DECIMAL128)); return
         * bgsigm.multiply(bgsigmm).divide(bgM, MathContext.DECIMAL128);
         */
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
    public Object clone() throws CloneNotSupportedException {
        return new FPG_MF(beta, gamma, m);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((beta == null) ? 0 : beta.hashCode());
        result = prime * result + ((gamma == null) ? 0 : gamma.hashCode());
        result = prime * result + ((m == null) ? 0 : m.hashCode());
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
        FPG_MF other = (FPG_MF) obj;
        if (beta == null) {
            if (other.beta != null)
                return false;
        } else if (!beta.equals(other.beta))
            return false;
        if (gamma == null) {
            if (other.gamma != null)
                return false;
        } else if (!gamma.equals(other.gamma))
            return false;
        if (m == null) {
            if (other.m != null)
                return false;
        } else if (!m.equals(other.m))
            return false;
        return true;
    }
    @Override
    public DoubleColumn xPoints() {
        DoubleColumn xColumn = DoubleColumn.create("x column");
        for (double i = 0; i < gamma + beta; i+= (gamma-beta)/10.0) {
            xColumn.append(i);
        }
        return xColumn;
    }
    @Override
    public DoubleColumn yPoints() {
        DoubleColumn yColumn = DoubleColumn.create("y column");
        for (double i = 0; i < gamma +beta; i+= (gamma-beta)/10.0) {
            yColumn.append(this.evaluate(i));
        }
        return yColumn;
    }
}
