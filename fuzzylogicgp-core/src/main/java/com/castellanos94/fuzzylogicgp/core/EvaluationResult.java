package com.castellanos94.fuzzylogicgp.core;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EvaluationResult extends ResultTask {
    
    protected String predicate;
    protected final Double forAll;
    protected final Double exists;
    protected final List<Double> result;
    protected final Map<String, List<Double>> extend;

    public EvaluationResult(Double forAll, Double exists, List<Double> result,
            Map<String, List<Double>> extend) {
        this.forAll = forAll;
        this.exists = exists;
        this.result = Collections.unmodifiableList(result);
        if (extend != null) {
            this.extend = Collections.unmodifiableMap(extend);
        } else {
            this.extend = null;
        }
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
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

    public Map<String, List<Double>> getExtend() {
        return extend;
    }

    @Override
    public String toString() {
        return "EvaluationResult [exists=" + exists + ", extend=" + extend + ", forAll=" + forAll + ", result=" + result
                + "]";
    }

}