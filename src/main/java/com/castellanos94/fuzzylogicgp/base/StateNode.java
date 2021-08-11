/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos94.fuzzylogicgp.base;

import com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunction;
import com.castellanos94.fuzzylogicgp.membershipfunction.Point;
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
import java.util.ArrayList;
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
    private String cname;
    @Expose
    @SerializedName("f")
    private MembershipFunction membershipFunction;

    public StateNode() {
        setType(NodeType.STATE);
    }

    public StateNode(StateNode state) {
        this.label = state.getLabel();
        this.cname = state.getColName();
        this.setType(NodeType.STATE);
        if (state.getMembershipFunction() != null)
            this.membershipFunction = state.getMembershipFunction();
        this.setEditable(state.isEditable());
    }

    public StateNode(String label, String cname) {
        this.label = label;
        this.cname = cname;
        this.setType(NodeType.STATE);
        this.setEditable(false);
    }

    public StateNode(String label, String cname, MembershipFunction membershipFunction) {
        this.label = label;
        this.cname = cname;
        this.membershipFunction = membershipFunction;
        this.setEditable(false);
        this.setType(NodeType.STATE);

    }

    public String getColName() {
        return cname;
    }

    public void setColName(String cname) {
        this.cname = cname;
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
            return String.format("{:label \"%s\", :colname \"%s\", :f %s}", this.label, this.cname,
                    this.membershipFunction);
        } else {
            return String.format("{:label \"%s\", :colname \"%s\"}", this.label, this.cname);
        }
    }

    @Override
    public StateNode copy() {
        StateNode state = new StateNode();
        if (this.getLabel() != null)
            state.setLabel(label);
        if (this.getColName() != null)
            state.setColName(cname);
        if (this.getMembershipFunction() != null)
            state.setMembershipFunction((MembershipFunction) this.getMembershipFunction().copy());
        if (this.getByGenerator() != null)
            state.setByGenerator(this.getByGenerator());
        state.setEditable(this.isEditable());
        state.setDescription(this.description);
        return state;
    }

    public void plot(String dirOutputString, String fileName) {

        Layout layout = Layout.builder().title(label + "(" + cname + "): " + membershipFunction.toString()).build();
        ArrayList<Point> points = (ArrayList<Point>) membershipFunction.getPoints();
        Trace trace = null;
        if (points.size() <= 1000) {
            double[] x = new double[points.size()];
            double[] y = new double[points.size()];
            for (int i = 0; i < y.length; i++) {
                x[i] = points.get(i).getX();
                y[i] = points.get(i).getY();
            }
            trace = ScatterTrace.builder(x, y).mode(ScatterTrace.Mode.LINE).build();
        } else {
            DoubleColumn xdc = DoubleColumn.create("x");
            DoubleColumn ydc = DoubleColumn.create("y");
            List<Integer> ret = IntStream.range(0, points.size()).boxed().collect(Collectors.toList());
            Collections.shuffle(ret);
            double[] values = new double[101];
            for (int i = 0; i < values.length; i++) {
                values[i] = i / 100.0;
            }
            System.out.println(points.size());
            int[] count = new int[values.length];
            boolean included_one = false;
            for (int i = 0; i < points.size(); i++) {
                Point v = points.get(i);
                for (int l = 0; l < values.length; l++) {
                    if (v.getY() > 0.00006 && v.getY() <= values[l] && count[l] < 20) {
                        xdc.append(v.getX());
                        ydc.append(v.getY());
                        count[l]++;
                        if (v.getY() > 0.95 && v.getY() <= 1) {
                            included_one = true;
                        }
                        break;
                    }
                }
            }
            if (!included_one) {
                int n = 0;
                for (int i = 0; i < points.size() && n < 200; i++) {
                    if (points.get(i).getY() <= 1.0 && points.get(i).getY() >= 0.95) {
                        xdc.append(points.get(i).getX());
                        ydc.append(points.get(i).getY());
                        n++;
                    }
                }
            }
            trace = ScatterTrace.builder(xdc, ydc).mode(ScatterTrace.Mode.LINE).build();
        }
        if (dirOutputString != null)
            Plot.show(new Figure(layout, trace),
                    Paths.get(dirOutputString, ((fileName == null) ? label : fileName) + ".html").toFile());
        else
            Plot.show(new Figure(layout, trace), new File((fileName == null) ? label : fileName + ".html"));

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cname == null) ? 0 : cname.hashCode());
        result = prime * result + ((membershipFunction == null) ? 0 : membershipFunction.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StateNode other = (StateNode) obj;
        if (cname == null) {
            if (other.cname != null)
                return false;
        } else if (!cname.equals(other.cname))
            return false;
        if (membershipFunction == null) {
            if (other.membershipFunction != null)
                return false;
        } else if (!membershipFunction.equals(other.membershipFunction))
            return false;
        return true;
    }

}