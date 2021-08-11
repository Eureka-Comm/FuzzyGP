package com.castellanos94.fuzzylogicgp;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import com.castellanos94.fuzzylogicgp.base.NodeTree;
import com.castellanos94.fuzzylogicgp.base.NodeType;
import com.castellanos94.fuzzylogicgp.base.OperatorException;
import com.castellanos94.fuzzylogicgp.base.StateNode;

import org.junit.Test;

public class OperatorTest {
    @Test
    public void toStringOperatorTest() throws OperatorException {
        NodeTree not = new NodeTree(NodeType.NOT);
        StateNode ha = new StateNode("high alcohol", "alcohol");
        not.addChild(ha);

        assertEquals("(NOT \"high alcohol\")", not.toString());

        StateNode ca = new StateNode("citric_acid", "citric_acid");
        StateNode fa = new StateNode("fixed_acidity", "fixed_acidity");
        NodeTree and = new NodeTree(NodeType.AND);
        and.addChild(ha);
        and.addChild(ca);
        and.addChild(fa);
        assertEquals("(AND \"high alcohol\" \"citric_acid\" \"fixed_acidity\")", and.toString());

    }

    @Test(expected = OperatorException.class)
    public void notExpectionTest() throws OperatorException {
        NodeTree not = new NodeTree(NodeType.NOT);
        for (StateNode stateNode : getStates()) {
            not.addChild(stateNode);
        }
    }
    @Test(expected = OperatorException.class)
    public void impExpectionTest() throws OperatorException {
        NodeTree not = new NodeTree(NodeType.IMP);
        for (StateNode stateNode : getStates()) {
            not.addChild(stateNode);
        }
    }
    @Test(expected = OperatorException.class)
    public void eqvExpectionTest() throws OperatorException {
        NodeTree not = new NodeTree(NodeType.EQV);
        for (StateNode stateNode : getStates()) {
            not.addChild(stateNode);
        }
    }

    public ArrayList<StateNode> getStates() {
        ArrayList<StateNode> states = new ArrayList<>();
        states.add(new StateNode("citric_acid", "citric_acid"));
        states.add(new StateNode("volatile_acidity", "volatile_acidity"));
        states.add(new StateNode("fixed_acidity", "fixed_acidity"));
        states.add(new StateNode("free_sulfur_dioxide", "free_sulfur_dioxide"));
        states.add(new StateNode("sulphates", "sulphates"));
        states.add(new StateNode("alcohol", "alcohol"));
        states.add(new StateNode("residual_sugar", "residual_sugar"));
        states.add(new StateNode("pH", "pH"));
        states.add(new StateNode("total_sulfur_dioxide", "total_sulfur_dioxide"));
        states.add(new StateNode("quality", "quality"));
        states.add(new StateNode("density", "density"));
        states.add(new StateNode("chlorides", "chlorides"));
        return states;
    }
}
