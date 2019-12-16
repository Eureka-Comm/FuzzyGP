/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *
 * @author hp
 */
public class Predicate {

    private ConcurrentHashMap<String, Node> nodes;
    private String idFather;
    private Double fitness;

    public Predicate(Predicate p) {
        this.nodes = new ConcurrentHashMap<>();
        p.getNodes().forEach((k, v) -> {
            try {
                nodes.put(k, (Node) v.clone());
            } catch (CloneNotSupportedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
        this.idFather = p.getIdFather();
    }
    public Predicate() {
        nodes = new ConcurrentHashMap<>();
    }

    public void addNode(Node father, Node node) throws OperatorException {
        if (father == null || nodes.isEmpty()) {
            idFather = node.getId();
            nodes.put(node.getId(), node);
            return;
        }
        if(father instanceof GeneratorNode){
            this.replace(father, node);
            return;
        }
        List<Node> childs;

        switch (father.getType()) {
            case IMP:
            case EQV:
                childs = searchChilds(father);
                if (childs.size() < 2) {
                    node.setFather(father.getId());
                    if (father.getType().equals(NodeType.IMP)) {
                        IMPNode impn = (IMPNode) father;
                        if (impn.getLeftID() == null) {
                            impn.setLeftID(node.getId());
                        } else if (impn.getRighID() == null) {
                            impn.setRighID(node.getId());
                        }
                    }
                    nodes.put(node.getId(), node);
                } else {
                    throw new OperatorException("\n" + father.getId() + " " + father.getType() + ": arity must be two elements.");
                }
                break;
            case NOT:
                childs = searchChilds(father);
                if (childs.isEmpty()) {
                    node.setFather(father.getId());
                    nodes.put(node.getId(), node);
                } else {
                    System.out.println(father);
                    System.out.println(node);
                    System.out.println("childs: "+childs);
                    throw new OperatorException(father.getId() + " " + father.getType() + ": arity must be one element.");
                }
                break;
            default:
                node.setFather(father.getId());
                nodes.put(node.getId(), node);
                break;
        }
    }
    @Deprecated
    public Node remove(Node node) {
        List<Node> searchChilds = searchChilds(node);
        for (Node node2 : searchChilds) {
            node2.setFather(null);
        }
        node.setFather(null);
        return nodes.remove(node);
    }

    public void replace(Node toReplace, Node newNode) throws OperatorException {
       // remove(toReplace);
        List<Node> searchChilds = searchChilds(toReplace);
        for (Node node2 : searchChilds) {
            node2.setFather(null);
        }

        Node nodeFather = null;
        if(toReplace.getFather() != null)
            nodeFather = nodes.get(toReplace.getFather());
        if (nodeFather != null && nodeFather instanceof OperatorNode) {
            OperatorNode op = (OperatorNode) nodeFather;
            newNode.setFather(toReplace.getFather());
            if (op.getType().equals(NodeType.EQV) || op.getType().equals(NodeType.IMP)) {
                Node[] array = findChild(nodeFather);                
                if ( array[0] != null && array[0].getId().equals(toReplace.getId())) {
                    array[0] = newNode;
                } else {
                    array[1] = newNode;
                }
                if(op.getType().equals(NodeType.IMP)){
                    IMPNode impNode = (IMPNode) op;
                    impNode.setLeftID(null);
                    impNode.setRighID(null);
                    
                }
                toReplace.setFather(null);
                for (Node node : array) {
                    node.setFather(null);
                }
                addNode(nodeFather, array[0]);
                addNode(nodeFather, array[1]);
            }else{
                toReplace.setFather(null);
                addNode(nodeFather, newNode);
            }
        }else{
            toReplace.setFather(null);
            addNode(nodeFather, newNode);
        }
    }
    public void update(Node toReplace, Node newNode) throws OperatorException {
        if(toReplace!=null ){
            List<Node> kids = searchChilds(toReplace);
            for (Node node : kids) {
                node.setFather(newNode.getId());
            }
        }
        if(toReplace instanceof GeneratorNode){
            toReplace.setFather(null);
        }
        Node nodeFather = nodes.get(toReplace.getFather());
        if (nodeFather != null && nodeFather instanceof OperatorNode) {
            OperatorNode op = (OperatorNode) nodeFather;
            newNode.setFather(toReplace.getFather());
            if (op.getType().equals(NodeType.EQV) || op.getType().equals(NodeType.IMP)) {
                Node[] array = (Node[]) searchChilds(nodeFather).toArray();

                if (array[0].getId().equals(toReplace.getId())) {
                    array[0] = newNode;
                } else {
                    array[1] = newNode;
                }

                addNode(nodeFather, array[0]);
                addNode(nodeFather, array[1]);
            } else {

            }
            addNode(nodeFather, newNode);
        }
    }
    public String toJson() {
        GsonBuilder gson = new GsonBuilder().setPrettyPrinting();
        return gson.create().toJson(this);
    }

    @Override
    public String toString() {

        Node father = nodes.get(idFather);
        if (father instanceof StateNode) {
            return "\"" + ((StateNode) father).getLabel() + "\"";
        } else if (father instanceof GeneratorNode) {
            return "\"" + ((GeneratorNode) father).getLabel() + "\"";
        }
        return makePrintTreeStruct(father);
    }

    public String makePrintTreeStruct(Node father) {

        String st = "(";
        if (father instanceof OperatorNode) {

            st += father.getType();
        }
        if (father.getType().equals(NodeType.IMP)) {
            IMPNode impn = (IMPNode) father;
            Node ln = nodes.get(impn.getLeftID()), rn = nodes.get(impn.getRighID());
            if (ln instanceof OperatorNode) {
                st += " " + makePrintTreeStruct(ln);
            } else if (ln instanceof GeneratorNode) {
                st += " \"" + ((GeneratorNode) ln).getLabel() + "\"";
            } else if (ln instanceof StateNode) {
                st += " \"" + ((StateNode) ln).getLabel() + "\"";
            }
            if (rn instanceof OperatorNode) {
                st += " " + makePrintTreeStruct(rn);
            } else if (rn instanceof GeneratorNode) {
                st += " \"" + ((GeneratorNode) rn).getLabel() + "\"";
            } else if (rn instanceof StateNode) {
                st += " \"" + ((StateNode) rn).getLabel() + "\"";
            }
        } else {
            List<Node> searchChilds = searchChilds(father);
            for (Node n : searchChilds) {
                if (n instanceof OperatorNode) {
                    st += " " + makePrintTreeStruct(n);
                } else if (n instanceof GeneratorNode) {
                    st += " \"" + ((GeneratorNode) n).getLabel() + "\"";
                } else if (n instanceof StateNode) {
                    st += " \"" + ((StateNode) n).getLabel() + "\"";
                }
            }
        }
        return st.trim() + ")";
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
    public Node[] findChild(Node father){
        Node[] childs = new Node[2];
        if(father.getType().equals(NodeType.IMP)){
            IMPNode mImpNode = (IMPNode) father;
            if(mImpNode.getLeftID() != null)
                childs[0] = nodes.get(mImpNode.getLeftID());
            if(mImpNode.getRighID() !=null)
                childs[1] = nodes.get(mImpNode.getRighID());
        }
        return childs;
    }

    public List<Node> searchChilds(Node father) {
        List<Node> childs = new ArrayList<>();
        
        nodes.forEach((k, v) -> {
            if (v.getFather() != null && v.getFather().equals(father.getId())) {
                childs.add(v);
            }
        });
        return childs;
    }

    public boolean isValid() throws OperatorException {
        for (Node v : nodes.values()) {
            if (v instanceof OperatorNode) {
                List<Node> childs = searchChilds(v);
                switch (v.getType()) {
                    case AND:
                    case OR:
                        if (childs.size() < 2) {
                            throw new OperatorException(v.getId() + " " + v.getType() + ": arity must be more that two elements.");
                        }
                        break;
                    case EQV:
                    case IMP:
                        if (childs.size() != 2) {
                            throw new OperatorException(v.getId() + " " + v.getType() + ": arity must be two elements.");
                        }
                        break;
                    case NOT:
                        if (childs.size() != 1) {
                            throw new OperatorException(v.getId() + " " + v.getType() + ": arity must be one element.");
                        }
                        break;

                }
            }
        }
        return true;
    }
    public int dfs(Node node){
       return dfs(node,0);
    }
    private int dfs(Node node, int pos){
        if(node.getFather() == null  )
            return pos;
        return dfs(this.nodes.get(node.getFather()),pos+1);

    }
    public Double getFitness() {
        return fitness;
    }

    public void setFitness(Double fitness) {
        this.fitness = fitness;
    }

    public ConcurrentHashMap<String, Node> getNodes() {
        return nodes;
    }

    public String getIdFather() {
        return idFather;
    }
    

}
