/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.membershipfunction;

import com.google.gson.annotations.Expose;
import static java.lang.Math.*;
import tech.tablesaw.api.DoubleColumn;

/**
 *
 * @author hp
 */
public class Sigmoid_MF extends MembershipFunction {

    /**
     *
     */
    private static final long serialVersionUID = 8054075265710944588L;
    @Expose
    private double center;
    @Expose
    private double beta;

    public Sigmoid_MF( double center,  double beta) {
        this.center = center;
        this.beta = beta;
        this.setType(MembershipFunctionType.SIGMOID);
    }

    public Sigmoid_MF( String center,  String beta) {
        this.center = Double.parseDouble(center );
        this.beta = Double.parseDouble(beta);
        this.setType(MembershipFunctionType.SIGMOID);
    }

    @Override
    public String toString() {
        return "sigmoid " + this.center + ", " + this.beta;
    }

    public Double partialDerivate(double value, String partial_parameter){
        if(partial_parameter.equals("beta"))
            return  -1 * (center * exp(center * (beta + value))) / pow((exp(beta*center) + exp(center*value)), 2);
        else if (partial_parameter.equals("center"))
            return ((value - beta) * exp(center * (value - beta))) / pow((exp(center * (value - center))) + 1, 2);
        else return 0.0;
    }

    @Override
    public double evaluate(Number value) {
        Double v = value.doubleValue();
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
        return new Sigmoid_MF(center, beta);
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
        Sigmoid_MF other = (Sigmoid_MF) obj;
        if (Double.doubleToLongBits(beta) != Double.doubleToLongBits(other.beta))
            return false;
        if (Double.doubleToLongBits(center) != Double.doubleToLongBits(other.center))
            return false;
        return true;
    }
    @Override
    public DoubleColumn xPoints() {
        DoubleColumn xColumn = DoubleColumn.create("x column");
        for (double i = 0; i < center*2; i+=0.01) {
            xColumn.append(i);
        }
        return xColumn;
    }
    @Override
    public DoubleColumn yPoints() {
        DoubleColumn yColumn = DoubleColumn.create("y column");
        for (double i = 0; i < center*2; i+=0.01) {
            yColumn.append(this.evaluate(i));
        }
        return yColumn;
    }


}
