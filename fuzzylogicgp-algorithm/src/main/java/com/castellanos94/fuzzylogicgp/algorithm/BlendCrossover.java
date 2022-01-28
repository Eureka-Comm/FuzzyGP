package com.castellanos94.fuzzylogicgp.algorithm;

import java.util.Random;

import com.castellanos94.fuzzylogicgp.core.ICrossover;

import org.checkerframework.checker.units.qual.A;

/**
 * Blend crossover in real-coded from
 * https://cse.iitkgp.ac.in/~dsamanta/courses/sca/resources/slides/GA-04%20Crossover%20Techniques.pdf
 */
public class BlendCrossover implements ICrossover {
    protected Random random;
    protected double probability;

    public BlendCrossover(double probability, Random random) {
        this.probability = probability;
        this.random = random;
    }

    @Override
    public double[][] execute(double[] a, double[] b, double[][] boundaries) {
        if (a.length != b.length) {
            return null;
        }
        double[][] offspring = new double[2][a.length];
        System.arraycopy(a, 0, offspring[0], 0, a.length);
        System.arraycopy(b, 0, offspring[1], 0, b.length);        

        double gamma;
        for (int i = 0; i < offspring.length; i++) {
            if (probability <= random.nextDouble()) {
                gamma = random.nextDouble();
                offspring[0][i] = (1 - gamma) * a[i] + gamma * b[i];
                offspring[1][i] = (1 - gamma) * b[i] + gamma * a[i];
            }
        }
        // Repair
        for (int i = 0; i < offspring.length; i++) {
            for (int j = 0; j < offspring[0].length; j++) {
                if (Double.isNaN(offspring[i][j])) { // NaN
                    offspring[i][j] = random.doubles(boundaries[j][0], boundaries[j][1]).findFirst()
                            .getAsDouble();
                } else if (Double.compare(offspring[i][j], boundaries[j][0]) < 0) { // Lower
                    offspring[i][j] = random.doubles(boundaries[j][0], boundaries[j][1]).findFirst()
                            .getAsDouble();
                } else if (Double.compare(offspring[i][j], boundaries[j][1]) > 0) { // Upper
                    offspring[i][j] = random.doubles(boundaries[j][0], boundaries[j][1]).findFirst()
                            .getAsDouble();
                }
            }

        }
        return offspring;
    }

}
