/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.base;

import java.io.File;
import java.nio.file.Paths;

import com.castellanos.fuzzylogicgp.membershipfunction.MembershipFunction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.ScatterTrace;
import tech.tablesaw.plotly.traces.Trace;

/**
 *
 * @author hp
 */
public class StateNode extends Node {

    /**
     *
     */
    private static final long serialVersionUID = -196106920996217719L;
    @Expose
    private String label;
    @Expose
    private String colName;
    @Expose
    @SerializedName("f")
    private MembershipFunction membershipFunction;

    public StateNode() {
        setType(NodeType.STATE);
    }

    public StateNode(StateNode state) {
        this.label = state.getLabel();
        this.colName = state.getColName();
        this.setType(NodeType.STATE);
        if (state.getMembershipFunction() != null)
            this.membershipFunction = state.getMembershipFunction();
        this.setEditable(state.isEditable());
    }

    public StateNode(String label, String colName) {
        this.label = label;
        this.colName = colName;
        this.setType(NodeType.STATE);
        this.setEditable(false);
    }

    public StateNode(String label, String colName, MembershipFunction membershipFunction) {
        this.label = label;
        this.colName = colName;
        this.membershipFunction = membershipFunction;
        this.setEditable(false);
        this.setType(NodeType.STATE);

    }

    public String getColName() {
        return colName;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

    public void setMembershipFunction(MembershipFunction membershipFunction) {
        this.membershipFunction = membershipFunction;
    }

    public MembershipFunction getMembershipFunction() {
        return membershipFunction;
    }

    @Override
    public String toString() {
        if (this.membershipFunction != null) {
            return String.format("{:label \"%s\" :colname \"%s\" :f [%s]}", this.label, this.colName,
                    this.membershipFunction);
        } else {
            return String.format("{:label \"%s\" :colname \"%s\"}", this.label, this.colName);
        }
    }

    public String toJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        StateNode state = new StateNode();
        if (this.getLabel() != null)
            state.setLabel(label);
        if (this.getColName() != null)
            state.setColName(colName);
        if (this.getMembershipFunction() != null)
            state.setMembershipFunction((MembershipFunction) this.getMembershipFunction().clone());
        if (this.getByGenerator() != null)
            state.setByGenerator(this.getByGenerator());
        state.setEditable(this.isEditable());
        return state;
    }

    public void plot(String dirOutputString, String fileName) {

        Layout layout = Layout.builder().title(label + "(" + colName + "): " + membershipFunction.toString()).build();
        Trace trace = ScatterTrace.builder(membershipFunction.xPoints(), membershipFunction.yPoints()).build();
        if (dirOutputString != null)
            Plot.show(new Figure(layout, trace),
                    Paths.get(dirOutputString, ((fileName == null) ? label : fileName) + ".html").toFile());
        else
            Plot.show(new Figure(layout, trace), new File((fileName == null) ? label : fileName + ".html"));

    }
}
