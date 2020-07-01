package com.castellanos.fuzzylogicgp.parser;

import com.google.gson.annotations.Expose;

public class EvaluationQuery extends Query  {

    /**
     *
     */
    private static final long serialVersionUID = 7821275859754726432L;
    @Expose
    private boolean showTree;
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
    @Override
    public String toString() {
        return "EvaluationQuery "+super.toString();
    }
  

    
}