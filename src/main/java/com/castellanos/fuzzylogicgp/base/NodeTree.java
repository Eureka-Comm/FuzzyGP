package com.castellanos.fuzzylogicgp.base;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.GsonBuilder;

public class NodeTree extends Node implements Comparable<NodeTree> {
    /**
     *
     */
    private static final long serialVersionUID = 7984595590989290929L;
    private Double fitness;
    private ArrayList<Node> childrens;
    private String leftID;
    private String righID;

    public NodeTree() {
        setType(NodeType.OPERATOR);
        this.childrens = new ArrayList<>();
    }

    public NodeTree(NodeType type) throws OperatorException {
        this.childrens = new ArrayList<>();
        switch (type) {
            case AND:
                setType(type);
                break;
            case OR:
                setType(type);
                break;
            case IMP:
                setType(type);
                break;
            case EQV:
                setType(type);
                break;
            case NOT:
                setType(type);
                break;
            default:
                throw new OperatorException(this.getId() + " " + this.getType() + ": illegal assigment.");

        }
    }

    /**
     * @param fitness the fitness to set
     */
    public void setFitness(Double fitness) {
        this.fitness = fitness;
    }

    /**
     * @return the childrens
     */
    public ArrayList<Node> getChildrens() {
        return childrens;
    }

    public void addChild(Node node) throws OperatorException {
        switch (this.getType()) {
            case AND:
            case OR:
                this.childrens.add(node);
                break;
            case IMP:
            case EQV:
                if (this.childrens.isEmpty()) {
                    this.leftID = node.getId();
                    this.childrens.add(node);
                } else if (childrens.size() == 1) {
                    this.righID = node.getId();
                    this.childrens.add(node);
                } else {
                    throw new OperatorException(this.getId() + " " + this.getType() + ": arity must be two element.");
                }
                break;
            case NOT:
                if (childrens.isEmpty()) {
                    this.childrens.add(node);
                    break;
                } else
                    throw new OperatorException(this.getId() + " " + this.getType() + ": arity must be one element.");
            case OPERATOR:
                this.childrens.add(node);
                break;
            default:
                throw new OperatorException(this.getId() + " " + this.getType() + ": arity must be ? element.");
        }
    }

    /**
     * @return the leftID
     */
    public String getLeftID() {
        return leftID;
    }

    /**
     * @return the righID
     */
    public String getRighID() {
        return righID;
    }

    /**
     * @param leftID the leftID to set
     */
    public void setLeftID(String leftID) {
        this.leftID = leftID;
    }

    /**
     * @param righID the righID to set
     */
    public void setRighID(String righID) {
        this.righID = righID;
    }

    /**
     * @return the fitness
     */
    public Double getFitness() {
        return fitness;
    }

    @Override
    public String toString() {
        if (childrens.isEmpty()) {
            return String.format("(%s)", this.getType());
        }
        String st = makePrintStruct(this);
        /*
         * for (Node node : childrens) { if(node instanceof NodeTree){
         * st+=" "+makePrintStruct((NodeTree) node); }else if( node instanceof
         * StateNode){ st += String.format(" \"%s\"", ((StateNode) node).getLabel() );
         * }else if(node instanceof GeneratorNode){ st += String.format(" \"%s\"",
         * ((GeneratorNode)node).getLabel()); } }
         */
        return st;
    }

    public Node findById(String id) {
        for (Node node : childrens) {
            if (node.getId().equals(id))
                return node;

            if (node instanceof NodeTree) {
                Node find = ((NodeTree) node).findById(id);
                if (find != null) {
                    return find;
                }
            }

        }
        return null;
    }

    private String makePrintStruct(Node node) {
        String st = "";
        if (node instanceof NodeTree) {
            NodeTree nodeTree = (NodeTree) node;
            if (nodeTree.getChildrens().isEmpty()) {
                return "(" + nodeTree.getType() + ")";
            } else {
                st += "(" + nodeTree.getType();
                if (nodeTree.getType() == NodeType.IMP || nodeTree.getType() == NodeType.EQV) {
                    if (nodeTree.getLeftID() != null) {
                        st += " " + makePrintStruct(nodeTree.findById(nodeTree.getLeftID()));
                    }
                    if (nodeTree.getRighID() != null) {
                        st += " " + makePrintStruct(nodeTree.findById(nodeTree.getRighID()));
                    }
                } else {
                    for (Node chilNode : nodeTree.getChildrens()) {
                        st += " " + makePrintStruct(chilNode);
                    }
                }
            }
        } else if (node instanceof StateNode) {
            st += String.format(" \"%s\"", ((StateNode) node).getLabel());
            return st;
        } else if (node instanceof GeneratorNode) {
            st += String.format(" \"%s\"", ((GeneratorNode) node).getLabel());
            return st;
        }

        return st + ")";
    }

