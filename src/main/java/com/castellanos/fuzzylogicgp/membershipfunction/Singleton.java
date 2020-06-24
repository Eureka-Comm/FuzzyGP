/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.membershipfunction;

import com.google.gson.annotations.Expose;

/**
 *
 * @author hp
 */
public class Singleton extends AMembershipFunction {

    /**
     *
     */
    private static final long serialVersionUID = -4595317229883563585L;
    @Expose
    private double a;

    public Singleton(double a) {
        this.a = a;
        this.setType(MembershipFunctionType.SINGLETON);

    }

    public double getA() {
        return a;
    }

    public void setA(double a) {
        this.a = a;
    }

    @Override
    public boolean isValid() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        return "singleton " + this.a;
    }

    @Override
    public double evaluate(double v) {
        return ( a == v)? 1.0 : 0.0;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    public double evaluate(String key) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

}
