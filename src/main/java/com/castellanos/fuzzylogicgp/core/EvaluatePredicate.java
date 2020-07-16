/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.castellanos.fuzzylogicgp.base.Node;
import com.castellanos.fuzzylogicgp.base.NodeTree;
import com.castellanos.fuzzylogicgp.base.NodeType;
import com.castellanos.fuzzylogicgp.base.OperatorException;
import com.castellanos.fuzzylogicgp.base.StateNode;
import com.castellanos.fuzzylogicgp.logic.Logic;
import com.castellanos.fuzzylogicgp.membershipfunction.MapNominal_MF;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.Destination;
import tech.tablesaw.io.json.JsonWriteOptions;
import tech.tablesaw.io.json.JsonWriter;
import tech.tablesaw.io.xlsx.XlsxReadOptions;
import tech.tablesaw.io.xlsx.XlsxReader;

/**
 *
 * @author Castellanos Alvarez, Alejadro
 * @version 1.0.0
 */
public class EvaluatePredicate {

    private NodeTree p;
    private Logic logic;
    private Table data;
    private Table fuzzyData;
    private DoubleColumn resultColumn;
    private String outPath;

    public EvaluatePredicate(Logic logic, Table data) {
        this.logic = logic;
        this.data = data;
    }

    public EvaluatePredicate(NodeTree p, Logic logic, Table data) {
        this.p = p;
        this.logic = logic;
        this.data = data;
    }

