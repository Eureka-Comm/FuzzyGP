package com.castellanos94.fuzzylogicgp.algorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.castellanos94.fuzzylogicgp.core.AMembershipFunctionOptimizer;
import com.castellanos94.fuzzylogicgp.core.NodeTree;
import com.castellanos94.fuzzylogicgp.core.NodeType;
import com.castellanos94.fuzzylogicgp.core.OperatorException;
import com.castellanos94.fuzzylogicgp.core.StateNode;
import com.castellanos94.fuzzylogicgp.logic.GMBC_Logic;
import com.castellanos94.fuzzylogicgp.logic.Logic;
import com.castellanos94.fuzzylogicgp.membershipfunction.FPG;
import com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunction;

import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.Table;

/**
 * Default FPG Optimizer
 */
public class FPGOptimizer extends AMembershipFunctionOptimizer {
    protected EvaluatePredicate evaluatePredicate;
    protected int maxIterations;
    protected int populationSize;
    protected double minTruthValue;
    protected double crossoverProbability;
    protected Table data;
    protected HashMap<String, Double[]> minMaxDataValue;
    protected Random random;
    protected SBXCrossover crossover;
    protected double[][] boundaries;

    public static void main(String[] args) throws IOException, OperatorException {
        long start = System.currentTimeMillis();
        ArrayList<StateNode> states = new ArrayList<>();
        states.add(new StateNode("citric_acid", "citric_acid"));
        states.add(new StateNode("volatile_acidity", "volatile_acidity"));
        states.add(new StateNode("fixed_acidity", "fixed_acidity"));
        states.add(new StateNode("free_sulfur_dioxide", "free_sulfur_dioxide"));
        states.add(new StateNode("sulphates", "sulphates"));
        states.add(new StateNode("alcohol", "alcohol"));
        states.add(new StateNode("residual_sugar", "residual_sugar"));
        states.add(new StateNode("pH", "pH"));
        states.add(new StateNode("total_sulfur_dioxide", "total_sulfur_dioxide"));
        states.add(new StateNode("quality", "quality"));
        states.add(new StateNode("density", "density"));
        states.add(new StateNode("chlorides", "chlorides"));
        Table table = Table.read().csv("fuzzylogicgp-algorithm/src/main/resources/datasets/tinto.csv");
        NodeTree and = new NodeTree(NodeType.AND);
        states.forEach(s -> {
            try {
                and.addChild(s);
            } catch (OperatorException e) {
                e.printStackTrace();
            }
        });
        FPGOptimizer optimizer = new FPGOptimizer(new GMBC_Logic(), table, 50, 20, 0.9, 0.95, null);
        NodeTree execute = optimizer.execute(and);
        System.out.println(execute + " f " + execute.getFitness());
        long end = System.currentTimeMillis();
        System.out.println("Time ms: " + (end - start));
    }

    /**
     * Default constructor
     * 
     * @param logic                - logic to evalute predicate
     * @param data                 - dataset
     * @param maxIterations        - max iterations
     * @param populationSize       - population size
     * @param minTruthValue        - minimum truth value of fitness
     * @param crossoverProbability - crossover probability
     * @param random               - can be null
     */
    public FPGOptimizer(Logic logic, Table data, int maxIterations, int populationSize, double minTruthValue,
            double crossoverProbability,
            Random random) {
        super(logic);
        this.data = data;
        this.evaluatePredicate = new EvaluatePredicate(logic, data);
        this.maxIterations = maxIterations;
        this.populationSize = populationSize;
        this.minTruthValue = minTruthValue;
        this.crossoverProbability = crossoverProbability;
        this.random = random == null ? new Random() : random;
        this.crossover = new SBXCrossover(20, crossoverProbability, this.random);
    }

