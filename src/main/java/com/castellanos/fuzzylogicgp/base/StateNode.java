/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.base;



import com.castellanos.fuzzylogicgp.membershipfunction.AMembershipFunction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
    private AMembershipFunction membershipFunction;

    public StateNode() {
        setType(NodeType.STATE);
    }

    public static StateNode parseState(String string) {
        StateNode s = new StateNode();
        String split[] = string.replace("{", "").replace("}", "").trim().split(":");
        String label = null, colname = "";
        for (String st : split) {
            String sub[] = st.split(" ");
            if (sub[0].contains("label")) {
                label = sub[1].replaceAll("\"", "").trim();
            } else if (sub[0].contains("colname")) {
                colname = sub[1].replaceAll("\"", "").trim();
            } else if (sub[0].contains("f")) {
                System.out.println(st);
            }
        }
        System.out.println(label + " " + colname);
        return null;
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

    public StateNode(String label, String colName, AMembershipFunction membershipFunction) {
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

    public void setMembershipFunction(AMembershipFunction membershipFunction) {
        this.membershipFunction = membershipFunction;
    }

    public AMembershipFunction getMembershipFunction() {
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
        if(label!=null)
        state.setLabel(label);
        if(colName!=null)
        state.setColName(colName);
        if (membershipFunction != null)
            state.setMembershipFunction((AMembershipFunction) this.getMembershipFunction().clone());
        if(this.getByGenerator()!=null)
            state.setByGenerator(this.getByGenerator());
        state.setEditable(this.isEditable());
        return state;
    }
}
