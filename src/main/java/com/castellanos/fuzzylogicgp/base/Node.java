/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.base;

import java.util.UUID;

/**
 *
 * @author hp
 */
public abstract class Node implements Cloneable {
    private final String id = UUID.randomUUID().toString();
    private String father;
    private NodeType type;
    private boolean editable;
    private String byGenerator;

    public Node() {
    }

    public NodeType getType() {
        return type;
    }

    public void setType(NodeType type) {
        this.type = type;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String getId() {
        return id;
    }

    public String getFather() {
        return father;
    }

    public void setFather(String father) {
        this.father = father;
    }

    /**
     * @return the byGenerator
     */
    public String getByGenerator() {
        return byGenerator;
    }

    /**
     * @param byGenerator the byGenerator to set
     */
    public void setByGenerator(String byGenerator) {
        this.byGenerator = byGenerator;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Node node = null;
        node = (Node) super.clone();
        return node;
    }

}