    @Override
    public NodeTree execute(NodeTree predicate) {
        // filter states with null MF
        final List<StateNode> statesToWork = NodeTree.getNodesByType(predicate, NodeType.STATE).stream()
                .map(n -> (StateNode) n).filter(s -> s.getMembershipFunction() == null || s.isEditable()).collect(Collectors.toList());
        minMaxDataValue = new HashMap<>();
        if (statesToWork.isEmpty()) {
            this.evaluatePredicate.execute(predicate);
            return predicate;
        }
        statesToWork.forEach(state -> {
            NumericColumn<?> doubleColumn = data.numberColumn(state.getColName());
            Double[] _minMax = new Double[2];
            _minMax[0] = doubleColumn.min();
            _minMax[1] = doubleColumn.max();
            minMaxDataValue.put(state.getId(), _minMax);
        });

        boundaries = makeBoundaries(statesToWork);
        // Random population
        Chromosome[] population = new Chromosome[populationSize];
        for (int i = 0; i < populationSize; i++) {
            population[i] = generate(statesToWork);
        }
        // control
        // Evaluate first iteration
        for (int i = 0; i < populationSize; i++) {
            _evaluate(predicate, statesToWork, population[i]);
        }
        Arrays.sort(population, comparator);
        // Main for
        ArrayList<Chromosome> offspring = new ArrayList<>();
        for (int i = 1; i < maxIterations && population[0].getFitness() < minTruthValue; i++) {
            // Crossover
            offspring.clear();
            Chromosome[] parents = selection(population);

            for (int k = 0; k < parents.length; k++) {
                offspring.addAll(crossover(parents[k], parents[k + 1 < parents.length ? k + 1 : 0]));
            }
            for (int k = 0; k < offspring.size(); k++) {
                // mutation(offspring.get(k), statesToWork);
                repair(offspring.get(k), statesToWork);
                _evaluate(predicate, statesToWork, offspring.get(k));
                // Repair if fitness -> 0
                if (Double.compare(offspring.get(k).getFitness(), 1.0e-5) <= 0) {
                    Chromosome tmp = generate(statesToWork);
                    _evaluate(predicate, statesToWork, tmp);
                    offspring.set(k, tmp);
                }
            }
            offspring.sort(this.comparator);
            for (int k = 0; k < offspring.size(); k++) {
                Chromosome child = offspring.get(k);
                for (int j = 0; j < populationSize; j++) {
                    Chromosome p = population[j];
                    if (child.compareTo(p) > 0) {
                        population[j] = child.copy();
                        break;
                    }
                }
            }
        }
        Arrays.sort(population, this.comparator);
        /*for (int i = 0; i < populationSize; i++) {
            double tmp = population[i].getFitness();
            _evaluate(predicate, statesToWork, population[i]);
            boolean valid = true;
            for (int j = 0; j < population[i].getFunctions().length; j++) {
                if (!population[i].getFunctions()[j].isValid()) {
                    valid = false;
                    break;
                }
            }
            System.out.println(String.format("%.05f (prev %.05f), valid ? %s", population[i].getFitness(), tmp, valid));
        }*/
        _evaluate(predicate, statesToWork, population[0]);
        return predicate;
    }

    /**
     * Creates a vector of bound variables for each state such that an fpg has 3
     * parameters beta, gamma and m. In the vector of bound variables one has [0] -
     * beta, [1] gamma and [2] m. Then there would be 3 spaces for each fpg in the
     * vector of variables -> variables * 3;
     * 
     * @param statesToWork
     * @return boundaries
     */
    private double[][] makeBoundaries(final List<StateNode> statesToWork) {
        double[][] boundaries = new double[statesToWork.size() * 3][2];

        Double[] doubles;
        int index = 0;
        for (int i = 0; i < statesToWork.size(); i++) {
            doubles = minMaxDataValue.get(statesToWork.get(i).getId());
            boundaries[index][0] = doubles[0];
            boundaries[index++][1] = doubles[1];
            boundaries[index][0] = doubles[0];
            boundaries[index++][1] = doubles[1];
            boundaries[index][0] = 0;
            boundaries[index++][1] = 1;
        }
        return boundaries;
    }

