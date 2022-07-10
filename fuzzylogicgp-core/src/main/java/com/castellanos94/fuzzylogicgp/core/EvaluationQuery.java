package com.castellanos94.fuzzylogicgp.core;

import com.google.gson.annotations.Expose;

public class EvaluationQuery extends Query {

    /**
     *
     */
    private static final long serialVersionUID = 7821275859754726432L;
    @Expose
    private boolean showTree;
    @Expose
    private boolean includeFuzzyData;
    @Expose
    private String jsonPredicate;

    public String getJsonPredicate() {
        return jsonPredicate;
    }

    public void setJsonPredicate(String jsonPredicate) {
        this.jsonPredicate = jsonPredicate;
    }

    public EvaluationQuery() {
        setType(TaskType.EVALUATION);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public boolean isShowTree() {
        return showTree;
    }

    public void setShowTree(boolean showTree) {
        this.showTree = showTree;
    }

    public boolean isIncludeFuzzyData() {
        return includeFuzzyData;
    }

    public void setIncludeFuzzyData(boolean includeFuzzyData) {
        this.includeFuzzyData = includeFuzzyData;
    }

    @Override
    public String toString() {
        return "EvaluationQuery " + super.toString();
    }

}