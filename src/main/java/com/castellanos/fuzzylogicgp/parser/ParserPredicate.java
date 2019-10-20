/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.parser;

import com.castellanos.fuzzylogicgp.base.ANDNode;
import com.castellanos.fuzzylogicgp.base.EQVNode;
import com.castellanos.fuzzylogicgp.base.GeneratorNode;
import com.castellanos.fuzzylogicgp.base.IMPNode;
import com.castellanos.fuzzylogicgp.base.NOTNode;
import com.castellanos.fuzzylogicgp.base.Node;
import com.castellanos.fuzzylogicgp.base.ORNode;
import com.castellanos.fuzzylogicgp.base.OperatorException;
import com.castellanos.fuzzylogicgp.base.OperatorNode;
import com.castellanos.fuzzylogicgp.base.Predicate;
import com.castellanos.fuzzylogicgp.base.StateNode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

/**
 *
 * @author hp
 */
public class ParserPredicate {

    private final String expression;
    private Stack<String> stack;
    private Node currentNodeRoot;
    private Predicate predicate;
    private final List<StateNode> states;
    private final List<GeneratorNode> generators;

    public ParserPredicate(String expression, List<StateNode> states, List<GeneratorNode> gs) {
        this.expression = expression;
        this.states = states;
        this.generators = gs;
        stack = new Stack<>();
    }

    public Predicate parser() throws OperatorException {
        List<String> split = expressionSplit(expression);
        predicate = new Predicate();
        if (isBalanced(split)) {
            Iterator<String> stringIterator = split.iterator();
            String rootString;

            while (stringIterator.hasNext()) {
                rootString = stringIterator.next();
                switch (rootString) {
                    case "(":
                        break;
                    case ")":
                        if (currentNodeRoot != null && currentNodeRoot.getFather() != null) {
                            currentNodeRoot = predicate.getNode(currentNodeRoot.getFather());
                        }
                        break;
                    default:
                        createNodeFromExpre(rootString);
                        break;
                }

            }
            return (predicate.isValid()) ? predicate : null;

        }
        throw new UnsupportedOperationException("Missing ')'"); //To change body of generated methods, choose Tools | Templates.

    }

    private boolean isBalanced(List<String> stringList) {
        for (String s : stringList) {  // Recorremos la expresión carácter a carácter
            if (s.equals("(")) {
                // Si el paréntesis es de apertura apilamos siempre
                stack.push(s);
            } else if (s.equals(")")) {
                // Si el paréntesis es de cierre actuamos según el caso
                if (!stack.empty()) {
                    stack.pop();// Si la stringStack no está vacía desapilamos
                } else {
                    // La stringStack no puede empezar con un cierre, apilamos y salimos
                    stack.push(")");
                    break;
                }

            }
        }
        return stack.isEmpty();
    }

    private List<String> expressionSplit(String cadena) {
        List<String> elementos = new ArrayList<>();
        char b;
        int i1, i2;

        for (i1 = 0, i2 = 0; i2 < cadena.length();) {
            switch (cadena.charAt(i1)) {
                case '(':
                case ')':
                    if (cadena.charAt(i1) == '(') {
                        elementos.add("(");
                    } else {
                        elementos.add(")");
                    }
                    i1++;
                    i2 = i1;
                    break;
                case '\"':
                    i1++;
                    i2 = i1;
                    while (cadena.charAt(i2) != '\"') {
                        i2++;
                    }
                    if (i2 > i1) {
                        elementos.add(cadena.substring(i1, i2));
                    }
                    i1 = i2 + 1;
                    i2 = i1;
                    break;
                case ' ':
                    i1++;
                    break;
                default:
                    i2 = i1;
                    do {
                        i2++;
                        b = cadena.charAt(i2);
                    } while (b != ' ' && b != '(' && b != ')' && b != '\"');
                    elementos.add(cadena.substring(i1, i2));
                    i1 = i2;
                    break;
            }
        }
        return elementos;
    }

    private void createNodeFromExpre(String rootString) throws OperatorException {
        Node tmp = null;
        switch (rootString) {
            case "AND":
                tmp = new ANDNode();              
                break;
            case "OR":
                tmp = new ORNode();
                break;
            case "EQV":
                tmp = new EQVNode();
                break;
            case "IMP":
                tmp = new IMPNode();
                break;
            case "NOT":
                tmp = new NOTNode();

                break;
            case "OPERATOR":
                for (GeneratorNode generator : generators) {
                    if (generator.getLabel().equals(rootString)) {
                        tmp = generator;
                        break;
                    }
                }

                break;
            default:

                for (int i = 0; i < states.size(); i++) {
                    if (states.get(i).getLabel().equals(rootString)) {
                        tmp = new StateNode(states.get(i));
                        break;
                    }
                }
                for (GeneratorNode generator : generators) {
                    if (generator.getLabel().equals(rootString)) {
                        tmp = generator;
                        break;
                    }
                }
                if (tmp == null) {
                    throw new OperatorException("Not found: " + rootString);
                }
                break;
        }

        predicate.addNode(currentNodeRoot, tmp);
        if (tmp instanceof OperatorNode) {
            currentNodeRoot = tmp;
        }

    }
    /**
     * @return the generators
     */
    public List<GeneratorNode> getGenerators() {
        return generators;
    }
    /**
     * @return the states
     */
    public List<StateNode> getStates() {
        return states;
    }
}
