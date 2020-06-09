package com.castellanos.fuzzylogicgp.parser;

import java.util.Set;

public class DiscoveryQuery extends Query {
    
    /**
     *
     */
    private static final long serialVersionUID = -5095255239602552073L;
    protected Set<String> generators;
    protected int depth;
    protected int num_pop;
    protected int num_iter;
    protected int num_result;
    protected float min_truth_value;
    protected float mut_percentage;
    protected int adj_num_pop;
    protected int adj_min_truth_value;

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
    public void setGenerators(Set<String> generators) {
        this.generators = generators;
    }
    public Set<String> getGenerators() {
        return generators;
    }


}