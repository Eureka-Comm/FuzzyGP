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
    public double not(double v1);
    public double imp(double v1, double v2);
    public double eqv(double v1, double v2);
    public double and(double v1, double v2);
    public double or(double v1, double v2);
}
