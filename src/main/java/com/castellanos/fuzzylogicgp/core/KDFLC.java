package com.castellanos.fuzzylogicgp.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.castellanos.fuzzylogicgp.base.ANDNode;
import com.castellanos.fuzzylogicgp.base.EQVNode;
import com.castellanos.fuzzylogicgp.base.GeneratorNode;
import com.castellanos.fuzzylogicgp.base.IMPNode;
import com.castellanos.fuzzylogicgp.base.NOTNode;
import com.castellanos.fuzzylogicgp.base.Node;
import com.castellanos.fuzzylogicgp.base.NodeType;
import com.castellanos.fuzzylogicgp.base.ORNode;
import com.castellanos.fuzzylogicgp.base.OperatorException;
import com.castellanos.fuzzylogicgp.base.Predicate;
import com.castellanos.fuzzylogicgp.base.StateNode;
import com.castellanos.fuzzylogicgp.logic.ALogic;
import com.castellanos.fuzzylogicgp.logic.GMBC;
import com.castellanos.fuzzylogicgp.parser.ParserPredicate;

import tech.tablesaw.api.Table;

/**
 * fuzzy compensatory logical knowledge discovery
 * 
 * @author Castellanos Alvarez, Alejandro.
 * @since Oct, 19.
 * @version 0.0.1
 */
public class KDFLC {
    private ParserPredicate parserPredicate;
    private Predicate predicatePattern;
    private HashMap<String, List<StateNode>> statesByGenerators;
    private ALogic logic;
    private int depth;
    private int num_pop;
    private int num_iter;
    private int num_result;
    private double min_truth_value;
    private double mut_percentage;

    // GOMF Params
    private int adj_num_pop;
    private int adj_num_iter;
    private double adj_min_truth_value;

    // Aux
    private static final Random rand = new Random();
    private Table data;

    public KDFLC(ParserPredicate pp, ALogic logic, int depth, int num_pop, int num_iter, int num_result,
            double min_truth_value, double mut_percentage, int adj_num_pop, int adj_num_iter,
            double adj_min_truth_value, Table data) throws OperatorException {
        this.parserPredicate = pp;
        this.predicatePattern = pp.parser();
        this.logic = logic;
        this.depth = depth;
        this.num_pop = num_pop;
        this.num_iter = num_iter;
        this.num_result = num_result;
        this.min_truth_value = min_truth_value;
        this.mut_percentage = mut_percentage;
        this.adj_num_pop = adj_num_pop;
        this.adj_num_iter = adj_num_iter;
        this.adj_min_truth_value = adj_min_truth_value;
        this.data = data;
    }

    public void execute() {
        statesByGenerators = new HashMap<>();
        predicatePattern.getNodes().forEach((k, v) -> {
            if (v instanceof GeneratorNode) {
                GeneratorNode gNode = (GeneratorNode) v;
                List<StateNode> states = new ArrayList<>();
                for (String var : gNode.getVariables()) {
                    for (StateNode s : parserPredicate.getStates()) {
                        if (s.getLabel().equals(var)) {
                            states.add(new StateNode(s.getLabel(), s.getColName(), s.getMembershipFunction()));
                            break;
                        }
                    }
                }
                statesByGenerators.put(gNode.getId(), states);
            }
        });
        Predicate[] population = makePopulation();

    }

    private Predicate[] makePopulation() {
        Predicate[] pop = new Predicate[num_pop];
        for (int i = 0; i < pop.length; i++) {
            pop[i] = createRandomInd();
            System.out.printf("ind %3d: %s\n",i+1,pop[i]);
        }

        return pop;
    }