    public boolean isValid() {
        return true;
    }

    public static NodeTree getNodeParent(NodeTree root, String idChild) {
        if (idChild == null) {
            return null;
        }
        for (Node node : root.getChildrens()) {
            if (node.getId().equals(idChild)) {
                return root;
            } else if (node instanceof NodeTree) {
                NodeTree parent = getNodeParent((NodeTree) node, idChild);
                if (parent != null) {
                    return parent;
                }
            }
        }
        return null;
    }

    public static ArrayList<Node> getNodesByType(NodeTree tree, NodeType type) {
        ArrayList<Node> nodes = new ArrayList<>();
        getNodesByType(tree, nodes, type);
        return nodes;
    }

    public static ArrayList<Node> getEditableNodes(NodeTree tree) {
        ArrayList<Node> nodes = new ArrayList<>();
        getNodesByType(tree, nodes, null);
        nodes.removeIf(node -> !node.isEditable());
        return nodes;
    }

    public static void getNodesByType(NodeTree tree, ArrayList<Node> nodes, NodeType type) {
        for (Node n : tree.getChildrens()) {
            if (type == null) {
                nodes.add(n);
            } else if (n.getType().equals(type)) {
                nodes.add(n);
            }
            if (n instanceof NodeTree) {
                getNodesByType((NodeTree) n, nodes, type);
            }
        }
    }

    public String toJson() {
        GsonBuilder gson = new GsonBuilder().setPrettyPrinting();
        return gson.create().toJson(this);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(this);

            oos.flush();
            oos.close();
            bos.close();
            byte[] byteData = bos.toByteArray();
            ByteArrayInputStream bais = new ByteArrayInputStream(byteData);
            Object object = (Object) new ObjectInputStream(bais).readObject();
            return object;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static int dfs(NodeTree root, Node node) {
        return dfs(root, node, 1);
    }

    private static int dfs(NodeTree root, Node node, int pos) {
        if (node.getId().equals(node.getId())) {
            return pos;
        }
        for (Node n : root.getChildrens()) {
            if (n.getId().equals(node.getId())) {
                return pos + 1;
            } else if (n instanceof GeneratorNode) {
                int position = dfs((NodeTree) n, node, pos);
                if (position != -1) {
                    return position;
                }
            }
        }
        return -1;
    }

    public static void replace(NodeTree nodeTree, Node toReplace, Node newNode) throws OperatorException {
        int pos = -1;
        for (int i = 0; i < nodeTree.getChildrens().size(); i++) {
            if (nodeTree.getChildrens().get(i).getId().equals(toReplace.getId())) {
                pos = i;
                break;
            }
        }
        if (pos != -1) {
            nodeTree.getChildrens().set(pos, newNode);
            if (nodeTree.getType() == NodeType.EQV || nodeTree.getType() == NodeType.IMP) {
                if (nodeTree.getLeftID().equals(toReplace.getId())) {
                    nodeTree.setLeftID(newNode.getId());
                } else if (nodeTree.getRighID().equals(toReplace.getId())) {
                    nodeTree.setRighID(newNode.getId());
                }
            }
        } else {
            throw new OperatorException(nodeTree.getId() + " is not the parent of " + toReplace.getId());
        }
    }

    @Override
    public int compareTo(NodeTree tree) {
        return this.fitness.compareTo(tree.getFitness());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((childrens == null) ? 0 : childrens.hashCode());
        result = prime * result + ((fitness == null) ? 0 : fitness.hashCode());
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
        NodeTree other = (NodeTree) obj;
        if (childrens == null) {
            if (other.childrens != null)
                return false;
        } else if (!this.toString().equals(other.toString()) && fitness != null && other.fitness != null && !fitness.equals(other.fitness))
            return false;
        return true;
    }

}