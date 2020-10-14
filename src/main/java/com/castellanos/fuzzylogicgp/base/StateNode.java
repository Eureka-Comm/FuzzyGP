/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.base;

import com.castellanos.fuzzylogicgp.membershipfunction.MembershipFunction;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.ScatterTrace;
import tech.tablesaw.plotly.traces.Trace;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
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

    public void setColName(String colName) {
        this.colName = colName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public MembershipFunction getMembershipFunction() {
        return membershipFunction;
    }

    public void setMembershipFunction(MembershipFunction membershipFunction) {
        this.membershipFunction = membershipFunction;
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
        DoubleColumn xPoints = (DoubleColumn) membershipFunction.xPoints();
        DoubleColumn yPoints = (DoubleColumn) membershipFunction.yPoints();
        Trace trace = null;
        if (yPoints.size() <= 1000) {
            trace = ScatterTrace.builder(xPoints, yPoints).build();
        } else {
            DoubleColumn xdc = DoubleColumn.create("x");
            DoubleColumn ydc = DoubleColumn.create("y");
            List<Integer> ret = IntStream.range(0, xPoints.size()).boxed().collect(Collectors.toList());
            Collections.shuffle(ret);
            double[] values = new double[101];
            for (int i = 0; i < values.length; i++) {
                values[i] = i/100.0;
            }
            System.out.println(yPoints.size());
            int []count = new int[values.length];
            boolean included_one = false;
            for (int i = 0; i < xPoints.size(); i++) {
                double v = yPoints.get(i);
                for (int l = 0; l < values.length; l++) {
                    if(v > 0.00006 && v <= values[l] && count[l]<20){
                        xdc.append(xPoints.get(i));
                        ydc.append(v);
                        count[l]++;
                        if(v >0.95 && v<= 1){
                            included_one = true;
                        }
                        break;
                    }
                }
            }
            if (!included_one){
                int n = 0;
                for (int i = 0; i < xPoints.size() && n < 200; i++) {
                    if(yPoints.get(i)<=1.0 &&  yPoints.get(i) >= 0.95 ){
                        xdc.append(xPoints.get(i));
                        ydc.append(yPoints.get(i));
                        n++;
                    }
                }
            }
            trace = ScatterTrace.builder(xdc, ydc).build();
        }
        if (dirOutputString != null)
            Plot.show(new Figure(layout, trace),
                    Paths.get(dirOutputString, ((fileName == null) ? label : fileName) + ".html").toFile());
        else
            Plot.show(new Figure(layout, trace), new File((fileName == null) ? label : fileName + ".html"));

    }
}
