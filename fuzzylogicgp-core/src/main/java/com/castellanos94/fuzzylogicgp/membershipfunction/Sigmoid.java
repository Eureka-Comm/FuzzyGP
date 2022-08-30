/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos94.fuzzylogicgp.membershipfunction;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

/**
 *
 * @author hp
 */
public class Sigmoid extends MembershipFunction {

    /**
     *
     */
    private static final long serialVersionUID = 8054075265710944588L;
    @Expose
    private Double center;
    @Expose
    private Double beta;

    public Sigmoid(double center, double beta) {
        this.center = center;
        this.beta = beta;
        this.setType(MembershipFunctionType.SIGMOID);
    }

    public Sigmoid(String center, String beta) {
        this.center = Double.parseDouble(center);
        this.beta = Double.parseDouble(beta);
        this.setType(MembershipFunctionType.SIGMOID);
    }

    @Override
    public String toString() {
        return "[sigmoid " + this.center + " " + this.beta + "]";
    }

    @Override
    public double evaluate(Number value) {
        Double v = value.doubleValue();
        return (1 / (1 + (Math.exp(-((Math.log(0.99) - Math.log(0.01)) / (center - beta)) * (v - center)))));

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

    public void setBeta(final double beta) {
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
        Sigmoid other = (Sigmoid) obj;
        if (Double.doubleToLongBits(beta) != Double.doubleToLongBits(other.beta))
            return false;
        if (Double.doubleToLongBits(center) != Double.doubleToLongBits(other.center))
            return false;
        return true;
    }

    @Override
    public MembershipFunction copy() {
        return new Sigmoid(center, beta);
    }

    @Override
    public List<Point> getPoints() {
        ArrayList<Point> points = new ArrayList<>();
        final double step = beta != 0 ? Math.abs(beta) / 100.0 : Math.abs(center) / 100.0;
        double x = center - 3 * center / 2.0;
        while (x <= (center + 5 * center / 4.0)) {
            points.add(new Point(x, evaluate(x)));
            x += step;
        }
        return points;
    }

    @Override
    public boolean isValid() {
        return (center != null && beta != null) && center > beta;
    }

    @Override
    public Double[] toArray() {
        return new Double[] { center, beta };
    }
}
