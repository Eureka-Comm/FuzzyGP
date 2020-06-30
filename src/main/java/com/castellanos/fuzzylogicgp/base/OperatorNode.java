/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.base;


/**
 *
 * @author hp
 */
public class OperatorNode extends Node {

    /**
     *
     */
    private static final long serialVersionUID = -4847569043801222596L;
    private double fitness;
    private String leftID;
    private String righID;

    public OperatorNode(NodeType type) {
        this.setType(type);
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public boolean isValid() {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
                                                                       // Tools | Templates.
    }

    @Override
    public String toString() {
        return this.getType().toString();
    }

    /**
     * @return the leftID
     */
    public String getLeftID() {
        return leftID;
    }

    /**
     * @param leftID the leftID to set
     */
    public void setLeftID(String leftID) {
        this.leftID = leftID;
    }

    /**
     * @return the righID
     */
    public String getRighID() {
        return righID;
    }

    /**
     * @param righID the righID to set
     */
    public void setRighID(String righID) {
        this.righID = righID;
    }
}