    private Predicate createRandomInd() {
        Predicate p = new Predicate(predicatePattern);
        Iterator<Node> iterator = p.getNodes().values().iterator();
        HashMap<String,HashMap<String,Node>> subTrees = new HashMap<>();
        while (iterator.hasNext()) {
            Node node = iterator.next();
            if ( node.getType() == NodeType.OPERATOR) {
                Node father = p.getNode(node.getFather());
                try {
                    // p.remove(father);
                    HashMap<String,Node> subTree = new HashMap<>();
                    complete_tree(p, (GeneratorNode) node, father, -1, p.dfs(node), subTree);
                    System.out.println(subTree);
                    subTrees.put(father.getId(), subTree);
                } catch (OperatorException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
        subTrees.forEach((k,v)->{
            p.remove(p.getNode(k));
            v.forEach((s,n)->{
                try {
                    p.addNode(p.getNode(s), n);
                } catch (OperatorException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });
        });

        return p;
    }

    private void complete_tree(Predicate p, GeneratorNode gNode, Node father, int arity, int currentDepth, HashMap<String,Node>subTree)
            throws OperatorException {
        if ( currentDepth == depth ) {

            int size = statesByGenerators.get(gNode.getId()).size();
            StateNode select = statesByGenerators.get(gNode.getId()).get(rand.nextInt(size));
            StateNode s = new StateNode(select.getLabel(), select.getColName(), select.getMembershipFunction());
            s.setFather(father.getId());
            subTree.put(father.getId(), s);            
        } else {
            
            arity = rand.nextInt(gNode.getVariables().size());
            
            Node newFather;
            NodeType nType = gNode.getOperators()[rand.nextInt(gNode.getOperators().length )];
            switch (nType) {
            case AND:
                newFather = new ANDNode();
                if(arity<2){
                    arity = 2;
                }
                break;
            case OR:
                newFather = new ORNode();
                if(arity < 2)
                    arity = 2;
                break;
            case IMP:
                newFather = new IMPNode();
                arity = 2;
                break;
            case EQV:
                newFather = new EQVNode();

                arity = 2;
                break;
            case NOT:
                newFather = new NOTNode();

                arity = 1;
                break;
            default:
                newFather = null;
            }
            newFather.setFather(father.getId());
            subTree.put(father.getId(), newFather);
            for (int i = 0; i < arity; i++)
                complete_tree(p, gNode, newFather, arity, currentDepth+1, subTree);
        }

    }

    private void mutation(Predicate[] population) {
    }

    private Predicate[] crossover(Predicate[] population) {
        Predicate[] childs = new Predicate[(population.length % 2 == 0) ? population.length / 2
                : population.length / 2 + 1];

        return childs;
    }

    private void evaluationChromosome(Predicate[] population) {
    }

    public static void main(String[] args) throws IOException, OperatorException {
        Table d = Table.read().csv("src/main/resources/datasets/tinto.csv");
        StateNode sa = new StateNode("alcohol", "alcohol");
        StateNode sph = new StateNode("pH", "pH");
        StateNode sq = new StateNode("quality", "quality");
        StateNode sfa = new StateNode("fixed_acidity", "fixed_acidity");
        List<StateNode> states = new ArrayList<>();
        states.add(sa);
        states.add(sph);
        states.add(sq);
        states.add(sfa);
        List<String> vars = new ArrayList<>();
        vars.add("alcohol");
        vars.add("pH");
         vars.add("fixed_acidity");

        GeneratorNode g = new GeneratorNode("*", new NodeType[] { NodeType.AND, NodeType.OR,NodeType.NOT }, vars);
        List<GeneratorNode> gs = new ArrayList<>();
        gs.add(g);
        String expression = "(NOT (AND \"*\" \"quality\") )";
        expression ="(NOT \"*\")";

        ParserPredicate pp = new ParserPredicate(expression, states, gs);

        KDFLC discovery = new KDFLC(pp, new GMBC(), 3, 5, 20, 10, 0.85, 0.05, 2, 1, 0.0, d);
        /*Predicate p = pp.parser();
        p.getNodes().forEach((k,v)->{
            System.out.println(v+", father = "+v.getFather()+" , level: "+p.dfs(v));
        });*/
        discovery.execute();
    }

}