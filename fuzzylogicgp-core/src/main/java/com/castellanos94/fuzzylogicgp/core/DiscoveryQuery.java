package com.castellanos94.fuzzylogicgp.core;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;
/**
 * Default max time 5 min for execution
 */
public class DiscoveryQuery extends Query {

    /**
     *
     */
    private static final long serialVersionUID = -5095255239602552073L;

    @Expose
    protected int num_pop;
    @Expose
    protected int num_iter;
    @Expose
    protected int num_result;
    @Expose
    protected float min_truth_value;
    @Expose
    protected float mut_percentage;
    @Expose
    protected int adj_num_pop;
    @Expose
    protected float adj_min_truth_value;
    @Expose
    protected int adj_num_iter;
    @Expose
    protected ArrayList<DummyGenerator> generators;
    @Expose
    protected long maxTime;

    public DiscoveryQuery() {
        setType(TaskType.DISCOVERY);
        this.maxTime = 5 * 60 * 1000;
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
        if (min_truth_value < 0.0 || min_truth_value > 1.0) {
            throw new IllegalArgumentException("Min truth value must be in [0,1].");
        }
        this.min_truth_value = min_truth_value;
    }

    public float getMut_percentage() {
        return mut_percentage;
    }

    public void setMut_percentage(float mut_percentage) {
        if (mut_percentage < 0.0 || mut_percentage > 1.0) {
            throw new IllegalArgumentException("Mut percentage must be in [0,1].");
        }
        this.mut_percentage = mut_percentage;
    }

    public int getAdj_num_pop() {
        return adj_num_pop;
    }

    public void setAdj_num_pop(int adj_num_pop) {
        this.adj_num_pop = adj_num_pop;
    }

    public float getAdj_min_truth_value() {
        return adj_min_truth_value;
    }

    public void setAdj_min_truth_value(float adj_min_truth_value) {
        if (adj_min_truth_value < 0.0 || adj_min_truth_value > 1.0) {
            throw new IllegalArgumentException("Adj min truth value must be in [0,1].");
        }

        this.adj_min_truth_value = adj_min_truth_value;
    }

    public int getAdj_num_iter() {
        return adj_num_iter;
    }

    public void setAdj_num_iter(int adj_num_iter) {
        this.adj_num_iter = adj_num_iter;
    }

    public ArrayList<DummyGenerator> getGenerators() {
        return generators;
    }

    public void setGenerators(ArrayList<DummyGenerator> generators) {
        this.generators = generators;
    }

    public long getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(long maxTime) {        
        this.maxTime = maxTime;        
    }

    @Override
    public String toString() {
        return "DiscoveryQuery [adj_min_truth_value=" + adj_min_truth_value + ", adj_num_iter=" + adj_num_iter
                + ", adj_num_pop=" + adj_num_pop + ", generators=" + generators + ", min_truth_value=" + min_truth_value
                + ", mut_percentage=" + mut_percentage + ", num_iter=" + num_iter + ", num_pop=" + num_pop
                + ", num_result=" + num_result +  ", maxTime=" + maxTime + "]" + super.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Float.floatToIntBits(adj_min_truth_value);
        result = prime * result + adj_num_iter;
        result = prime * result + adj_num_pop;
        result = prime * result + ((generators == null) ? 0 : generators.hashCode());
        result = prime * result + Float.floatToIntBits(min_truth_value);
        result = prime * result + Float.floatToIntBits(mut_percentage);
        result = prime * result + num_iter;
        result = prime * result + num_pop;
        result = prime * result + num_result;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DiscoveryQuery other = (DiscoveryQuery) obj;
        if (Float.floatToIntBits(adj_min_truth_value) != Float.floatToIntBits(other.adj_min_truth_value)) {
            return false;
        }
        if (adj_num_iter != other.adj_num_iter) {
            return false;
        }
        if (adj_num_pop != other.adj_num_pop) {
            return false;
        }

        if (generators == null) {
            if (other.generators != null) {
                return false;
            }
        } else if (!generators.equals(other.generators)) {
            return false;
        }
        if (Float.floatToIntBits(min_truth_value) != Float.floatToIntBits(other.min_truth_value)) {
            return false;
        }
        if (Float.floatToIntBits(mut_percentage) != Float.floatToIntBits(other.mut_percentage)) {
            return false;
        }
        if (num_iter != other.num_iter) {
            return false;
        }
        if (num_pop != other.num_pop) {
            return false;
        }
        if (num_result != other.num_result) {
            return false;
        }
        return true;
    }

}
