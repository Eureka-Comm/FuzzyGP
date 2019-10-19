/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.logic;

import java.util.List;

/**
 * Logica Compensatoria Basada en la Media Geometrica.
 *
 * @author hp
 */
public class GMBC implements ALogic {

    @Override
    public Double not(Double v1) {
        return (1 - v1);
    }

    @Override
    public Double imp(Double v1, Double v2) {
        Double resultado, neg, aux, aux2;
        neg = 1 - v1;
        aux = (1 - neg) * (1 - v2);
        aux2 = Math.pow(aux, 0.5);
        resultado = 1.0 - aux2;
        //=1-POWER((1-O2)*(1-L2),1/2)
        return (resultado);
    }

    @Override
    public Double eqv(Double v1, Double v2) {
        Double resultado;
        resultado = Math.pow(imp(v1, v2) * imp(v1, v2), (Double) (1.0 / 2.0));
        return (resultado);
    }

    @Override
    public Double and(Double v1, Double v2) {
        //System.out.println(v1+ "  "+v2);
        return (Math.pow(v1 * v2, (double) (1.0 / 2.0)));
    }

    @Override
    public Double or(Double v1, Double v2) {
        return (1.0 - Math.pow(v1, (Double) (1.0 / v2)));
    }

    @Override
    public Double forAll(List<Double> values) {
        Double result = 0.0;
        result = values.stream().filter((value) -> (value != 0)).map((value) -> Math.log(value)).reduce(result, (accumulator, _item) -> accumulator + _item);
        return Math.pow(Math.E, ((1.0 / values.size()) * result));
    }

    @Override
    public Double exist(List<Double> values) {
        double result = 0.0;
        result = values.stream().filter((value) -> (value != 0)).map((value) -> (Math.log(1 - value))).reduce(result, (accumulator, _item) -> accumulator + _item);
        return (1 - Math.pow(Math.E, ((1.0 / values.size()) * result)));
    }

}
