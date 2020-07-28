package com.castellanos.fuzzylogicgp.parser;

import com.castellanos.fuzzylogicgp.base.NodeTree;
import com.google.gson.annotations.Expose;

public class EvaluationQuery extends Query  {

    /**
     *
     */
    private static final long serialVersionUID = 7821275859754726432L;
    @Expose
    private boolean showTree;
    @Expose 
    private NodeTree predicaTree;

    public NodeTree getPredicaTree() {
        return predicaTree;
    }
    public void setPredicaTree(NodeTree predicaTree) {
        this.predicaTree = predicaTree;
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
    @Override
    public String toString() {
        return "EvaluationQuery "+super.toString();
    }
  

    
}