package com.castellanos94;

import java.util.ArrayList;

import com.castellanos94.fuzzylogicgp.core.DummyGenerator;
import com.castellanos94.fuzzylogicgp.core.NodeTree;
import com.castellanos94.fuzzylogicgp.core.NodeType;
import com.castellanos94.fuzzylogicgp.core.OperatorException;
import com.castellanos94.fuzzylogicgp.core.StateNode;
import com.castellanos94.fuzzylogicgp.membershipfunction.Sigmoid;
import com.castellanos94.fuzzylogicgp.parser.ParserPredicate;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for Dummy generator parser
 */
public class DummyGeneratorTest {

    @Test
    public void dummyGeneratorToPredicate() throws OperatorException, CloneNotSupportedException {
        String expression = "(\"calidad\")";
        NodeType[] operators = { NodeType.AND, NodeType.OR, NodeType.NOT };
        ArrayList<StateNode> states = getStates();
        ArrayList<String> variables = new ArrayList<>();
        states.stream().map(StateNode::getLabel).forEach(variables::add);
        DummyGenerator generator = new DummyGenerator("calidad", operators, variables, 2);
        ArrayList<DummyGenerator> gs = new ArrayList<>();
        gs.add(generator);
        ParserPredicate parserPredicate = new ParserPredicate(expression, states, gs);
        NodeTree predicate = parserPredicate.parser();
        Assert.assertNotNull(predicate);
        Assert.assertEquals(predicate.toString(), expression);
    }

    @Test
    public void impExpression() throws OperatorException, CloneNotSupportedException {
        String expression = "(IMP \"calidad\" \"high quality\")";
        ArrayList<StateNode> states = getStates();

        ArrayList<String> variables = new ArrayList<>();
        states.stream().map(StateNode::getLabel).forEach(variables::add);
        states.add(new StateNode("high quality", "quality", new Sigmoid(5.5, 4)));

        NodeType[] operators = { NodeType.AND, NodeType.OR, NodeType.NOT,NodeType.EQV,NodeType.IMP};

        DummyGenerator generator = new DummyGenerator("calidad", operators, variables, 2);
        ArrayList<DummyGenerator> gs = new ArrayList<>();
        gs.add(generator);

        ParserPredicate parserPredicate = new ParserPredicate(expression, states, gs);
        NodeTree predicate = parserPredicate.parser();

        Assert.assertNotNull(predicate);

        Assert.assertEquals(predicate.toString(), expression);
    }

    private ArrayList<StateNode> getStates() {
        ArrayList<StateNode> states = new ArrayList<>();
        states.add(new StateNode("a", "alcohol"));
        states.add(new StateNode("c", "chlorides"));
        states.add(new StateNode("fa", "fixed_acidity"));

        states.add(new StateNode("ca", "citric_acid"));
        states.add(new StateNode("va", "volatile_acidity"));
        states.add(new StateNode("fsd", "free_sulfur_dioxide"));
        states.add(new StateNode("s", "sulphates"));
        states.add(new StateNode("rs", "residual_sugar"));
        states.add(new StateNode("ph", "pH"));
        states.add(new StateNode("tsd", "total_sulfur_dioxide"));
        states.add(new StateNode("d", "density"));
        return states;
    }
}
