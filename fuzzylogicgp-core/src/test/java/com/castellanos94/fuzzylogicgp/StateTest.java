package com.castellanos94.fuzzylogicgp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import com.castellanos94.fuzzylogicgp.core.StateNode;

import org.junit.Test;

/**
 * Unit test for state
 */
public class StateTest {
    @Test
    public void testSimpleState() {
        String label = "high alcohol";
        String cname = "alcohol";
        StateNode stateNode = new StateNode(label, cname);
        assertEquals(String.format("{:label \"%s\", :colname \"%s\"}", label, cname), stateNode.toString());
    }
    @Test
    public void testStateCopy() {
        String label = "high alcohol";
        String cname = "alcohol";
        StateNode stateNode = new StateNode(label, cname);
        StateNode copyNode = stateNode.copy();
        assertNotEquals(copyNode.getId(), stateNode.getId());
    }
}
