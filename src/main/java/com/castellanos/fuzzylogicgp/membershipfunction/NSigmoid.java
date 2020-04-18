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

    /**
     *
     */
    private static final long serialVersionUID = -3118936100100373869L;
    private double center;
    private double beta;

    public NSigmoid(double center, double beta) {
        this.center = center;
        this.beta = beta;
        this.setType(MembershipFunctionType.NSIGMOID);
    }

    public NSigmoid(String center, String beta) {
        this.center = Double.parseDouble(center);
        this.beta = Double.parseDouble(beta);
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
    public double evaluate(double v) {
        return (1-(1/(1+(Math.exp(-((Math.log(0.99)-Math.log(0.01))/(center-beta))*(v-center))))));

        /*BigDecimal lg99 = BigDecimalMath.log(new BigDecimal("0.99"), MathContext.DECIMAL128);
        BigDecimal lg01 = BigDecimalMath.log(new BigDecimal("0.01"), MathContext.DECIMAL128);
        BigDecimal one = new BigDecimal("1");
        BigDecimal vexp = BigDecimalMath
                .exp(((((lg99.subtract(lg01)).divide(center.subtract(beta), MathContext.DECIMAL128)).negate())
                        .multiply(v.subtract(center))), MathContext.DECIMAL128);

        return one.min(one.divide(one.add(vexp), MathContext.DECIMAL128));*/
    }

    public double getCenter() {
        return center;
    }

    public void setCenter(double center) {
        this.center = center;
    }

    public double getBeta() {
        return beta;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }
    @Override
    public Object clone() throws CloneNotSupportedException {
        NSigmoid ns = (NSigmoid) super.clone();
        /*ns.setBeta(new BigDecimal(this.getBeta().toString()));
        ns.setCenter(new BigDecimal(this.getCenter().toString()));*/
        return ns;
    }
}
