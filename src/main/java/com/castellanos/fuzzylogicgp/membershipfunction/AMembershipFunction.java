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
public abstract class AMembershipFunction {

    public MembershipFunctionType type;

    public abstract boolean isValid();

    public MembershipFunctionType getType() {
        return type;
    }

    public void setType(MembershipFunctionType type) {
        this.type = type;
    }
        
    public abstract BigDecimal evaluate(BigDecimal v);
}
