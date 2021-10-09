/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos94.fuzzylogicgp.parser;

import com.castellanos94.fuzzylogicgp.core.DummyGenerator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import com.castellanos94.fuzzylogicgp.core.Node;
import com.castellanos94.fuzzylogicgp.core.NodeTree;
import com.castellanos94.fuzzylogicgp.core.NodeType;
import com.castellanos94.fuzzylogicgp.core.OperatorException;
import com.castellanos94.fuzzylogicgp.core.StateNode;

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
    private final List<DummyGenerator> generators;
    private final ArrayList<Node> nodes;

    public ParserPredicate(String expression, List<StateNode> states, List<DummyGenerator> gs) {
        this.expression = expression;
        this.states = states;
        this.generators = gs;
        stack = new Stack<>();
        this.nodes = new ArrayList<>();
        nodes.addAll(states);
        nodes.addAll(gs);
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
                            NodeTree tmp = NodeTree.getNodeParent(predicate, currentNodeRoot.getId());
                            if(tmp!=null){
                                currentNodeRoot = tmp;
                            }
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

    private void createNodeFromExpre(String rootString) throws OperatorException {
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
        
            default:

                for (int i = 0; i < states.size(); i++) {
                    if (states.get(i).getLabel().equals(rootString)) {
                        tmp = (Node) states.get(i).copy();
                        break;
                    }
                }
                for (DummyGenerator generator : generators) {
                    if (generator.getLabel().equals(rootString)) {
                        tmp = generator.toGeneratorNode(nodes);
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
    public List<DummyGenerator> getGenerators() {
        return generators;
    }

    /**
     * @return the states
     */
    public List<StateNode> getStates() {
        return states;
    }
    public static void main(String[] args) throws OperatorException, CloneNotSupportedException {
        String expression = "(\"calidad\")";
        NodeType[] operators = { NodeType.AND, NodeType.OR, NodeType.NOT };
        ArrayList<StateNode> states = new ArrayList<>();
        states.add(new StateNode("a", "alcohol"));
        states.add(new StateNode("c", "chlorides"));
        states.add(new StateNode("q", "quality"));
        states.add(new StateNode("fa", "fixed_acidity"));

        ArrayList<String> variables = new ArrayList<>();
        states.stream().map(StateNode::getLabel).forEach(variables::add);
        DummyGenerator generator = new DummyGenerator("calidad", operators, variables, 2);
        ArrayList<DummyGenerator> gs = new ArrayList<>();
        gs.add(generator);
        ParserPredicate parserPredicate = new ParserPredicate(expression, states, gs);
        NodeTree predicate = parserPredicate.parser();
        System.out.println(predicate);
    }
}
