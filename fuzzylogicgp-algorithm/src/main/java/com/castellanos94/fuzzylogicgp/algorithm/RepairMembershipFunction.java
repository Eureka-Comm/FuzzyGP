package com.castellanos94.fuzzylogicgp.algorithm;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.castellanos94.fuzzylogicgp.core.AMembershipFunctionOptimizer;
import com.castellanos94.fuzzylogicgp.core.StateNode;
import com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunctionType;

public class RepairMembershipFunction {
    protected Random random;
    protected double probability;

    public RepairMembershipFunction(Random random) {
        this.random = random;
    }

    /**
     * Repairs membership functions by validating that they are within their limits
     * and comply with their restrictions
     * 
     * @param states          - sorted
     * @param boundaries      - lower/upper limits
     * @param minMaxDataValue - data
     * @param chromosome      - individual
     */
    public void repair(final List<StateNode> states, double[][][] boundaries, HashMap<String, Double[]> minMaxDataValue,
            AMembershipFunctionOptimizer.Chromosome chromosome) {
        for (int i = 0; i < chromosome.getFunctions().length; i++) {
            MembershipFunctionType type = states.get(i).getMembershipFunction() == null ? MembershipFunctionType.FPG
                    : states.get(i).getMembershipFunction().getType();
            switch (type) {
                case FPG:
                    Double[] values = chromosome.getFunctions()[i];
                    if (Double.compare(values[0], values[1]) == 0) {
                        String idCname = states.get(i).getId();
                        Double[] ref = minMaxDataValue.get(idCname);

                        double beta = random.doubles(ref[0], ref[1]).findAny().getAsDouble();
                        double gamma = random.doubles(beta, ref[1]).findAny().getAsDouble();
                        while (Double.compare(gamma, beta) <= 0) {
                            if (Double.compare(beta, ref[1]) == 0) {
                                beta = random.doubles(ref[0], ref[1]).findAny().getAsDouble();
                            }
                            gamma = random.doubles(beta, ref[1]).findAny().getAsDouble();
                        }
                        values[0] = beta;
                        values[1] = gamma;
                    }
                    if (Double.compare(values[0], values[1]) > 0) {
                        double tmp = values[1];
                        values[1] = values[0];
                        values[0] = tmp;
                    }
                    if (Double.isNaN(values[2]) || Double.compare(values[2], 0) < 0
                            || Double.compare(values[2], 1) > 0) {
                        values[2] = (random.nextInt(100001) / 100001.0);
                    }
                    break;

                default:
                    throw new UnsupportedOperationException("Not repair method for membership " + type);
            }
            this.boundary(chromosome.getFunctions()[i], boundaries[i]);
        }
    }

    public void boundary(Double[] values, double[][] boundaries) {
        for (int i = 0; i < values.length; i++) {
            boolean noteq = Double.compare(boundaries[i][0], boundaries[i][1]) != 0;
            if (Double.isNaN(values[i])) { // NaN
                values[i] = noteq ? random.doubles(boundaries[i][0], boundaries[i][1]).findFirst()
                        .getAsDouble() : boundaries[i][0];
            } else if (Double.compare(values[i], boundaries[i][0]) < 0) { // Lower
                values[i] = noteq ? random.doubles(boundaries[i][0], boundaries[i][1]).findFirst()
                        .getAsDouble() : boundaries[i][0];
            } else if (Double.compare(values[i], boundaries[i][1]) > 0) { // Upper
                values[i] = noteq ? random.doubles(boundaries[i][0], boundaries[i][1]).findFirst()
                        .getAsDouble() : boundaries[i][0];
            }
        }
    }
}
