package com.castellanos94.fuzzylogicgp.algorithm;

import java.util.Random;

/**
 * This class allows to apply a SBX crossover operator using two parent
 * solutions (Double encoding).
 */
public class SBXCrossover {
    public static final double EPS = 1.0e-6;
    private Random randomGenerator;
    private double distributionIndex;
    private double crossoverProbability;

    /**
     * distributionIndex : 30 & crossoverProbability : 1.0
     */
    public SBXCrossover() {
        this(30, 1.0, new Random());
    }

    /**
     * 
     * @param distributionIndex
     * @param crossoverProbability
     */
    public SBXCrossover(double distributionIndex, double crossoverProbability, Random random) {
        this.distributionIndex = distributionIndex;
        this.crossoverProbability = crossoverProbability;
        this.randomGenerator = random;
    }

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
    public double[][] execute(double[] a, double[] b, double[][] boundaries) {
        if (a.length != b.length) {
            return null;
        }
        double[][] offspring = new double[2][a.length];
        offspring[0] = a.clone();
        offspring[1] = b.clone();

        double rand;
        double y1, y2, lowerBound, upperBound;
        double c1, c2;
        double alpha, beta, betaq;
        double valueX1, valueX2;
        for (int i = 0; i < a.length; i++) {
            valueX1 = a[i];
            valueX2 = b[i];
            if (randomGenerator.nextDouble() <= crossoverProbability) {
                if (Math.abs(valueX1 - valueX2) > EPS) {
                    if (valueX1 < valueX2) {
                        y1 = valueX1;
                        y2 = valueX2;
                    } else {
                        y1 = valueX2;
                        y2 = valueX1;
                    }

                    lowerBound = boundaries[i][0];
                    upperBound = boundaries[i][1];

                    rand = randomGenerator.nextDouble();
                    beta = 1.0 + (2.0 * (y1 - lowerBound) / (y2 - y1));
                    alpha = 2.0 - Math.pow(beta, -(distributionIndex + 1.0));

                    if (rand <= (1.0 / alpha)) {
                        betaq = Math.pow(rand * alpha, (1.0 / (distributionIndex + 1.0)));
                    } else {
                        betaq = Math.pow(1.0 / (2.0 - rand * alpha), 1.0 / (distributionIndex + 1.0));
                    }
                    c1 = 0.5 * (y1 + y2 - betaq * (y2 - y1));

                    beta = 1.0 + (2.0 * (upperBound - y2) / (y2 - y1));
                    alpha = 2.0 - Math.pow(beta, -(distributionIndex + 1.0));

                    if (rand <= (1.0 / alpha)) {
                        betaq = Math.pow((rand * alpha), (1.0 / (distributionIndex + 1.0)));
                    } else {
                        betaq = Math.pow(1.0 / (2.0 - rand * alpha), 1.0 / (distributionIndex + 1.0));
                    }
                    c2 = 0.5 * (y1 + y2 + betaq * (y2 - y1));

                    if (randomGenerator.nextDouble() <= 0.5) {
                        offspring[0][i] = c2;
                        offspring[1][i] = c1;
                    } else {
                        offspring[0][i] = c1;
                        offspring[1][i] = c2;

                    }
                }
            }
        }
        // Repair
        for (int i = 0; i < offspring.length; i++) {
            for (int j = 0; j < offspring[0].length; j++) {
                if (Double.isNaN(offspring[i][j])) { // NaN
                    offspring[i][j] = randomGenerator.doubles(boundaries[i][0], boundaries[i][1]).findFirst()
                            .getAsDouble();
                } else if (Double.compare(offspring[i][j], boundaries[i][0]) < 0) { // Lower
                    offspring[i][j] = randomGenerator.doubles(boundaries[i][0], boundaries[i][1]).findFirst()
                            .getAsDouble();
                } else if (Double.compare(offspring[i][j], boundaries[i][1]) > 0) { // Upper
                    offspring[i][j] = randomGenerator.doubles(boundaries[i][0], boundaries[i][1]).findFirst()
                            .getAsDouble();
                }
            }

        }
        return offspring;
    }
}
