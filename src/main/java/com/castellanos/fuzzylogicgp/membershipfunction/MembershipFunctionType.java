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
    MAPNOMIAL("map-nominal"), TRIANGULAR("triangular"), TRAPEZOIDAL("trapezoidal"), RTRAPEZOIDAL("Rtrapezoidal"),
    LTRAPEZOIDAL("Ltrapezoidal"), GAMMA("gamma"), LGAMMA("Lgamma"), PSEUDOEXP("pseudo-exp"), GAUSSIAN("gaussian"),
    ZFORM("Zform"), SFORM("Sfomr"), NOMINAL("nomial"), GBELL("gbell");

    private final String str;

    private MembershipFunctionType(String str) {
        this.str = str;
    }

    @Override
    public String toString() {
        return this.str;
    }

}
