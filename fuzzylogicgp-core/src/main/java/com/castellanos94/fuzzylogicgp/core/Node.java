/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos94.fuzzylogicgp.core;

import java.io.Serializable;
import java.util.UUID;

import com.google.gson.annotations.Expose;

/**
 *
 * @author hp
 */
public abstract class Node implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 2377425499330019252L;
    protected final String id;
    @Expose
    private NodeType type;
    @Expose
    private boolean editable;
    @Expose
    protected String byGenerator;
    @Expose
    protected String label;
    @Expose
    protected String description = "";
    
    public Node() {
        id = UUID.randomUUID().toString();
    }

    protected Node(String id){
        this.id = id;
    }

    abstract Node withId(String id);

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public abstract Object copy();

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Node other = (Node) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
