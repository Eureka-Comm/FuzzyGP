package com.castellanos.fuzzylogicgp.parser;

import java.io.Serializable;

import com.castellanos.fuzzylogicgp.membershipfunction.AMembershipFunction;

public class DummyState implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = -8442298849631361229L;
    protected String label;
    protected String colname;
    protected AMembershipFunction f;
    public DummyState(String label, String colname){
        this.colname =colname;
        this.label = label;
    }
    public DummyState(String label, String colname, AMembershipFunction f) {
        this.label =label;
        this.colname =colname;
        this.f = f;
	}
	public String getColname() {
        return colname;
    }
    public AMembershipFunction getF() {
        return f;
    }
    public String getLabel() {
        return label;
    }
    public void setColname(String colname) {
        this.colname = colname;
    }
    public void setF(AMembershipFunction f) {
        this.f = f;
    }
    public void setLabel(String label) {
        this.label = label;
    }

}