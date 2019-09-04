/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.logic;

/**
 *
 * @author hp
 */
public interface ALogic {
    public double not(Double v1);
    public double imp(Double v1, Double v2);
    public double eqv(Double v1, Double v2);
    public double and(Double v1, Double v2);
    public double or(Double v1, Double v2);
}
