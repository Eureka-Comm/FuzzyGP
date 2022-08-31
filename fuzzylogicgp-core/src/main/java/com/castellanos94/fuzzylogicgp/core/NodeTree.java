package com.castellanos94.fuzzylogicgp.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import com.google.gson.GsonBuilder;

public class NodeTree extends Node implements Comparable<NodeTree>, Iterable<Node> {
    /**
     *
     */
    private static final long serialVersionUID = 7984595590989290929L;
    protected Double fitness;
    protected LinkedList<Node> children;
    private String leftID;
    private String righID;

    public NodeTree() {
        super();
        setType(NodeType.OPERATOR);
        this.children = new LinkedList<>();
    }

    protected NodeTree(String id, NodeType type, Double fitness, LinkedList<Node> children, String leftID, String righID){
        super(id);
        setType(type);
        this.fitness = fitness;
        this.children =children;
        this.leftID = leftID;
        this.righID = righID;

    }

    @Override
    NodeTree withId(String id) {
        return new NodeTree(id, getType(), fitness, children, leftID, righID);
    }

    public NodeTree(NodeType type) throws OperatorException {
        this();
        this.children = new LinkedList<>();
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
            case OPERATOR:
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
     * @return the children
     *
     */
    public LinkedList<Node> getChildren() {
        return children;
    }

    /**
     * Added a node to the father
     * 
     * @param node to add
     * @throws OperatorException
     */
    public void addChild(Node node) throws OperatorException {
        Node node_ = node;

        switch (this.getType()) {
            case AND:
            case OR:
                this.children.add(node_);
                break;
            case IMP:
                if (this.children.isEmpty()) {
                    this.leftID = node_.getId();
                    this.children.add(node_);
                } else if (children.size() == 1) {
                    this.righID = node_.getId();
                    this.children.add(node_);
                } else {
                    throw new OperatorException(this.getId() + " " + this.getType() + ": arity must be two element.");
                }
                break;
            case EQV:
                if (this.children.size() < 2) {
                    this.children.add(node_);
                } else {
                    throw new OperatorException(this.getId() + " " + this.getType() + ": arity must be two element.");
                }
                break;
            case NOT:
                if (children.isEmpty()) {
                    this.children.add(node_);
                    break;
                } else
                    throw new OperatorException(this.getId() + " " + this.getType() + ": arity must be one element.");
            case OPERATOR:
                this.children.add(node_);
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
        if (children.isEmpty()) {
            return String.format("(%s)", this.getType());
        }
        String st = makePrintStruct(this);
        return st;
    }

    public Node findById(String id) {
        for (Node node : children) {
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
            if (nodeTree.children.isEmpty()) {
                return " (" + nodeTree.getType() + ")";
            } else {
                st += " (" + ((nodeTree.getType() != NodeType.OPERATOR) ? nodeTree.getType() : "");
                if (nodeTree.getType() == NodeType.IMP) {
                    if (nodeTree.getLeftID() != null) {
                        st += addWhiteSpace(st) + makePrintStruct(nodeTree.findById(nodeTree.getLeftID()));
                    }
                    if (nodeTree.getRighID() != null) {
                        st += addWhiteSpace(st) + makePrintStruct(nodeTree.findById(nodeTree.getRighID()));
                    }
                } else {
                    for (Node chilNode : nodeTree) {
                        st += addWhiteSpace(st) + makePrintStruct(chilNode);
                    }
                }
            }
        } else if (node instanceof StateNode) {
            st += String.format("\"%s\"", ((StateNode) node).getLabel());
            return st;
        } else if (node instanceof GeneratorNode) {
            st += String.format("\"%s\"", ((GeneratorNode) node).getLabel());
            return st;
        }

        return st.trim() + ")";
    }

    private String addWhiteSpace(String st) {
        if (!st.isEmpty()) {
            char c = st.charAt(st.length() - 1);
            if (!Character.isWhitespace(c) && c != '(') {
                return " ";
            }
        }
        return "";
    }

    @Override
    public String getLabel() {
        if (this.label != null && !this.label.trim().isEmpty()) {
            return label;
        } else {
            return this.getType().toString();
        }
    }

    public boolean isValid() {
        boolean rs = isValid(this);
        if (rs) {
            for (Node node : children) {
                if (node instanceof NodeTree) {
                    rs = ((NodeTree) node).isValid();
                    if (!rs) {
                        return rs;
                    }
                }
            }
        }
        return rs;
    }

    private boolean isValid(NodeTree node) {
        switch (node.getType()) {
            case OR:
            case AND:
                return node.children.size() >= 2;
            case IMP:
            case EQV:
                return node.children.size() == 2;
            case NOT:
                return node.children.size() == 1;
            default:
                return true;
        }
    }

    public static NodeTree getNodeParent(NodeTree root, String idChild) {
        if (idChild == null) {
            return null;
        }
        if (root == null)
            return null;
        for (Node node : root) {
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

    public static <T> ArrayList<T> getNodesByType(NodeTree tree, Class<T> clazz) {
        ArrayList<T> nodes = new ArrayList<>();
        getNodesByType(tree, nodes, clazz);
        return nodes;
    }

    public static ArrayList<Node> getEditableNodes(NodeTree tree) {
        ArrayList<Node> nodes = new ArrayList<>();
        getNodesByType(tree, nodes, Node.class);
        Iterator<Node> iterator = nodes.iterator();
        while (iterator.hasNext()) {
            Node _n = iterator.next();
            if (_n.isEditable() == false && _n.getByGenerator() == null) {
                iterator.remove();
            }
        }
        return nodes;
    }

    private static <T> void getNodesByType(NodeTree tree, ArrayList<T> nodes, Class<T> clazz) {
        for (Node n : tree) {
            if (clazz.isInstance(n)) {
                nodes.add(clazz.cast(n));
            }
            if (n instanceof NodeTree) {
                getNodesByType((NodeTree) n, nodes, clazz);
            }
        }
    }

    public String toJson() {
        GsonBuilder gson = new GsonBuilder();
        return gson.create().toJson(this);
    }

    @Override
    public NodeTree copy() {
        try {
            NodeTree tree = new NodeTree(getType());
            tree.setEditable(isEditable());
            if (this.getByGenerator() != null)
                tree.setByGenerator(getByGenerator());
            this.children.forEach(n -> {
                try {
                    Node _n = (Node) n.copy();
                    _n.setEditable(n.isEditable());
                    _n.setByGenerator(n.getByGenerator());
                    tree.addChild(_n);
                } catch (OperatorException e) {
                    e.printStackTrace();
                }
            });
            tree.setDescription(description);
            tree.setFitness(fitness);
            return tree;
        } catch (OperatorException e) {
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
        for (Node n : root) {
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

    public static void replace(NodeTree nodeTree, Node toReplace, Node newNode, boolean isUpdateRoot)
            throws OperatorException {

        if (isUpdateRoot) {
            nodeTree.setType(newNode.getType());
            nodeTree.setEditable(newNode.isEditable());
            if (toReplace instanceof GeneratorNode) {
                nodeTree.setByGenerator(toReplace.getId());
            }
            nodeTree.children.clear();
            return;
        }
        int pos = -1;
        for (int i = 0; i < nodeTree.children.size(); i++) {
            if (nodeTree.getChildren().get(i).getId().equals(toReplace.getId())) {
                pos = i;
                break;
            }
        }
        if (pos != -1) {
            nodeTree.getChildren().set(pos, newNode);
            if (nodeTree.getType() == NodeType.IMP) {
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
        result = prime * result + ((children == null) ? 0 : children.hashCode());
        result = prime * result + ((fitness == null) ? 0 : fitness.hashCode());
        result = prime * result + ((leftID == null) ? 0 : leftID.hashCode());
        result = prime * result + ((righID == null) ? 0 : righID.hashCode());
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
        if (children == null) {
            if (other.children != null)
                return false;
        } else if (!children.equals(other.children))
            return false;
        if (fitness == null) {
            if (other.fitness != null)
                return false;
        } else if (!fitness.equals(other.fitness))
            return false;
        if (leftID == null) {
            if (other.leftID != null)
                return false;
        } else if (!leftID.equals(other.leftID))
            return false;
        if (righID == null) {
            if (other.righID != null)
                return false;
        } else if (!righID.equals(other.righID))
            return false;
        return true;
    }

    @Override
    public Iterator<Node> iterator() {
        return this.children.iterator();
    }

}