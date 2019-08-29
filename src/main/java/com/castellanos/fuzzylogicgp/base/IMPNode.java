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
public class IMPNode extends OperatorNode {
    private String leftID;
    private String righID;
    
    public IMPNode() {
        setType(NodeType.IMP);
    }

    public String getLeftID() {
        return leftID;
    }

    public String getRighID() {
        return righID;
    }

    public void setLeftID(String leftID) {
        this.leftID = leftID;
    }

    public void setRighID(String righID) {
        this.righID = righID;
    }
    
   
}
