/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.logic;

import java.util.ArrayList;
import java.util.List;

/**
 * Logica Compensatoria Basada en la Media Geometrica.
 *
 * @author hp
 */
public class GMBC_Logic extends Logic {
    public GMBC_Logic(boolean natural_implication) {
        super(natural_implication);
    }

    public GMBC_Logic() {
        super(false);
    }

    @Override
    public double not(double v1) {
        return 1.0 - v1;
    }

    @Override
    public double imp(double v1, double v2) {
        double neg = 1 - v1;
        double aux = (1 - neg) * (1 - v2);
        double aux2 = Math.pow(aux, 0.5);
        double resultado = 1.0 - aux2;
        return (resultado);
    }

    @Override
    public double and(double v1, double v2) {
        return (Math.pow(v1 * v2, 0.5));
    }

    @Override
    public double and(ArrayList<Double> values) {
        return Math.pow(values.stream().parallel().reduce(1.0, (a, b) -> a * b), 1.0 / values.size());
    }

    @Override
    public double or(double v1, double v2) {
        return (1.0 - Math.pow((1 - v1) * (1 - v2), 0.5));
    }

    @Override
    public double or(ArrayList<Double> values) {
        double x = 1.0;
        for (Double value : values) {
            x *= (1 - value);
        }
        return (1.0 - Math.pow(x, 1.0 / values.size()));
    }

    @Override
    public double forAll(List<Double> values) {
        double result = 0.0;
        result = values.stream().filter((value) -> (value != 0)).map((value) -> Math.log(value)).reduce(result,
                (accumulator, _item) -> accumulator + _item);
        if (result == 0)
            return 0;
        return Math.pow(Math.E, ((1.0 / values.size()) * result));
    }

    @Override
    public double exist(List<Double> values) {
        double result = 0.0;
        result = values.stream().filter((value) -> (value != 0)).map((value) -> (Math.log(1 - value))).reduce(result,
                (accumulator, _item) -> accumulator + _item);
        return (1 - Math.pow(Math.E, ((1.0 / values.size()) * result)));
    }

}
