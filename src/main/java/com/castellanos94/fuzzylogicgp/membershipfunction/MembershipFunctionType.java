/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos94.fuzzylogicgp.membershipfunction;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author hp
 */
public enum MembershipFunctionType {
    @SerializedName("gclv")
    GCLV("gclv"), 
    @SerializedName("fpg")
    FPG("fpg"), 
    @SerializedName("sigmoid")
    SIGMOID("sigmoid"), 
    @SerializedName("-sigmoid")
    NSIGMOID("-sigmoid"), 
    @SerializedName("singleton")
    SINGLETON("singleton"),
    @SerializedName("map-nominal")
    MAPNOMIAL("map-nominal"), 
    @SerializedName("triangular")
    TRIANGULAR("triangular"), 
    @SerializedName("trapezoidal")
    TRAPEZOIDAL("trapezoidal"), 
    @SerializedName("rtrapezoidal")
    RTRAPEZOIDAL("rtrapezoidal"),
    @SerializedName("ltrapezoidal")
    LTRAPEZOIDAL("ltrapezoidal"), 
    @SerializedName("gamma")
    GAMMA("gamma"), 
    @SerializedName("lgamma")
    LGAMMA("lgamma"), 
    @SerializedName("pseudo-exp")
    PSEUDOEXP("pseudo-exp"), 
    @SerializedName("gaussian")
    GAUSSIAN("gaussian"),
    @SerializedName("zform")
    ZFORM("zform"), 
    @SerializedName("sform")
    SFORM("sform"), 
    @SerializedName("nominal")
    NOMINAL("nominal"), 
    @SerializedName("gbell")
    GBELL("gbell");

    private final String str;

    private MembershipFunctionType(String str) {
        this.str = str.toLowerCase();
    }
    
    @Override
    public String toString() {
        return this.str;
    }

}
