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
public class Sigmoid extends AMembershipFunction {

    /**
     *
     */
    private static final long serialVersionUID = 8054075265710944588L;
    private double center;
    private double beta;

    public Sigmoid( double center,  double beta) {
        this.center = center;
        this.beta = beta;
        this.setType(MembershipFunctionType.SIGMOID);
    }

    public Sigmoid( String center,  String beta) {
        this.center = Double.parseDouble(center );
        this.beta = Double.parseDouble(beta);
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
    public double evaluate( double v) {
       /* BigDecimal lg99 = BigDecimalMath.log(new BigDecimal("0.99"), MathContext.DECIMAL128);
        BigDecimal lg01 = BigDecimalMath.log(new BigDecimal("0.01"), MathContext.DECIMAL128);
        BigDecimal one = new BigDecimal("1");
        BigDecimal vexp = BigDecimalMath
                .exp(((((lg99.subtract(lg01)).divide(center.subtract(beta), MathContext.DECIMAL128)).negate())
                        .multiply(v.subtract(center))), MathContext.DECIMAL128);

        return one.divide(one.add(vexp), MathContext.DECIMAL128);*/
        return (1/(1+(Math.exp(-((Math.log(0.99)-Math.log(0.01))/(center-beta))*(v-center)))));

    }

    public double getCenter() {
        return center;
    }

    public void setCenter( double center) {
        this.center = center;
    }

    public double getBeta() {
        return beta;
    }

    public void setBeta(final double beta) {
        this.beta = beta;
    }
    @Override
    public Object clone() throws CloneNotSupportedException {
        Sigmoid s = (Sigmoid) super.clone();
        /*s.setBeta(new BigDecimal(this.getBeta().toString()));
        s.setCenter(new BigDecimal(this.getCenter().toString()));*/
        return s;
    }

}
