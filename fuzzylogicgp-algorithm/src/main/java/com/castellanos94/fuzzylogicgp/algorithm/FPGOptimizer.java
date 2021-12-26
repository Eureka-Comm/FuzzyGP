package com.castellanos94.fuzzylogicgp.algorithm;

import java.io.IOException;
import java.util.ArrayList;
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
    protected double mutationRate;
    protected double crossoverRate;
    protected Table data;
    protected HashMap<String, Double[]> minMaxDataValue;
    protected Random random;

    public static void main(String[] args) throws IOException, OperatorException {
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
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
        FPGOptimizer optimizer = new FPGOptimizer(new GMBC_Logic(), table, 100, 20, 0.9, 0.95, 0.1);
        NodeTree execute = optimizer.execute(and);
        System.out.println(execute + " f " + execute.getFitness());
    }

    /**
     * Default constructor
     * 
     * @param logic          - logic to evalute predicate
     * @param data           - dataset
     * @param maxIterations  - max iterations
     * @param populationSize - population size
     * @param minTruthValue  - minimum truth value of fitness
     * @param crossoverRate  - crossover probability
     * @param mutationRate   - mutation probability
     */
    public FPGOptimizer(Logic logic, Table data, int maxIterations, int populationSize, double minTruthValue,
            double crossoverRate,
            double mutationRate) {
        super(logic);
        this.data = data;
        this.evaluatePredicate = new EvaluatePredicate(logic, data);
        this.maxIterations = maxIterations;
        this.populationSize = populationSize;
        this.minTruthValue = minTruthValue;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.random = new Random(1);
    }

    @Override
    public NodeTree execute(NodeTree predicate) {
        // filter states with null MF
        final List<StateNode> statesToWork = NodeTree.getNodesByType(predicate, NodeType.STATE).stream()
                .filter(n -> n instanceof StateNode)
                .map(n -> (StateNode) n).filter(s -> s.getMembershipFunction() == null).collect(Collectors.toList());
        minMaxDataValue = new HashMap<>();
        statesToWork.parallelStream().forEach(state -> {
            NumericColumn<?> doubleColumn = data.numberColumn(state.getColName());
            Double[] _minMax = new Double[2];
            _minMax[0] = doubleColumn.min();
            _minMax[1] = doubleColumn.max();
            minMaxDataValue.put(state.getId(), _minMax);
        });
        // Random population
        FPG[][] population = new FPG[populationSize][statesToWork.size()];
        for (int i = 0; i < populationSize; i++) {
            population[i] = generateIndividual(statesToWork);
            population[i] = generateIndividual(statesToWork);
        }
        // Evaluate first iteratior
        final double[] fitness = new double[populationSize];
        for (int i = 0; i < populationSize; i++) {
            fitness[i] = _evaluate(predicate, statesToWork, population[i]);
            System.out.printf("Evaluate %3d : %.05f\n", (i + 1), fitness[i]);
        }
        // Main for
        for (int i = 1; i < maxIterations; i++) {

        }
        return predicate;
    }

    private double _evaluate(NodeTree predicate, List<StateNode> statesToWork, FPG[] fpgs) {
        for (int i = 0; i < fpgs.length; i++) {
            statesToWork.get(i).setMembershipFunction(fpgs[i]);
        }
        return evaluatePredicate.evaluate(predicate);
    }

    private FPG[] generateIndividual(List<StateNode> statesToWork) {
        FPG[] chromosome = new FPG[statesToWork.size()];
        for (int i = 0; i < chromosome.length; i++) {
            String idCname = statesToWork.get(i).getId();
            Double[] ref = minMaxDataValue.get(idCname);
            double beta = random.doubles(ref[0], ref[1] + 1).findAny().getAsDouble();
            double gamma = random.doubles(beta, ref[1] + 1).findAny().getAsDouble();
            while (Double.compare(gamma, beta) <= 0) {
                gamma = random.doubles(beta, ref[1] + 1).findAny().getAsDouble();
            }
            chromosome[i] = new FPG(beta, gamma, random.nextInt(100001) / 100001.0);
        }
        return chromosome;
    }

    @Override
    protected List<MembershipFunction> crossover(MembershipFunction... functions) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected MembershipFunction mutation(MembershipFunction membershipFunction) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected MembershipFunction repair(MembershipFunction membershipFunction) {
        // TODO Auto-generated method stub
        return null;
    }

}
