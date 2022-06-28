package com.castellanos94.fuzzylogicgp.algorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.castellanos94.fuzzylogicgp.core.AMembershipFunctionOptimizer;
import com.castellanos94.fuzzylogicgp.core.ICrossover;
import com.castellanos94.fuzzylogicgp.core.Node;
import com.castellanos94.fuzzylogicgp.core.NodeTree;
import com.castellanos94.fuzzylogicgp.core.NodeType;
import com.castellanos94.fuzzylogicgp.core.OperatorException;
import com.castellanos94.fuzzylogicgp.core.StateNode;
import com.castellanos94.fuzzylogicgp.logic.GMBCLogic;
import com.castellanos94.fuzzylogicgp.logic.Logic;
import com.castellanos94.fuzzylogicgp.membershipfunction.FPG;
import com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunction;
import com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunctionFactory;
import com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunctionType;

import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.Table;

/**
 * Default FPG Optimizer
 * Se cambio la funcion objetivo de forAll -> and(forAll, exits)
 */
public class FPGOptimizer extends AMembershipFunctionOptimizer {
    private static final Logger logger = LogManager.getLogger(FPGOptimizer.class);

    // protected EvaluatePredicate evaluatePredicate;
    protected int maxIterations;
    protected int populationSize;
    protected double minTruthValue;
    protected double crossoverProbability;
    protected Table data;
    protected HashMap<String, Double[]> minMaxDataValue;
    protected Random random;
    protected ICrossover sbxCrossover;
    protected ICrossover blenCrossover;
    protected RepairMembershipFunction repairMembershipFunction;
    protected double[][][] boundaries;

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
        states.add(new StateNode("quality", "quality", new FPG(5., 8., null)));
        states.add(new StateNode("density", "density"));
        states.add(new StateNode("chlorides", "chlorides", new FPG(null, null, 1.0)));
        Table table = Table.read().csv("fuzzylogicgp-algorithm/src/main/resources/datasets/tinto.csv");
        NodeTree and = new NodeTree(NodeType.AND);
        states.forEach(s -> {
            try {
                and.addChild(s);
            } catch (OperatorException e) {
                e.printStackTrace();
            }
        });
        Random rand = new Random(1);
        FPGOptimizer optimizer = new FPGOptimizer(new GMBCLogic(), table, 20, 20, 0.9, 0.95, rand);
        NodeTree execute = optimizer.execute(and);
        System.out.println(execute + " f " + execute.getFitness());
        long end = System.currentTimeMillis();
        logger.info("Time ms: " + (end - start));
        and.forEach(s -> System.out.println(s));
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
        // this.evaluatePredicate = new EvaluatePredicate(logic, data);
        this.maxIterations = maxIterations;
        this.populationSize = populationSize;
        this.minTruthValue = minTruthValue;
        this.crossoverProbability = crossoverProbability;
        this.random = random == null ? new Random() : random;
        // this.sbxCrossover = new SBXCrossover(20, crossoverProbability, this.random);
        this.blenCrossover = new BlendCrossover(crossoverProbability, this.random);
        this.repairMembershipFunction = new RepairMembershipFunction(random);
    }

    @Override
    public NodeTree execute(NodeTree predicate) {
        // filter states with null MF
        ArrayList<StateNode> states = NodeTree.getNodesByType(predicate, StateNode.class);
        final List<StateNode> statesToWork = states.stream()
                .map(n -> (StateNode) n)
                .filter(s -> s.getMembershipFunction() == null
                        || (s.getMembershipFunction() != null && s.getMembershipFunction().isEditable()
                                || !s.getMembershipFunction().isValid()))
                .collect(Collectors.toList());
        for (Node node : states) {
            if (node instanceof StateNode) {
                StateNode sn = (StateNode) node;

                if (sn.getMembershipFunction() != null && !sn.isEditable() &&
                        (!sn.getMembershipFunction().isValid()
                                && sn.getMembershipFunction().getType() != MembershipFunctionType.FPG)) {
                    throw new IllegalArgumentException("Invalid membership function " +
                            sn.toString());
                }

            }
        }
        minMaxDataValue = new HashMap<>();
        if (statesToWork.isEmpty()) {
            EvaluatePredicate evaluatePredicate = new EvaluatePredicate(logic, data);
            evaluatePredicate.execute(predicate);
            return predicate;
        }
        statesToWork.forEach(state -> {
            NumericColumn<?> doubleColumn = data.numberColumn(state.getColName());
            Double[] _minMax = new Double[2];
            _minMax[0] = doubleColumn.min();
            _minMax[1] = doubleColumn.max();
            if (Double.compare(_minMax[0], _minMax[1]) == 0) {
                _minMax[1] = _minMax[0] + 1;
            }
            minMaxDataValue.put(state.getId(), _minMax);
        });

        boundaries = makeBoundaries(statesToWork);
        // Random population
        Chromosome[] population = new Chromosome[populationSize];
        for (int i = 0; i < populationSize; i++) {
            population[i] = generate(statesToWork, i < populationSize / 2 ? 0 : 1);
            _evaluate(predicate, statesToWork, population[i]);
        }
        Arrays.sort(population, comparator);
        // Main for
        ArrayList<Chromosome> offspring = new ArrayList<>();
        int bestIndex = -1;
        for (int i = 1; i < maxIterations; i++) {
            // Crossover
            offspring.clear();
            Chromosome[] parents = selection(population);

            for (int k = 0; k < parents.length; k++) {
                offspring.addAll(crossover(parents[k], parents[k + 1 < parents.length ? k + 1 : 0]));
            }
            int replace = 0;
            for (int k = 0; k < offspring.size(); k++) {
                Chromosome _child = offspring.get(k);
                mutation(_child, statesToWork);
                repair(_child, statesToWork);
                _evaluate(predicate, statesToWork, _child);
                // Repair if fitness -> 0
                if (Double.isNaN(_child.getFitness())
                        || Double.compare(_child.getFitness(), 1.0e-5) <= 0) {
                    Chromosome tmp = generate(statesToWork, 0);
                    _evaluate(predicate, statesToWork, tmp);
                    offspring.set(k, tmp);
                    replace++;
                }
            }
            // logger.info("Replace {} of {}", replace, offspring.size());
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
            boolean found = false;
            for (int j = 0; j < populationSize; j++) {
                if (Double.isNaN(population[j].getFitness())) {
                    population[j] = generate(statesToWork, j < populationSize / 2 ? 0 : 1);
                    _evaluate(predicate, statesToWork, population[j]);
                } else if (population[j].getFitness() >= minTruthValue) {
                    found = true;
                    bestIndex = j;
                    minTruthValue = population[j].getFitness();
                }
            }
            if (found) {
                break;
            }
        }
        if (bestIndex != -1) {
            _evaluate(predicate, statesToWork, population[bestIndex]);
            return predicate;
        } else {
            double max = population[0].getFitness();
            bestIndex = 0;
            for (int i = 1; i < populationSize; i++) {
                if (!Double.isNaN(population[i].getFitness()) && population[i].getFitness() > max) {
                    bestIndex = i;
                    max = population[i].getFitness();
                }
            }
            _evaluate(predicate, statesToWork, population[bestIndex]);
            predicate.setFitness(population[bestIndex].getFitness());
            return predicate;
        }
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
    private double[][][] makeBoundaries(final List<StateNode> statesToWork) {
        double[][][] boundaries = new double[statesToWork.size()][][];
        Double[] doubles;

        for (int i = 0; i < statesToWork.size(); i++) {
            int index = 0;
            doubles = minMaxDataValue.get(statesToWork.get(i).getId());
            if (statesToWork.get(i).getMembershipFunction() != null) {
                Double[] array = statesToWork.get(i).getMembershipFunction().toArray();
                boundaries[i] = new double[array.length][2];
                for (int j = 0; j < array.length; j++) {
                    if (array[j] != null) {
                        boundaries[i][index][0] = array[j];
                        boundaries[i][index++][1] = array[j];
                    } else {
                        if (j == 2 && statesToWork.get(i).getMembershipFunction()
                                .getType() == MembershipFunctionType.FPG) {
                            boundaries[i][index][0] = 0;
                            boundaries[i][index++][1] = 1;
                        } else {
                            boundaries[i][index][0] = doubles[0];
                            boundaries[i][index++][1] = doubles[1];
                        }
                    }

                }
            } else {
                boundaries[i] = new double[3][2];

                boundaries[i][index][0] = doubles[0];
                boundaries[i][index++][1] = doubles[1];
                boundaries[i][index][0] = doubles[0];
                boundaries[i][index++][1] = doubles[1];
                boundaries[i][index][0] = 0;
                boundaries[i][index++][1] = 1;
            }
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
            MembershipFunctionType _type = statesToWork.get(i).getMembershipFunction() != null
                    ? statesToWork.get(i).getMembershipFunction().getType()
                    : MembershipFunctionType.FPG;
            MembershipFunction f = MembershipFunctionFactory.fromArray(_type, chromosome.getFunctions()[i]);
            f.setEditable(true);
            statesToWork.get(i).setMembershipFunction(f);
        }

        EvaluatePredicate evaluatePredicate = new EvaluatePredicate(logic, data);
        double forAll;
        if (predicate.getType() == NodeType.IMP) {
            forAll = evaluatePredicate.evaluateIMP(predicate);
        } else {
            forAll = evaluatePredicate.evaluate(predicate);
        }
        chromosome.setFitness(forAll);
    }

    @Override
    protected Chromosome generate(final List<StateNode> states, int generationType) {
        Double[][] functions = new Double[states.size()][];
        for (int i = 0; i < states.size(); i++) {
            StateNode stateNode = states.get(i);
            // if (generationType == 0) {
            functions[i] = stateNode.getMembershipFunction() == null ? new Double[3]
                    : stateNode.getMembershipFunction().toArray();
            String idCname = stateNode.getId();
            Double[] ref = minMaxDataValue.get(idCname);

            double beta = random.doubles(ref[0], ref[1]).findAny().getAsDouble();
            double gamma = random.doubles(beta, ref[1]).findAny().getAsDouble();
            while (Double.compare(gamma, beta) <= 0) {
                if (Double.compare(beta, ref[1]) == 0) {
                    beta = random.doubles(ref[0], ref[1]).findAny().getAsDouble();
                }
                gamma = random.doubles(beta, ref[1]).findAny().getAsDouble();
            }
            functions[i] = new Double[] { functions[i][0] == null ? beta : functions[i][0],
                    functions[i][1] == null ? gamma : functions[i][1],
                    functions[i][2] == null ? random.nextInt(100001) / 100001.0 : functions[i][2] };
            // logger.info("GENERATE {}:{}", stateNode, Arrays.toString(functions[i]));
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
            // parents[i] = chromosomes[i % 2 == 0 ? i :
            // random.nextInt(chromosomes.length)];
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
        offspring.add(new Chromosome(blenCrossover.execute(a.getFunctions(), b.getFunctions(), boundaries)));
        offspring.add(new Chromosome(blenCrossover.execute(b.getFunctions(), a.getFunctions(), boundaries)));
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
                double beta = random.doubles(ref[0], ref[1]).findAny().getAsDouble();
                double gamma = random.doubles(beta, ref[1]).findAny().getAsDouble();
                while (Double.compare(gamma, beta) <= 0) {
                    if (Double.compare(beta, ref[1]) == 0) {
                        beta = random.doubles(ref[0], ref[1]).findAny().getAsDouble();
                    }
                    gamma = random.doubles(beta, ref[1]).findAny().getAsDouble();
                }
                chromosome.getFunctions()[i] = new Double[] { beta, gamma, random.nextInt(100001) / 100001.0 };
            }
            this.repairMembershipFunction.repair(states, boundaries, minMaxDataValue, chromosome);
        }
    }

    @Override
    protected void repair(Chromosome chromosome, List<StateNode> states) {
        this.repairMembershipFunction.repair(states, boundaries, minMaxDataValue, chromosome);
    }

}
