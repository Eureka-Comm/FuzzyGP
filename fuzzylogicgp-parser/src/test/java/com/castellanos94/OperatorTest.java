package com.castellanos94;

import java.util.ArrayList;

import com.castellanos94.fuzzylogicgp.core.DummyGenerator;
import com.castellanos94.fuzzylogicgp.core.NodeTree;
import com.castellanos94.fuzzylogicgp.core.OperatorException;
import com.castellanos94.fuzzylogicgp.core.StateNode;
import com.castellanos94.fuzzylogicgp.parser.ParserPredicate;

import org.junit.Assert;
import org.junit.Test;

/**
 * Parser de expresiones con solo operadores
 */
public class OperatorTest {
    @Test
    public void andExpression() throws OperatorException, CloneNotSupportedException {
        String expression = "(AND \"a\" \"c\" \"q\" \"fa\")";
        ArrayList<StateNode> states = getStates();

        ArrayList<DummyGenerator> gs = new ArrayList<>();

        ParserPredicate parserPredicate = new ParserPredicate(expression, states, gs);
        NodeTree predicate = parserPredicate.parser();

        Assert.assertNotNull(predicate);

        Assert.assertEquals(predicate.toString(), expression);
    }

    @Test
    public void orExpression() throws OperatorException, CloneNotSupportedException {
        String expression = "(OR \"fa\" \"c\" \"q\")";
        ArrayList<StateNode> states = getStates();

        ArrayList<DummyGenerator> gs = new ArrayList<>();

        ParserPredicate parserPredicate = new ParserPredicate(expression, states, gs);
        NodeTree predicate = parserPredicate.parser();

        Assert.assertNotNull(predicate);

        Assert.assertEquals(predicate.toString(), expression);
    }

    @Test
    public void notExpression() throws OperatorException, CloneNotSupportedException {
        String expression = "(NOT \"a\")";
        ArrayList<StateNode> states = getStates();

        ArrayList<DummyGenerator> gs = new ArrayList<>();

        ParserPredicate parserPredicate = new ParserPredicate(expression, states, gs);
        NodeTree predicate = parserPredicate.parser();

        Assert.assertNotNull(predicate);

        Assert.assertEquals(predicate.toString(), expression);
    }

    @Test
    public void impExpression() throws OperatorException, CloneNotSupportedException {
        String expression = "(IMP \"a\" \"fa\")";
        ArrayList<StateNode> states = getStates();

        ArrayList<DummyGenerator> gs = new ArrayList<>();

        ParserPredicate parserPredicate = new ParserPredicate(expression, states, gs);
        NodeTree predicate = parserPredicate.parser();

        Assert.assertNotNull(predicate);

        Assert.assertEquals(predicate.toString(), expression);
    }

    @Test
    public void eqvExpression() throws OperatorException, CloneNotSupportedException {
        String expression = "(EQV \"a\" \"q\")";
        ArrayList<StateNode> states = getStates();

        ArrayList<DummyGenerator> gs = new ArrayList<>();

        ParserPredicate parserPredicate = new ParserPredicate(expression, states, gs);
        NodeTree predicate = parserPredicate.parser();

        Assert.assertNotNull(predicate);

        Assert.assertEquals(predicate.toString(), expression);
    }

    @Test
    public void impAntecedentNOTANDExpression() throws OperatorException, CloneNotSupportedException {
        String expression = "(IMP (NOT (AND \"a\" \"a\")) \"q\")";
        ArrayList<StateNode> states = getStates();

        ArrayList<DummyGenerator> gs = new ArrayList<>();

        ParserPredicate parserPredicate = new ParserPredicate(expression, states, gs);
        NodeTree predicate = parserPredicate.parser();

        Assert.assertNotNull(predicate);

        Assert.assertEquals(predicate.toString(), expression);
    }

    private ArrayList<StateNode> getStates() {
        ArrayList<StateNode> states = new ArrayList<>();
        states.add(new StateNode("a", "alcohol"));
        states.add(new StateNode("c", "chlorides"));
        states.add(new StateNode("q", "quality"));
        states.add(new StateNode("fa", "fixed_acidity"));
        return states;
    }

}
