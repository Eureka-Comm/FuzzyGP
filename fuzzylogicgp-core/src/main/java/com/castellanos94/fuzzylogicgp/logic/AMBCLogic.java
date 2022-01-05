/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos94.fuzzylogicgp.logic;

import java.util.ArrayList;
import java.util.List;

/**
 * AMBC logic: https://doi.org/10.1142/S1469026811003070 -based implementation.
 * 
 * @author Castellanos-Alvarez, Alejandro.
 */
public class AMBCLogic extends Logic {

    public AMBCLogic(ImplicationType implicationType) {
        super(implicationType);
    }

    public AMBCLogic() {
        super();
    }

    @Override
    public double not(double v1) {
        return 1 - v1;
    }

    @Override
    public double and(double v1, double v2) {
        return Math.sqrt(Math.min(v1, v2) * (0.5) * (v1 + v2));
    }

    @Override
    public double or(double v1, double v2) {
        return (1.0 - Math.sqrt(Math.min((1.0 - v1), (1.0 - v2)) * (0.5) * ((1.0 - v1) + (1.0 - v2))));
    }

    @Override
    public double forAll(List<Double> values) {
        return Math.sqrt(values.stream().min(Double::compare).get() * (1.0 / values.size())
                * (values.stream().mapToDouble(Double::doubleValue).sum()));
    }

    @Override
    public double exist(List<Double> values) {
        double min = 1.0;
        double sum = 0.0;
        for (Double valDouble : values) {
            double tmp = 1.0 - valDouble;
            sum += tmp;
            if (min > tmp)
                min = tmp;
        }
        return 1.0 - Math.sqrt((min * (1.0 / values.size()) * sum));
    }

    @Override
    public double or(ArrayList<Double> values) {
        double min = 1.0;
        double sum = 0.0;
        for (Double valDouble : values) {
            double tmp = 1.0 - valDouble;
            sum += tmp;
            if (min > tmp) {
                min = tmp;
            }
        }
        return (1.0 - Math.sqrt(min * (1.0 / values.size()) * sum));
    }

    @Override
    public double and(ArrayList<Double> values) {
        return Math.sqrt(values.stream().min(Double::compare).get() * (1.0 / values.size())
                * values.stream().mapToDouble(Double::doubleValue).sum());
    }

}
