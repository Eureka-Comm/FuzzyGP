package com.castellanos.fuzzylogicgp.base;

import java.util.ArrayList;

import com.castellanos.fuzzylogicgp.logic.ALogic;

public class Query {
    protected String db_uri;
    protected String out_file;
    protected ArrayList<StateNode> states;
    protected ALogic logic;
    protected String predicate;
    protected ArrayList<GeneratorNode> generators;
    protected int depth;
    protected int num_pop;
    protected int num_iter;
    protected int num_result;
    protected float min_truth_value;
    protected float mut_percentage;
    protected int adj_num_pop;
    protected int adj_min_truth_value;

    public String getDb_uri() {
        return db_uri;
    }

    public void setDb_uri(String db_uri) {
        this.db_uri = db_uri;
    }

    public String getOut_file() {
        return out_file;
    }

    public void setOut_file(String out_file) {
        this.out_file = out_file;
    }

    public ArrayList<StateNode> getStates() {
        return states;
    }

    public void setStates(ArrayList<StateNode> states) {
        this.states = states;
    }

    public ALogic getLogic() {
        return logic;
    }

    public void setLogic(ALogic logic) {
        this.logic = logic;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getNum_pop() {
        return num_pop;
    }

    public void setNum_pop(int num_pop) {
        this.num_pop = num_pop;
    }

    public int getNum_iter() {
        return num_iter;
    }

    public void setNum_iter(int num_iter) {
        this.num_iter = num_iter;
    }

    public int getNum_result() {
        return num_result;
    }

    public void setNum_result(int num_result) {
        this.num_result = num_result;
    }

    public float getMin_truth_value() {
        return min_truth_value;
    }

    public void setMin_truth_value(float min_truth_value) {
        this.min_truth_value = min_truth_value;
    }

    public float getMut_percentage() {
        return mut_percentage;
    }

    public void setMut_percentage(float mut_percentage) {
        this.mut_percentage = mut_percentage;
    }

    public int getAdj_num_pop() {
        return adj_num_pop;
    }

    public void setAdj_num_pop(int adj_num_pop) {
        this.adj_num_pop = adj_num_pop;
    }

    public int getAdj_min_truth_value() {
        return adj_min_truth_value;
    }

    public void setAdj_min_truth_value(int adj_min_truth_value) {
        this.adj_min_truth_value = adj_min_truth_value;
    }
    /**
     * @return the generators
     */
    public ArrayList<GeneratorNode> getGenerators() {
        return generators;
    }
    /**
     * @param generators the generators to set
     */
    public void setGenerators(ArrayList<GeneratorNode> generators) {
        this.generators = generators;
    }

}