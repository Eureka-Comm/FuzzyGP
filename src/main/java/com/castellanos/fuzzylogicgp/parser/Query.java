package com.castellanos.fuzzylogicgp.parser;

import java.io.Serializable;
import java.util.ArrayList;

import com.castellanos.fuzzylogicgp.base.StateNode;
import com.castellanos.fuzzylogicgp.logic.ALogic;

public class Query implements Serializable{
    
    /**
     *
     */
    private static final long serialVersionUID = -4688673109726568325L;
    protected String db_uri;
    protected String out_file;
    protected ArrayList<DummyState> states;
    protected LogicType logic;
    protected String predicate;
    public Query(){}
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

    public ArrayList<DummyState> getStates() {
        return states;
    }

    public void setStates(ArrayList<DummyState> states) {
        this.states = states;
    }

   public LogicType getLogic() {
       return logic;
   }
   public void setLogic(LogicType logic) {
       this.logic = logic;
   }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    
}