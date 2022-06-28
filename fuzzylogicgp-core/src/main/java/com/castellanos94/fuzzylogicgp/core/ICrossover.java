package com.castellanos94.fuzzylogicgp.core;

/**
 * Interface for membership fuction crossover
 */
public interface ICrossover {

    /**
     * Generates two children given two parents
     * 
     * @param a          - parent
     * @param b          - parent
     * @param boundaries - for each variable "i" there is a boundary value [lower,
     *                   upper] -> boundaries[i][0] (lower), boundaries[i][1]
     *                   (upper)
     * @return
     */
    public Double[][] execute(Double[][] a, Double[][] b, double[][][] boundaries);
}