    /**
     * Evaluate Predicate with the chromosome
     * 
     * @param predicate
     * @param statesToWork
     * @param chromosome
     */
    protected void _evaluate(NodeTree predicate, List<StateNode> statesToWork, Chromosome chromosome) {
        for (int i = 0; i < statesToWork.size(); i++) {
            statesToWork.get(i).setMembershipFunction(chromosome.getFunctions()[i]);
        }
        try {
            chromosome.setFitness(evaluatePredicate.evaluate(predicate));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Chromosome generate(final List<StateNode> states) {
        MembershipFunction[] functions = new MembershipFunction[states.size()];
        for (int i = 0; i < states.size(); i++) {
            String idCname = states.get(i).getId();
            Double[] ref = minMaxDataValue.get(idCname);
            double beta = random.doubles(ref[0], ref[1]).findAny().getAsDouble();
            double gamma = random.doubles(beta, ref[1] + 1).findAny().getAsDouble();
            while (Double.compare(gamma, beta) <= 0) {
                gamma = random.doubles(beta, ref[1] + 1).findAny().getAsDouble();
            }
            functions[i] = new FPG(beta, gamma, random.nextInt(100001) / 100001.0);
        }
        return new Chromosome(functions);
    }

    /**
     * Tournament selection default
     * 
     * @param population - the fitness
     * @return selected chromosomes
     */
    protected Chromosome[] selection(Chromosome[] chromosomes) {
        Arrays.sort(chromosomes, this.comparator);
        int size = chromosomes.length / 2;
        while (size % 2 != 0) {
            size++;
        }
        Chromosome[] parents = new Chromosome[size];
        for (int i = 0; i < parents.length; i++) {
            parents[i] = chromosomes[i];
        }
        return parents;
    }

    /**
     * SBX crossover for FPG
     */
    @Override
    protected List<Chromosome> crossover(Chromosome a, Chromosome b) {
        List<Chromosome> offspring = new ArrayList<>();
        int size = a.getFunctions().length;

        double[] aVars = new double[size * 3];
        double[] bVars = new double[size * 3];
        int aIndex = 0;
        int bIndex = 0;
        for (int i = 0; i < size; i++) {
            FPG af = (FPG) a.getFunctions()[i];
            FPG bf = (FPG) b.getFunctions()[i];
            aVars[aIndex] = af.getBeta();
            aVars[aIndex++] = af.getGamma();
            aVars[aIndex++] = af.getM();

            bVars[bIndex] = bf.getBeta();
            bVars[bIndex++] = bf.getGamma();
            bVars[bIndex++] = bf.getM();
        }
        double[][] offspringVars = crossover.execute(aVars, bVars, boundaries);
        aIndex = 0;
        bIndex = 0;
        MembershipFunction[] aFPG = new FPG[size];
        MembershipFunction[] bFPG = new FPG[size];
        for (int i = 0; i < size; i++) {
            aFPG[i] = new FPG(offspringVars[0][aIndex], offspringVars[0][aIndex++], offspringVars[0][aIndex++]);
            bFPG[i] = new FPG(offspringVars[0][bIndex], offspringVars[0][bIndex++], offspringVars[0][bIndex++]);
        }
        offspring.add(new Chromosome(aFPG));
        offspring.add(new Chromosome(bFPG));
        return offspring;
    }

    /**
     * Dont used
     */

    @Override
    protected void mutation(Chromosome chromosome, List<StateNode> states) {
        double mutationProbability = 0.1;
        for (int i = 0; i < chromosome.getFunctions().length; i++) {
            if (random.nextDouble() <= mutationProbability) {
                String idCname = states.get(i).getId();
                Double[] ref = minMaxDataValue.get(idCname);
                double beta = random.doubles(ref[0], ref[1] + 1).findAny().getAsDouble();
                double gamma = random.doubles(beta, ref[1] + 1).findAny().getAsDouble();
                while (Double.compare(gamma, beta) <= 0) {
                    gamma = random.doubles(beta, ref[1] + 1).findAny().getAsDouble();
                }
                chromosome.getFunctions()[i] = new FPG(beta, gamma, random.nextInt(100001) / 100001.0);
            }
        }
    }

    @Override
    protected void repair(Chromosome chromosome, List<StateNode> states) {
        for (int i = 0; i < chromosome.getFunctions().length; i++) {
            FPG fpg = (FPG) chromosome.getFunctions()[i];
            if (Double.compare(fpg.getBeta(), fpg.getGamma()) == 0) {
                String idCname = states.get(i).getId();
                Double[] ref = minMaxDataValue.get(idCname);

                double beta = random.doubles(ref[0], ref[1]).findAny().getAsDouble();
                double gamma = random.doubles(beta, ref[1] + 1).findAny().getAsDouble();
                while (Double.compare(gamma, beta) <= 0) {
                    gamma = random.doubles(beta, ref[1] + 1).findAny().getAsDouble();
                }
                fpg.setBeta(beta);
                fpg.setGamma(gamma);
            }
            if (Double.compare(fpg.getBeta(), fpg.getGamma()) > 0) {
                double tmp = fpg.getGamma();
                fpg.setGamma(fpg.getBeta());
                fpg.setBeta(tmp);
            }
            if (Double.isNaN(fpg.getM()) || Double.compare(fpg.getM(), 0) < 0
                    || Double.compare(fpg.getM(), 1) > 0) {
                fpg.setM(random.nextInt(100001) / 100001.0);
            }
        }
    }

}