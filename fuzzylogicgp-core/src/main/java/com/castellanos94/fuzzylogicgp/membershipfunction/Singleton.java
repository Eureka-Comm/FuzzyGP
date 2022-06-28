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
public class Singleton extends MembershipFunction {

    /**
     *
     */
    private static final long serialVersionUID = -4595317229883563585L;
    @Expose
    private Double a;

    public Singleton(double a) {
        this();
        this.a = a;
    }

    public Singleton() {
        this.type = MembershipFunctionType.SINGLETON;
    }

    public double getA() {
        return a;
    }

    public void setA(double a) {
        this.a = a;
    }

    @Override
    public boolean isValid() {
        if (a == null)
            return false;
        return !(a > 1.0 || a < 0.0);
    }

    @Override
    public String toString() {
        return "[singleton " + this.a + "]";
    }

    @Override
    public double evaluate(Number v) {
        return (a == v.doubleValue()) ? 1.0 : 0.0;
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
        Singleton other = (Singleton) obj;
        if (Double.doubleToLongBits(a) != Double.doubleToLongBits(other.a))
            return false;
        return true;
    }

    @Override
    public List<Point> getPoints() {
        ArrayList<Point> point = new ArrayList<>();
        point.add(new Point(a, 1.0));
        point.add(new Point(a, 0));
        return point;
    }

    @Override
    public MembershipFunction copy() {
        return new Singleton(a);
    }

    @Override
    public Double[] toArray() {
        return new Double[] { a };
    }

}
