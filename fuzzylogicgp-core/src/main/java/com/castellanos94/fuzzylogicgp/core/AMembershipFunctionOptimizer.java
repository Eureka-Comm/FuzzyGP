package com.castellanos94.fuzzylogicgp.core;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import com.castellanos94.fuzzylogicgp.logic.Logic;
import com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunction;

/**
 * Abstract class for membership function optimizer
 */
public abstract class AMembershipFunctionOptimizer {
    protected Logic logic;
    /**
     * Comparator for maximization
     */
    protected Comparator<Chromosome> comparator = Comparator.comparing(Chromosome::getFitness).reversed();

    public AMembershipFunctionOptimizer(Logic logic) {
        this.logic = logic;
    }

    public abstract NodeTree execute(NodeTree predicate);

    protected abstract Chromosome generate(final List<StateNode> states);

    protected abstract List<Chromosome> crossover(Chromosome a, Chromosome b);

    protected abstract void mutation(Chromosome chromosome, List<StateNode> states);

    protected abstract void repair(Chromosome chromosome, List<StateNode> states);

    public Logic getLogic() {
        return logic;
    }

    public void setLogic(Logic logic) {
        this.logic = logic;
    }

    public static class Chromosome implements Comparable<Chromosome> {
        private double fitness;
        private MembershipFunction[] functions;

        public Chromosome(MembershipFunction[] functions) {
            this.functions = functions;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + Arrays.hashCode(functions);
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Chromosome other = (Chromosome) obj;
            if (!Arrays.equals(functions, other.functions))
                return false;
            return true;
        }

        @Override
        public int compareTo(Chromosome o) {
            return Double.compare(this.fitness, o.fitness);
        }

        public double getFitness() {
            return fitness;
        }

        public void setFitness(double fitness) {
            this.fitness = fitness;
        }

        public MembershipFunction[] getFunctions() {
            return functions;
        }

        public void setFunctions(MembershipFunction[] functions) {
            this.functions = functions;
        }

        public Chromosome copy() {
            MembershipFunction[] f = new MembershipFunction[this.functions.length];
            for (int i = 0; i < f.length; i++) {
                f[i] = functions[i].copy();
            }
            Chromosome c = new Chromosome(f);
            c.setFitness(fitness);
            return c;
        }

        @Override
        public String toString() {
            return "Chromosome [fitness=" + fitness + ", functions=" + Arrays.toString(functions) + "]";
        }

    }

}
