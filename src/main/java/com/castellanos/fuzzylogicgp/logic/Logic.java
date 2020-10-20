/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.logic;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hp
 */
public abstract class Logic {
    protected boolean natural_implication;

    public Logic(boolean natural_implication) {
        this.natural_implication = natural_implication;
    }

    public boolean isNatural_implication() {
        return natural_implication;
    }

    public void setNatural_implication(boolean natural_implication) {
        this.natural_implication = natural_implication;
    }

    public abstract double not(double v1);

    public double imp(double v1, double v2) {
        if (natural_implication) {
            return this.or(this.not(v1), v2);
        }
        return this.or(this.not(v1), this.and(v1, v2));
    }

    public double eqv(double v1, double v2) {
        return this.and(this.imp(v1, v2), this.imp(v2, v1));
    }

    public abstract double and(double v1, double v2);

    public abstract double and(ArrayList<Double> values);

    public abstract double or(double v1, double v2);

    public abstract double or(ArrayList<Double> values);

    public abstract double forAll(List<Double> values);

    public abstract double exist(List<Double> values);

}
