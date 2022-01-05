/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos94.fuzzylogicgp.logic;

import java.util.ArrayList;
import java.util.List;

/**
 * Logic with implication support
 * 
 * @author Castellanos Alvarez, Alejandro
 * @version 1.0.1
 */
public abstract class Logic { 

    protected ImplicationType implicationType;

    public Logic(ImplicationType type) {
        this.implicationType = type;
    }

    public Logic() {
        this(ImplicationType.Zadeh);
    }

    public ImplicationType getImplicationType() {
        return implicationType;
    }

    public void setImplicationType(ImplicationType implicationType) {
        this.implicationType = implicationType;
    }

    public abstract double not(double v1);

    public double imp(double v1, double v2) {
        switch (implicationType) {
            case Natural:
                return or(not(v1), v2);
            case Zadeh:
                return or(not(v1), and(v1, v2));
            case Reichenbach:
                return 1.0 - v1 + v1 * v2;
            case KlirYuan:
                return 1.0 - v1 + (Math.pow(v1, 2) * v2);
            case Yager:
                return Math.pow(v1, v2);
            default:
                throw new IllegalArgumentException("Invalid implication type: " + implicationType);
        }
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

    @Override
    public String toString() {
        return this.getClass().getName() + " " + implicationType;
    }
}
