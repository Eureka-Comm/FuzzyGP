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
public enum MembershipFunctionType {
    GCLV("gclv"), FPG("fpg"), SIGMOID("sigmoid"), NSIGMOID("-sigmoid"), SINGLETON("singleton"),
    MAPNOMIAL("map-nominal"), TRIANGULAR("triangular"), TRAPEZOIDAL("trapezoidal"), RTRAPEZOIDAL("rtrapezoidal"),
    LTRAPEZOIDAL("ltrapezoidal"), GAMMA("gamma"), LGAMMA("lgamma"), PSEUDOEXP("pseudo-exp"), GAUSSIAN("gaussian"),
    ZFORM("zform"), SFORM("sform"), NOMINAL("nominal"), GBELL("gbell");

    private final String str;

    private MembershipFunctionType(String str) {
        this.str = str;
    }
    
    @Override
    public String toString() {
        return this.str;
    }

}
