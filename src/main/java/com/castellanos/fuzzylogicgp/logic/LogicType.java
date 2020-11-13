package com.castellanos.fuzzylogicgp.logic;

import com.google.gson.annotations.SerializedName;

public enum LogicType {
    @SerializedName("gmbc")
    GMBC, 
    @SerializedName("ambc")
    AMBC, 
    @SerializedName("zadeh")
    ZADEH, 
    @SerializedName("acf")
    ACF;
}