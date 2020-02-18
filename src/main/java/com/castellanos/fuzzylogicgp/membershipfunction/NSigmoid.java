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
public class NSigmoid extends AMembershipFunction {

    private BigDecimal center;
    private BigDecimal beta;

    public NSigmoid(BigDecimal center, BigDecimal beta) {
        this.center = center;
        this.beta = beta;
        this.setType(MembershipFunctionType.NSIGMOID);
    }

    public NSigmoid(String center, String beta) {
        this.center = new BigDecimal(center, MathContext.DECIMAL128);
        this.beta = new BigDecimal(beta, MathContext.DECIMAL128);
        this.setType(MembershipFunctionType.NSIGMOID);
    }

    @Override
    public boolean isValid() {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
                                                                       // Tools | Templates.
    }

    @Override
    public String toString() {
        return "nsigmoid " + this.center + ", " + this.beta;
    }

    @Override
    public BigDecimal evaluate(BigDecimal v) {
        // return
        // (1-(1/(1+(Math.exp(-((Math.log(0.99)-Math.log(0.01))/(center-beta))*(v-center))))));

        BigDecimal lg99 = BigDecimalMath.log(new BigDecimal("0.99"), MathContext.DECIMAL128);
        BigDecimal lg01 = BigDecimalMath.log(new BigDecimal("0.01"), MathContext.DECIMAL128);
        BigDecimal one = new BigDecimal("1");
        BigDecimal vexp = BigDecimalMath
                .exp(((((lg99.subtract(lg01)).divide(center.subtract(beta), MathContext.DECIMAL128)).negate())
                        .multiply(v.subtract(center))), MathContext.DECIMAL128);

        return one.min(one.divide(one.add(vexp), MathContext.DECIMAL128));
    }

    public BigDecimal getCenter() {
        return center;
    }

    public void setCenter(BigDecimal center) {
        this.center = center;
    }

    public BigDecimal getBeta() {
        return beta;
    }

    public void setBeta(BigDecimal beta) {
        this.beta = beta;
    }
    @Override
    public Object clone() throws CloneNotSupportedException {
        NSigmoid ns = (NSigmoid) super.clone();
        ns.setBeta(new BigDecimal(this.getBeta().toString()));
        ns.setCenter(new BigDecimal(this.getCenter().toString()));
        return ns;
    }
}
