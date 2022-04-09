package com.castellanos94.fuzzylogicgp;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import com.castellanos94.fuzzylogicgp.core.NodeTree;
import com.castellanos94.fuzzylogicgp.core.NodeType;
import com.castellanos94.fuzzylogicgp.core.OperatorException;
import com.castellanos94.fuzzylogicgp.core.StateNode;

import org.junit.Test;

/**
 * Unit test for nodetree node
 * 
 */
public class NodeTreeTest {
    @Test
    public void getByClass() throws OperatorException {
        ArrayList<StateNode> states = new ArrayList<>();
        states.add(new StateNode("citric_acid", "citric_acid"));
        states.add(new StateNode("volatile_acidity", "volatile_acidity"));
        NodeTree tree = new NodeTree(NodeType.AND);
        states.forEach(t -> {
            try {
                tree.addChild(t);
            } catch (OperatorException e) {
                e.printStackTrace();
            }
        });
        ArrayList<NodeTree> nodesByType = NodeTree.getNodesByType(tree, NodeTree.class);
        assertEquals(0, nodesByType.size());
        ArrayList<StateNode> stateNodes = NodeTree.getNodesByType(tree, StateNode.class);
        assertEquals(2, stateNodes.size());
    }
}
