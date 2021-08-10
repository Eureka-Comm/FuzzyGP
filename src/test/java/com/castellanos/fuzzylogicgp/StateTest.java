package com.castellanos.fuzzylogicgp;

import static org.junit.Assert.assertEquals;

import com.castellanos.fuzzylogicgp.base.StateNode;

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
}
