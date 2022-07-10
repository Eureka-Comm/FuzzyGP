package com.castellanos94.fuzzylogicgp.core;

import java.util.HashMap;
import java.util.List;

public class EvaluationResult extends ResultTask {
    protected final Double forAll;
    protected final Double exists;
    protected final List<Double> result;
    protected final HashMap<String, List<Double>> extend;

    public EvaluationResult(Double forAll, Double exists, List<Double> result,
            HashMap<String, List<Double>> extend) {
        this.forAll = forAll;
        this.exists = exists;
        this.result = result;
        this.extend = extend;
    }

    public Double getExists() {
        return exists;
    }

    public Double getForAll() {
        return forAll;
    }

    public List<Double> getResult() {
        return result;
    }

    public HashMap<String, List<Double>> getExtend() {
        return extend;
    }
}