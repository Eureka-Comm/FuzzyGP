/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos94.fuzzylogicgp.membershipfunction;

import java.awt.geom.Point2D;
import java.util.List;

import com.google.gson.annotations.Expose;

/**
 *
 * @author hp
 */
public class NSigmoid extends MembershipFunction {

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
        return (center != null && beta != null) && center < beta;
    }

    public NSigmoid(double center, double beta) {
        this();
        this.center = center;
        this.beta = beta;
    }

    public NSigmoid(String center, String beta) {
        this(Double.parseDouble(center), Double.parseDouble(beta));
    }

    public NSigmoid() {
        super(MembershipFunctionType.NSIGMOID);
    }

    @Override
    public String toString() {
        return "[-sigmoid " + this.center + " " + this.beta + "]";
    }

    @Override
    public double evaluate(Number value) {
        Double v = value.doubleValue();
        double alpha = (Math.log(0.99) - Math.log(0.01)) / (beta - center);
        return 1.0 - 1.0 / (1 + Math.exp(-alpha * (v - beta)));
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
        NSigmoid other = (NSigmoid) obj;
        if (Double.doubleToLongBits(beta) != Double.doubleToLongBits(other.beta))
            return false;
        if (Double.doubleToLongBits(center) != Double.doubleToLongBits(other.center))
            return false;
        return true;
    }

    @Override
    public List<Point2D> getPoints() {
        double x = -center * beta / 2;
        double step = (center * beta) / 100.0;
        return calculatePoints(step, x);
    }

    @Override
    public MembershipFunction copy() {
        return new NSigmoid(center, beta);
    }
}
