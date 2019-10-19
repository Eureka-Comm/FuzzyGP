/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.logic;

import java.util.List;

/**
 *
 * @author hp
 */
public interface ALogic {
    public Double not(Double v1);
    public Double imp(Double v1, Double v2);
    public Double eqv(Double v1, Double v2);
    public Double and(Double v1, Double v2);
    public Double or(Double v1, Double v2);
    public Double forAll(List<Double> values);
    public Double exist(List<Double> values);
}
