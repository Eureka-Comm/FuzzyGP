/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.base;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author hp
 */
public class Predicate {

    private HashMap<String, Node> nodes;
    private String idFather;

    public Predicate() {
        nodes = new HashMap<>();
    }

    public void addAllNode(Node node) {
        addNode(node);
        if (node instanceof Operator) {
            Operator o = (Operator) node;
            o.getChilds().forEach((n) -> {
                addAllNode(n);
            });
        }

    }

    public void addNode(Node node) {
        if (nodes.isEmpty()) {
            idFather = node.getId();
        }
        nodes.put(node.getId(), node);
    }

    public Node remove(Node node) {
        return nodes.remove(node);
    }

    public void replace(Node toReplace, Node newNode) throws OperatorException {
        remove(toReplace);
        Node nodeFather = nodes.get(toReplace.getFather());
        if (nodeFather != null && nodeFather instanceof Operator) {
            Operator op = (Operator) nodeFather;
            newNode.setFather(toReplace.getFather());
            if (op.getType().equals(NodeType.EQV) || op.getType().equals(NodeType.IMP)) {
                Node[] array = (Node[]) op.getChilds().toArray();
                if (array[0].equals(toReplace)) {
                    array[0] = newNode;
                } else {
                    array[1] = newNode;
                }
                op.setChilds(Arrays.asList(array));
            } else {
                op.remove(toReplace);
                op.addChild(newNode);
            }
            addNode(newNode);
        }
    }

    public String toJson() {
        GsonBuilder gson = new GsonBuilder().setPrettyPrinting();
        return gson.create().toJson(this);
    }

    @Override
    public String toString() {
        return nodes.get(idFather).toString();
    }

    public static Predicate fromJson(String st) {
        Gson gson = new Gson();
        return gson.fromJson(st, Predicate.class);
    }

    public Node getNode(String father) {
        return nodes.get(father);
    }

    public void setIdFather(String idFather) {
        this.idFather = idFather;
    }

}
