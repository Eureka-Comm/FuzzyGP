/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.membershipfunction;

import java.math.BigDecimal;

/**
 *
 * @author hp
 */
public class Singleton extends AMembershipFunction {

    private BigDecimal a;

    public Singleton(BigDecimal a) {
        this.a = a;
        this.setType(MembershipFunctionType.SINGLETON);

    }

    public BigDecimal getA() {
        return a;
    }

    public void setA(BigDecimal a) {
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
    public BigDecimal evaluate(BigDecimal v) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
