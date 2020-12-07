package com.castellanos.fuzzylogicgp.logic;

import java.util.List;

import com.google.gson.annotations.Expose;

public class GMBC_FA_Logic extends GMBC_Logic {
    @Expose
    private int exponent;

    public void setExponent(int exponent) {
        this.exponent = exponent;
    }

    public GMBC_FA_Logic(int coefficient, boolean natural_implication) {
        super(natural_implication);
        this.exponent = coefficient;
    }

    public GMBC_FA_Logic() {
        this(3, false);
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
}
