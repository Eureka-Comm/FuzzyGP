/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.membershipfunction;

import java.math.BigDecimal;
import java.math.MathContext;

import ch.obermuhlner.math.big.BigDecimalMath;

/**
 *
 * @author hp
 */
public class Sigmoid extends AMembershipFunction {

    private BigDecimal center;
    private BigDecimal beta;

    public Sigmoid(final BigDecimal center, final BigDecimal beta) {
        this.center = center;
        this.beta = beta;
        this.setType(MembershipFunctionType.SIGMOID);
    }

    public Sigmoid(final String center, final String beta) {
        this.center = new BigDecimal(center, MathContext.DECIMAL128);
        this.beta = new BigDecimal(beta, MathContext.DECIMAL128);
        this.setType(MembershipFunctionType.SIGMOID);
    }

    @Override
    public boolean isValid() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString() {
        return "sigmoid " + this.center + ", " + this.beta;
    }

    @Override
    public BigDecimal evaluate(final BigDecimal v) {
        BigDecimal lg99 = BigDecimalMath.log(new BigDecimal("0.99"), MathContext.DECIMAL128);
        BigDecimal lg01 = BigDecimalMath.log(new BigDecimal("0.01"), MathContext.DECIMAL128);
        BigDecimal one = new BigDecimal("1");
        BigDecimal vexp = BigDecimalMath
                .exp(((((lg99.subtract(lg01)).divide(center.subtract(beta), MathContext.DECIMAL128)).negate())
                        .multiply(v.subtract(center))), MathContext.DECIMAL128);

        return one.divide(one.add(vexp), MathContext.DECIMAL128);
        // return
        // (1/(1+(Math.exp(-((Math.log(0.99)-Math.log(0.01))/(center-beta))*(v-center)))));

    }

    public BigDecimal getCenter() {
        return center;
    }

    public void setCenter(final BigDecimal center) {
        this.center = center;
    }

    public BigDecimal getBeta() {
        return beta;
    }

    public void setBeta(final BigDecimal beta) {
        this.beta = beta;
    }

}
