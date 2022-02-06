package com.castellanos94.fuzzylogicgp.logic;

import java.util.List;

import com.google.gson.annotations.Expose;

public class GMBCFALogic extends GMBCLogic {
    @Expose
    private int exponent;

    public void setExponent(int exponent) {
        this.exponent = exponent;
    }

    public GMBCFALogic(int coefficient, ImplicationType implicationType) {
        super(implicationType);
        this.exponent = coefficient;
    }

    public GMBCFALogic() {
        this(3, ImplicationType.Zadeh);
    }

    @Override
    public double forAll(List<Double> values) {
        double pe = 0.0;
        for (double v : values) {
            if (v != 0) {
                pe += Math.log(v);
            } else {
                return 0;
            }
        }
        pe /= values.size();
        double r = 0;
        for (double v : values) {
            r += (v - pe) * (v - pe);
        }
        r = Math.sqrt(r / values.size());
        return Math.exp(pe - exponent * r);
    }
    @Override
    public String toString() {
        return this.getClass().getName() + " " + implicationType + " "+exponent;
    }
}
