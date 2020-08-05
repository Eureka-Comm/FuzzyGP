/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.membershipfunction;

import com.google.gson.annotations.Expose;

import tech.tablesaw.api.DoubleColumn;

/**
 *
 * @author hp
 */
public class NSigmoid_MF extends MembershipFunction {

    /**
     *
     */
    private static final long serialVersionUID = -3118936100100373869L;
    @Expose
    private Double center;
    @Expose
    private Double beta;

    @Override
    public boolean isValid() {
        return !( center == null || beta == null);
    }

    public NSigmoid_MF(double center, double beta) {
        this.center = center;
        this.beta = beta;
        this.setType(MembershipFunctionType.NSIGMOID);
    }

    public NSigmoid_MF(String center, String beta) {
        this.center = Double.parseDouble(center);
        this.beta = Double.parseDouble(beta);
        this.setType(MembershipFunctionType.NSIGMOID);
    }
    public NSigmoid_MF(){
        this.setType(MembershipFunctionType.NSIGMOID);
    }
    @Override
    public String toString() {
        return "nsigmoid " + this.center + ", " + this.beta;
    }

    @Override
    public double evaluate(Number value) {
        Double v = value.doubleValue();
        return (1-(1/(1+(Math.exp(-((Math.log(0.99)-Math.log(0.01))/(center-beta))*(v-center))))));

        /*BigDecimal lg99 = BigDecimalMath.log(new BigDecimal("0.99"), MathContext.DECIMAL128);
        BigDecimal lg01 = BigDecimalMath.log(new BigDecimal("0.01"), MathContext.DECIMAL128);
        BigDecimal one = new BigDecimal("1");
        BigDecimal vexp = BigDecimalMath
                .exp(((((lg99.subtract(lg01)).divide(center.subtract(beta), MathContext.DECIMAL128)).negate())
                        .multiply(v.subtract(center))), MathContext.DECIMAL128);

        return one.min(one.divide(one.add(vexp), MathContext.DECIMAL128));*/
    }

    public Double getCenter() {
        return center;
    }

    public void setCenter(double center) {
        this.center = center;
    }

    public Double getBeta() {
        return beta;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }
    @Override
    public Object clone() throws CloneNotSupportedException {
        return new NSigmoid_MF(center, beta);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(beta);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(center);
        result = prime * result + (int) (temp ^ (temp >>> 32));
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
        NSigmoid_MF other = (NSigmoid_MF) obj;
        if (Double.doubleToLongBits(beta) != Double.doubleToLongBits(other.beta))
            return false;
        if (Double.doubleToLongBits(center) != Double.doubleToLongBits(other.center))
            return false;
        return true;
    }

    @Override
    public DoubleColumn xPoints() {
        DoubleColumn xColumn = DoubleColumn.create("x column");
        for (double i = 0; i < center*2; i+=0.1) {
            xColumn.append(i);
        }
        return xColumn;
    }
    @Override
    public DoubleColumn yPoints() {
        DoubleColumn yColumn = DoubleColumn.create("y column");
        for (double i = 0; i < center*2; i+=0.1) {
            yColumn.append(this.evaluate(i));
        }
        return yColumn;
    }
    
}
