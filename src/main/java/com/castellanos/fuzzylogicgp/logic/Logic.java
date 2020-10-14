/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

/**
 *
 * @author hp
 */
public abstract class Logic {

    private static Set<Logic> instances = Collections.newSetFromMap(new WeakHashMap<Logic, Boolean>());

    public Logic() {
        instances.add(this);
    }

    public static Set<Logic> getInstances() {
        return instances;
    }

    public abstract double not(double v1);

    public abstract double imp(double v1, double v2);

    public abstract double eqv(double v1, double v2);

    public abstract double and(double v1, double v2);

    public abstract double and(ArrayList<Double> values);

    public abstract double or(double v1, double v2);

    public abstract double or(ArrayList<Double> values);

    public abstract double forAll(List<Double> values);

    public abstract double exist(List<Double> values);
}
