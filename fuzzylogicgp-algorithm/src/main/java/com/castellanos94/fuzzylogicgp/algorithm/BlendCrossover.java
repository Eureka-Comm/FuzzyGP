package com.castellanos94.fuzzylogicgp.algorithm;

import java.util.Random;

import com.castellanos94.fuzzylogicgp.core.ICrossover;

/**
 * Blend crossover in real-coded from
 * https://cse.iitkgp.ac.in/~dsamanta/courses/sca/resources/slides/GA-04%20Crossover%20Techniques.pdf
 */
public class BlendCrossover implements ICrossover {
    protected Random random;
    protected double probability;
    protected RepairMembershipFunction repair;

    public BlendCrossover(double probability, Random random) {
        this.probability = probability;
        this.random = random;
        this.repair = new RepairMembershipFunction(random);
    }

    @Override
    public Double[][] execute(Double[][] a, Double[][] b, double[][][] boundaries) {
        if (a.length != b.length) {
            return null;
        }
        Double[][] offspring = new Double[a.length][];
        double gamma;

        for (int i = 0; i < offspring.length; i++) {
            offspring[i] = new Double[a[i].length];
            System.arraycopy(a[i], 0, offspring[i], 0, a[i].length);
            if (probability <= random.nextDouble()) {
                gamma = random.nextDouble();
                for (int j = 0; j < a[i].length; j++) {
                    offspring[i][j] = (1 - gamma) * a[i][j] + gamma * b[i][j];
                    offspring[i][j] = (1 - gamma) * b[i][j] + gamma * a[i][j];
                }
            }
            // Repair
            repair.boundary(offspring[i], boundaries[i]);
        }

        return offspring;
    }

}
