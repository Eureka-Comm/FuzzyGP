package com.castellanos.fuzzylogicgp.parser;


public class EvaluationQuery extends Query  {

    /**
     *
     */
    private static final long serialVersionUID = 7821275859754726432L;

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

    @Override
    public String toString() {
        return "EvaluationQuery "+super.toString();
    }
  

    
}