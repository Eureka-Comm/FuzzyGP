package com.castellanos94.fuzzylogicgp.logic;

import java.util.ArrayList;
import java.util.List;

/**
 * Ying, Mingsheng. (2002). Implication operators in fuzzy logic. Fuzzy Systems,
 * IEEE Transactions on. 10. 88 - 91. 10.1109/91.983282.
 */
public class Zadeh_Logic extends Logic {
    public Zadeh_Logic() {
        super();
    }

    @Override
    public void setImplicationType(ImplicationType implicationType) {
        throw new UnsupportedOperationException("Illegal assignment");
    }

    @Override
    public double not(double v1) {
        return 1 - v1;
    }

    @Override
    public double imp(double v1, double v2) {
        return Math.max(1 - v1, Math.min(v1, v2));
    }

    @Override
    public double eqv(double v1, double v2) {
        return Math.min(imp(v1, v2), imp(v2, v1));
    }

    @Override
    public double and(double v1, double v2) {
        return Math.min(v1, v2);
    }

    @Override
    public double and(ArrayList<Double> values) {
        return values.stream().min(Double::compare).get();
    }

    @Override
    public double or(double v1, double v2) {
        return Math.max(v1, v2);
    }

    @Override
    public double or(ArrayList<Double> values) {
        return values.stream().max(Double::compare).get();
    }

    @Override
    public double forAll(List<Double> values) {
        return values.stream().min(Double::compare).get();
    }

    @Override
    public double exist(List<Double> values) {
        return values.stream().max(Double::compare).get();
    }

}