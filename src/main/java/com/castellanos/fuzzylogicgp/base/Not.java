/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.base;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hp
 */
public class Not extends Operator {

    public Not() {
        setChilds(new ArrayList<>());
        setType(NodeType.NOT);
    }

    public Not(List<Node> childs) {
        setChilds(childs);
        setType(NodeType.NOT);
    }

    @Override
    public boolean addChild(Node e) throws OperatorException {
        if (this.getChilds().isEmpty()) {
            return super.addChild(e); //To change body of generated methods, choose Tools | Templates.
        }
       throw new OperatorException(this.getId()+" "+this.getType() + ": arity must be one element.");
      
    }

}