    public EvaluatePredicate(NodeTree p, Logic logic, String path) {
        this.p = p;
        this.logic = logic;
        try {
            if (path.contains(".csv")) {
                data = Table.read().file(new File(path));
            } else {
                XlsxReader reader = new XlsxReader();
                XlsxReadOptions options = XlsxReadOptions.builder(path).build();
                data = reader.read(options);
            }
        } catch (IOException ex) {
            Logger.getLogger(EvaluatePredicate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public EvaluatePredicate(NodeTree p, Logic logic, String path, String outPath) {
        this.p = p;
        this.logic = logic;
        this.outPath = outPath;
        if (path.contains(".csv")) {
            try {
                data = Table.read().file(new File(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            XlsxReader reader = new XlsxReader();
            XlsxReadOptions options = XlsxReadOptions.builder(path).build();
            try {
                data = reader.read(options);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public double evaluate() {

        dataFuzzy();
        fitCompute();
        // if (p.getIdFather() != null &&
        // !p.getNode(p.getIdFather()).getType().equals(NodeType.STATE)) {
        StringColumn fa = StringColumn.create("For All");
        List<Double> rsColumn = new ArrayList<>();
        for (Double x : resultColumn) {
            rsColumn.add(x);
        }
        double forAllValue = logic.forAll(rsColumn);
        p.setFitness(forAllValue);
        fa.append("" + p.getFitness());

        StringColumn ec = StringColumn.create("Exist");
        ec.append("" + logic.exist(rsColumn));
        for (int i = 1; i < fuzzyData.rowCount(); i++) {
            fa.append("");
            ec.append("");
        }
        fuzzyData.addColumns(fa, ec, resultColumn);
        return forAllValue;
        // }
        // return BigDecimal.ONE.negate();
    }

    public void exportToCsv() throws IOException {
        fuzzyData.write().csv(outPath);
    }

    public void exportToJSON(String file) throws IOException {
        File f = new File(file.replace(".xlsx", ".csv").replace(".xls", ".csv"));
        JsonWriter jsonWriter = new JsonWriter();
        JsonWriteOptions options = JsonWriteOptions.builder(new Destination(new File(f.getAbsolutePath().replace(".csv", ".json")))).header(true).build();
        jsonWriter.write(fuzzyData, options);
        fuzzyData.write().toFile(f);
    }

    public void exportToCsv(String outPath) throws IOException {
        fuzzyData.write().csv(outPath);
    }

    public void resultPrint() {
        System.out.println(fuzzyData.toString());
    }

    private void fitCompute() {
        Double result;
        resultColumn = DoubleColumn.create("result");
        for (int i = 0; i < fuzzyData.rowCount(); i++) {
            try {
                // result = fitValue(p.getNode(p.getIdFather()), i);
                result = fitValue(p, i);
                resultColumn.append(result.doubleValue());
            } catch (OperatorException ex) {
                Logger.getLogger(EvaluatePredicate.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private double fitValue(Node node, int index) throws OperatorException {
        List<Node> child;
        NodeTree nodeTree;
        ArrayList<Double> values;
        switch (node.getType()) {
            case AND:
                nodeTree = (NodeTree) node;
                child = nodeTree.getChildrens();
                values = new ArrayList<>();
                for (int i = 0; i < child.size(); i++) {
                    values.add(fitValue(child.get(i), index));
                }

                nodeTree.setFitness(logic.and(values));
                return nodeTree.getFitness();
            case OR:
                nodeTree = (NodeTree) node;
                child = nodeTree.getChildrens();
                /*
                 * for (int i = 0; i < child.size(); i++) { aux *= (1 - fitValue(child.get(i),
                 * index)); // aux = aux.multiply(BigDecimal.ONE.subtract(fitValue(child.get(i),
                 * index))); }
                 */
                values = new ArrayList<>();
                for (int i = 0; i < child.size(); i++) {
                    values.add(fitValue(child.get(i), index));
                }
                nodeTree.setFitness(logic.or(values));
                return nodeTree.getFitness();
            // return logic.or(aux, new BigDecimal(child.size()));
            case NOT:
                // return logic.not(fitValue(((NodeTree) p).getChildrens().get(0), index));
                nodeTree = (NodeTree) node;
                child = nodeTree.getChildrens();
                nodeTree.setFitness(logic.not(fitValue(child.get(0), index)));
                return nodeTree.getFitness();
            case IMP:
                nodeTree = (NodeTree) node;
                NodeTree imp = (NodeTree) node;
                nodeTree.setFitness(logic.imp(fitValue(imp.findById(imp.getLeftID()), index),
                        fitValue(imp.findById(imp.getRighID()), index)));
                return nodeTree.getFitness();
            case EQV:
                nodeTree = (NodeTree) node;
                child = nodeTree.getChildrens();
                nodeTree.setFitness(logic.eqv(fitValue(child.get(0), index), fitValue(child.get(1), index)));
                return nodeTree.getFitness();
            case STATE:
                StateNode st = (StateNode) node;
                return Double.valueOf(fuzzyData.getString(index, st.getLabel()));
            // return new BigDecimal(fuzzyData.getString(index,
            // st.getLabel()),MathContext.DECIMAL64);
            case OPERATOR:
                nodeTree = (NodeTree) node;
                return fitValue(nodeTree.getChildrens().get(0), index);
            default:
                throw new UnsupportedOperationException("Dont supported: " + node.getType() + " : " + node.getId());
        }

    }

    private void dataFuzzy() {
        fuzzyData = Table.create();
        ArrayList<Node> nodes = NodeTree.getNodesByType(p, NodeType.STATE);
        for (Node v : nodes) {
            StateNode s = (StateNode) v;
            if (!fuzzyData.columnNames().contains(s.getLabel())) {
                ColumnType type = data.column(s.getColName()).type();

                DoubleColumn dc = DoubleColumn.create(s.getLabel());

                if (type == ColumnType.DOUBLE) {
                    Column<Double> column = (Column<Double>) data.column(s.getColName());
                    for (Double cell : column) {
                        dc.append(s.getMembershipFunction().evaluate((cell)));
                    }

                } else if (type == ColumnType.FLOAT) {
                    Column<Float> column = (Column<Float>) data.column(s.getColName());
                    for (Float cell : column) {
                        dc.append(s.getMembershipFunction().evaluate((cell)));
                    }

                } else if (type == ColumnType.INTEGER) {
                    Column<Integer> column = (Column<Integer>) data.column(s.getColName());
                    for (Integer cell : column) {
                        dc.append(s.getMembershipFunction().evaluate((cell)));
                    }

                } else if (type == ColumnType.LONG) {
                    Column<Long> column = (Column<Long>) data.column(s.getColName());
                    for (Long cell : column) {
                        dc.append(s.getMembershipFunction().evaluate((cell)));
                    }
                } else if (type == ColumnType.STRING) {
                    Column<String> column = (Column<String>) data.column(s.getColName());                    
                    for (String valueString : column) {
                        Double value = s.getMembershipFunction().evaluate(valueString);
                        dc.append(value);
                    }

                } else {
                    System.out.println("ColumnType: " + type);
                }
                fuzzyData.addColumns(dc);
            }
        }
        // System.out.println(fuzzyData);
    }

    public void setPredicate(NodeTree p) {
        this.p = p;
    }
    /**
     * 
     * @return fuzzy data table of the evaluated predicate
     */
    public Table getFuzzyData() {
        return fuzzyData;
    }
    /**
     * 
     * @return the evaluated predicated
     */
    public NodeTree getPredicate() {
        return p;
    }
    

}
