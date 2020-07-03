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
public class Singleton_MF extends MembershipFunction {

    /**
     *
     */
    private static final long serialVersionUID = -4595317229883563585L;
    @Expose
    private double a;

    public Singleton_MF(double a) {
        if (a > 1.0 || a < 0.0) {
            throw new IllegalArgumentException("a must be in [0,1]");
        }
        this.a = a;
        this.setType(MembershipFunctionType.SINGLETON);

    }

    public double getA() {
        return a;
    }

    public void setA(double a) {
        if (a > 1.0 || a < 0.0) {
            throw new IllegalArgumentException("a must be in [0,1]");
        }
        this.a = a;
    }

    @Override
    public boolean isValid() {
        return !(a > 1.0 || a < 0.0);
    }

    @Override
    public String toString() {
        return "singleton " + this.a;
    }

    @Override
    public double evaluate(double v) {
        return (a == v) ? 1.0 : 0.0;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(a);
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
        Singleton_MF other = (Singleton_MF) obj;
        if (Double.doubleToLongBits(a) != Double.doubleToLongBits(other.a))
            return false;
        return true;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new Singleton_MF(a);
    }

    @Override
    public DoubleColumn xPoints() {
        DoubleColumn xColumn = DoubleColumn.create("x column");
        for (double i = 0; i < a * 2; i += 0.1) {
            xColumn.append(i);
        }
        return xColumn;
    }

    @Override
    public DoubleColumn yPoints() {
        DoubleColumn yColumn = DoubleColumn.create("y column");
        for (double i = 0; i < a * 2; i += 0.1) {
            yColumn.append(this.evaluate(i));
        }
        return yColumn;
    }

}
