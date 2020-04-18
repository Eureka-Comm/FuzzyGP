/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.parser;

import com.castellanos.fuzzylogicgp.base.GeneratorNode;
import com.castellanos.fuzzylogicgp.base.Node;
import com.castellanos.fuzzylogicgp.base.NodeTree;
import com.castellanos.fuzzylogicgp.base.NodeType;
import com.castellanos.fuzzylogicgp.base.OperatorException;
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
    private NodeTree predicate;
    private final List<StateNode> states;
    private final List<GeneratorNode> generators;

    public ParserPredicate(String expression, List<StateNode> states, List<GeneratorNode> gs) {
        this.expression = expression;
        this.states = states;
        this.generators = gs;
        stack = new Stack<>();
    }

    public NodeTree parser() throws OperatorException, CloneNotSupportedException {
        List<String> split = expressionSplit(expression);
        // predicate = new Predicate();
        predicate = null;
        if (isBalanced(split)) {
            Iterator<String> stringIterator = split.iterator();
            String rootString;

            while (stringIterator.hasNext()) {
                rootString = stringIterator.next();
                switch (rootString) {
                    case "(":
                        break;
                    case ")":
                        if (currentNodeRoot != null) {
                            currentNodeRoot = predicate.getNodeParent(predicate, currentNodeRoot.getId());
                        }
                        break;
                    default:
                        createNodeFromExpre(rootString);
                        break;
                }

            }
            if (predicate == null && currentNodeRoot != null) {
                NodeTree p = new NodeTree();
                p.addChild(currentNodeRoot);
                return p;
            }
            return (predicate.isValid()) ? predicate : null;

        }
        throw new UnsupportedOperationException("Missing ')'"); // To change body of generated methods, choose Tools |
                                                                // Templates.

    }

    private boolean isBalanced(List<String> stringList) {
        for (String s : stringList) { // Recorremos la expresión carácter a carácter
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

    private void createNodeFromExpre(String rootString) throws OperatorException, CloneNotSupportedException {
        Node tmp = null;
        switch (rootString) {
            case "AND":
                tmp = new NodeTree(NodeType.AND);
                break;
            case "OR":
                tmp = new NodeTree(NodeType.OR);
                break;
            case "EQV":
                tmp = new NodeTree(NodeType.EQV);
                break;
            case "IMP":
                tmp = new NodeTree(NodeType.IMP);
                break;
            case "NOT":
                tmp = new NodeTree(NodeType.NOT);

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
                        tmp = (Node) states.get(i).clone();
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

        // predicate.addNode(currentNodeRoot, tmp);
        if (predicate == null && tmp instanceof NodeTree) {
            predicate = (NodeTree) tmp;
        }
        if (currentNodeRoot == null) {
            currentNodeRoot = tmp;
        }
        if (!currentNodeRoot.getId().equals(tmp.getId()) && currentNodeRoot instanceof NodeTree) {
            ((NodeTree) currentNodeRoot).addChild(tmp);
            if (tmp instanceof NodeTree)
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
