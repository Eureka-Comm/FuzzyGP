/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.base;

import java.util.List;

/**
 *
 * @author hp
 */
public abstract class Operator extends Node {

    private List<Node> childs;
    private double fitness;

    public Operator() {
    }

    public void setChilds(List<Node> childs) {
        this.childs = childs;
    }

    public List<Node> getChilds() {
        return childs;
    }

    public boolean addChild(Node e) throws OperatorException {
        return this.childs.add(e);
    }

    public boolean remove(Node e) {
        return this.childs.remove(e);
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public boolean isValid() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        String st = "(" + this.getType() + " ";
        for (Node e : this.childs) {
            switch (e.getType()) {
                case STATE:
                    State s = (State) e;
                    st += "\"" + s.getLabel() + "\" ";
                    break;
                case OPERATOR:
                    Generator g = (Generator) e;
                    st += "\"" + g.getLabel() + "\" ";
                    break;
                default:
                    st += e.toString() + " ";
                    break;
            }
        }
        return st.trim() + ")";
    }

}
