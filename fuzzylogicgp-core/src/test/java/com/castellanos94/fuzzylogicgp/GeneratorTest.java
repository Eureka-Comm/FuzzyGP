package com.castellanos94.fuzzylogicgp;

import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import java.util.ArrayList;

import com.castellanos94.fuzzylogicgp.core.NodeType;
import com.castellanos94.fuzzylogicgp.core.StateNode;
import com.castellanos94.fuzzylogicgp.core.DummyGenerator;

import org.junit.Test;

/**
 * Unit test for generator node
 * 
 */
public class GeneratorTest {
    @Test
    public void simpleGenerator() {
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
        DummyGenerator generator = new DummyGenerator();
        generator.setLabel("todos los estados");
        ArrayList<String> variables = new ArrayList<>();
        for (StateNode stateNode : states) {
            variables.add(stateNode.getLabel());
        }
        NodeType[] operators = new NodeType[] { NodeType.AND, NodeType.OR, NodeType.IMP, NodeType.EQV, NodeType.NOT };
        generator.setVariables(variables);
        generator.setOperators(operators);
        generator.setDepth(2);
        assertEquals("DummyGeneratorNode [depth=" + 2 + ", label=" + generator.getLabel() + ", operators="
                + Arrays.toString(operators) + ", variables=" + variables + "]", generator.toString());

    }
}
