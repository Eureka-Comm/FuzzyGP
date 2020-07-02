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
public class GMBC_Logic implements Logic {

    @Override
    public double not(double v1) {
        // return BigDecimal.ONE.subtract(v1);
        return 1.0 - v1;
    }

    @Override
    public double imp(double v1, double v2) {
        /*
         * BigDecimal resultado, neg, aux, aux2; neg = BigDecimal.ONE.subtract(v1); aux
         * = BigDecimal.ONE.subtract(neg).multiply(BigDecimal.ONE.subtract(v2)); aux2 =
         * BigDecimalMath.pow(aux, new BigDecimal("0.5"), MathContext.DECIMAL64);
         * resultado = BigDecimal.ONE.subtract(aux2);
         */
        double neg = 1 - v1;
        double aux = (1 - neg) * (1 - v2);
        double aux2 = Math.pow(aux, 0.5);
        double resultado = 1.0 - aux2;
        // =1-POWER((1-O2)*(1-L2),1/2)
        return (resultado);
    }

    @Override
    public double eqv(double v1, double v2) {
        // BigDecimal resultado;
        double resultado = Math.pow(imp(v1, v2) * imp(v1, v2), (Double) (1.0 / 2.0));
        // resultado = BigDecimalMath.pow(imp(v1, v2).multiply(imp(v2, v1)), new
        // BigDecimal("0.5"), MathContext.DECIMAL64);
        return (resultado);
    }

    @Override
    public double and(double v1, double v2) {
        // System.out.println(v1+ " "+v2);
        return (Math.pow(v1 * v2, 0.5));
        // return BigDecimalMath.pow(v1.multiply(v2), new BigDecimal("0.5"),
        // MathContext.DECIMAL64);
    }
    @Override
    public double and(ArrayList<Double> values) {
        
        return Math.pow(values.stream().parallel().reduce(1.0, (a,b)->  a*b) ,1.0/values.size());
    }
    @Override
    public double or(double v1, double v2) {
        // return (1.0 - Math.pow(v1, 0.5));
        // return BigDecimal.ONE.subtract( BigDecimalMath.pow(v1,s
        // BigDecimal.ONE.divide(v2, MathContext.DECIMAL64), MathContext.DECIMAL64));
        return (1.0 - Math.pow((1 - v1) * (1 - v2), 0.5));
    }

    @Override
    public double or(ArrayList<Double> values) {
       double x = 1.0;
        for (Double value : values) {
            x *= (1-value);
        }
        return (1.0 - Math.pow(x, 1.0/values.size()));
       // return (1.0 - Math.pow(values.stream().parallel().reduce(0.0, (a,b) -> (1.0-a)*(1.0-b)), 1.0/values.size()));

    }

    @Override
    public double forAll(List<Double> values) {
        double result = 0.0;
        result = values.stream().filter((value) -> (value != 0)).map((value) -> Math.log(value)).reduce(result,
                (accumulator, _item) -> accumulator + _item);
        return Math.pow(Math.E, ((1.0 / values.size()) * result));
        /*
         * BigDecimal rs = BigDecimal.ZERO; for (BigDecimal x : values) { if
         * (!x.equals(BigDecimal.ZERO) && x.compareTo(BigDecimal.ZERO) != 0) rs =
         * rs.add(BigDecimalMath.log(x, MathContext.DECIMAL64), MathContext.DECIMAL64);
         * }
         * 
         * 
         * /* return BigDecimalMath.pow(BigDecimalMath.e(MathContext.DECIMAL64),
         * BigDecimal.ONE .divide(rs.multiply(BigDecimal.ONE.divide(new
         * BigDecimal(values.size()), MathContext.DECIMAL64), MathContext.DECIMAL64),
         * MathContext.DECIMAL64), MathContext.DECIMAL64);
         */
        /*
         * return BigDecimalMath.exp(rs.divide(new BigDecimal(values.size()),
         * MathContext.DECIMAL64), MathContext.DECIMAL64);
         */
    }

    @Override
    public double exist(List<Double> values) {
        // BigDecimal result = BigDecimal.ZERO;
        double result = 0.0;
        result = values.stream().filter((value) -> (value != 0)).map((value) -> (Math.log(1 - value))).reduce(result,
                (accumulator, _item) -> accumulator + _item);
        return (1 - Math.pow(Math.E, ((1.0 / values.size()) * result)));
        /*
         * for (BigDecimal x : values) { if (!x.equals(BigDecimal.ZERO)) { BigDecimal
         * tmp = BigDecimal.ONE.subtract(x,MathContext.DECIMAL64); if
         * (!tmp.equals(BigDecimal.ZERO) && tmp.compareTo(BigDecimal.ZERO) != 0) result
         * = result.add(BigDecimalMath.log(tmp, MathContext.DECIMAL64),
         * MathContext.DECIMAL64); } } /* return BigDecimal.ONE.subtract(
         * BigDecimalMath.pow(BigDecimalMath.e(MathContext.DECIMAL64),
         * BigDecimal.ONE.divide(result.multiply( BigDecimal.ONE.divide(new
         * BigDecimal(values.size()), MathContext.DECIMAL64), MathContext.DECIMAL64),
         * MathContext.DECIMAL64), MathContext.DECIMAL64), MathContext.DECIMAL64);
         */
        /*
         * return BigDecimal.ONE.subtract(BigDecimalMath .exp(result.divide(new
         * BigDecimal(values.size()), MathContext.DECIMAL64), MathContext.DECIMAL64));
         */
    }


}
