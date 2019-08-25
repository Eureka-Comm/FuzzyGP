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
public class And extends Operator {

    public And() {
        setChilds(new ArrayList<>());
        setType(NodeType.AND);
    }

    public And(List<Node> childs) {
        setChilds(childs);
        setType(NodeType.AND);

    }

}
