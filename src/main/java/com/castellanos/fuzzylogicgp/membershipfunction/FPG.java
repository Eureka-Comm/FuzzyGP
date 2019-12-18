/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.membershipfunction;

import static java.lang.Math.pow;

import java.math.BigDecimal;
import java.math.MathContext;

import ch.obermuhlner.math.big.BigDecimalMath;

/**
 *
 * @author hp
 */
public class FPG extends AMembershipFunction {

    private BigDecimal gamma;
    private BigDecimal beta;
    private BigDecimal m;

    public FPG(String gamma, String beta, String m) {
        this.gamma = new BigDecimal(gamma, MathContext.DECIMAL128);
        this.beta = new BigDecimal(beta, MathContext.DECIMAL128);
        this.m = new BigDecimal(m, MathContext.DECIMAL128);
        this.setType(MembershipFunctionType.FPG);
    }

    public FPG(BigDecimal gamma, BigDecimal beta, BigDecimal m) {
        this.gamma = gamma;
        this.beta = beta;
        this.m = m;
        this.setType(MembershipFunctionType.FPG);
    }

    public FPG() {
        this.setType(MembershipFunctionType.FPG);
    }

    @Override
    public boolean isValid() {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
                                                                       // Tools | Templates.
    }

    @Override
    public String toString() {
        return "FPG " + this.beta + " " + this.gamma + " " + this.m;
    }

    @Override
    public BigDecimal evaluate(BigDecimal v) {
        // BigDecimal sigm, sigmm, M;

        /*
         * sigm = pow(new Sigmoid(gamma, beta).evaluate(v), m); sigmm = pow(1.0 - new
         * Sigmoid(gamma, beta).evaluate(v), 1.0 - m); M = pow(m, m) * pow((1 - m), (1 -
         * m));
         * 
         * return ((sigm * sigmm) / M);
         */
        BigDecimal one = new BigDecimal("1", MathContext.DECIMAL128);
        BigDecimal calc_sig = new Sigmoid(gamma, beta).evaluate(v);

        // Apfloat apsigm = ApfloatMath.pow(new Apfloat(calc_sig),new Apfloat(m));
        BigDecimal bgsigm = BigDecimalMath.pow(calc_sig, m, MathContext.DECIMAL128);

        // Apfloat apsigmm = ApfloatMath.pow(new Apfloat(one.subtract(calc_sig)), new
        // Apfloat(one.subtract(m)));
        BigDecimal bgsigmm = BigDecimalMath.pow(one.subtract(calc_sig), one.subtract(m), MathContext.DECIMAL128);

        // Apfloat M = ApfloatMath.pow(new Apfloat(m), new
        // Apfloat(m)).multiply(ApfloatMath.pow(new Apfloat(one.subtract(m)), new
        // Apfloat(one.subtract(m))));
        BigDecimal bgM = BigDecimalMath.pow(m, m, MathContext.DECIMAL128)
                .multiply(BigDecimalMath.pow(one.subtract(m), one.subtract(m), MathContext.DECIMAL128));
        return bgsigm.multiply(bgsigmm).divide(bgM, MathContext.DECIMAL128);
    }

    public BigDecimal getGamma() {
        return gamma;
    }

    public void setGamma(BigDecimal gamma) {
        this.gamma = gamma;
    }

    public BigDecimal getBeta() {
        return beta;
    }

    public void setBeta(BigDecimal beta) {
        this.beta = beta;
    }

    public BigDecimal getM() {
        return m;
    }

    public void setM(BigDecimal m) {
        this.m = m;
    }

}
