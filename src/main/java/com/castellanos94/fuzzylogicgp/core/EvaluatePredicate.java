/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos94.fuzzylogicgp.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.castellanos94.fuzzylogicgp.base.Node;
import com.castellanos94.fuzzylogicgp.base.NodeTree;
import com.castellanos94.fuzzylogicgp.base.NodeType;
import com.castellanos94.fuzzylogicgp.base.OperatorException;
import com.castellanos94.fuzzylogicgp.base.StateNode;
import com.castellanos94.fuzzylogicgp.logic.Logic;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;
import tech.tablesaw.io.Destination;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.io.csv.CsvWriteOptions;
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
    private static final Logger logger = LogManager.getLogger(EvaluatePredicate.class);
    private NodeTree predicate;
    private Logic logic;
    private Table data;
    private Table fuzzyData;
    private DoubleColumn resultColumn;
    private String outPath;

    /**
     * Default constructor
     * 
     * @param logic
     */
    public EvaluatePredicate(Logic logic) {
        this.logic = logic;
    }

    /**
     * Constructor whit dataset
     * 
     * @param logic to evaluate
     */
    public EvaluatePredicate(Logic logic, Table data) {
        this(logic);
        this.data = data;
    }

    public EvaluatePredicate(NodeTree predicate, Logic logic, String path, String outPath) {
        this.predicate = predicate;
        this.logic = logic;
        this.outPath = outPath;
        if (path.contains(".csv")) {
            try {
                CsvReadOptions build = CsvReadOptions.builder(new File(path)).header(true).locale(Locale.US).build();
                data = Table.read().csv(build);
            } catch (IOException e) {
                logger.error("CSV reader:", e);
            }
        } else {
            XlsxReader reader = new XlsxReader();
            XlsxReadOptions options = XlsxReadOptions.builder(path).locale(Locale.US).build();
            try {
                data = reader.read(options);
            } catch (IOException e) {
                logger.error("Xlsx reader:", e);
            }
        }

    }

    public double evaluate(NodeTree predicate) {
        this.predicate = predicate;
        return evaluate();
    }

    public double evaluate() {
        dataFuzzy();
        fitCompute();
        StringColumn fa = StringColumn.create("For All");
        List<Double> rsColumn = new ArrayList<>();
        for (Double x : resultColumn) {
            rsColumn.add(x);
        }
        double forAllValue = logic.forAll(rsColumn);
        predicate.setFitness(forAllValue);
        fa.append("" + predicate.getFitness());

        StringColumn ec = StringColumn.create("Exist");
        ec.append("" + logic.exist(rsColumn));
        for (int i = 1; i < fuzzyData.rowCount(); i++) {
            fa.append("");
            ec.append("");
        }
        fuzzyData.addColumns(fa, ec, resultColumn);
        return forAllValue;
    }

    public void exportToCsv() throws IOException {
        fuzzyData.write().csv(CsvWriteOptions.builder(outPath.replace(".xlsx", ".csv").replace(".xls", ".csv"))
                .header(true).separator(',').quoteChar('"').build());
    }

    public void exportToJSON(String file) throws IOException {
        File f = new File(file.replace(".xlsx", ".csv").replace(".xls", ".csv"));
        JsonWriter jsonWriter = new JsonWriter();
        JsonWriteOptions options = JsonWriteOptions
                .builder(new Destination(new File(f.getAbsolutePath().replace(".csv", ".json")))).header(true).build();
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
                result = fitValue(predicate, i);
                resultColumn.append(result.doubleValue());
            } catch (OperatorException ex) {
                logger.error("Fit compute " + ex);
            }
        }
    }

    private double fitValue(Node node, int index) throws OperatorException {
        NodeTree nodeTree;
        ArrayList<Double> values;
        switch (node.getType()) {
            case AND:
                nodeTree = (NodeTree) node;
                values = new ArrayList<>();
                for (Node child : nodeTree) {
                    values.add(fitValue(child, index));
                }

                nodeTree.setFitness(logic.and(values));
                return nodeTree.getFitness();
            case OR:
                nodeTree = (NodeTree) node;
                values = new ArrayList<>();
                for (Node child : nodeTree) {
                    values.add(fitValue(child, index));
                }
                nodeTree.setFitness(logic.or(values));
                return nodeTree.getFitness();
            case NOT:
                nodeTree = (NodeTree) node;
                nodeTree.setFitness(logic.not(fitValue(nodeTree.iterator().next(), index)));
                return nodeTree.getFitness();
            case IMP:
                nodeTree = (NodeTree) node;
                NodeTree imp = (NodeTree) node;
                nodeTree.setFitness(logic.imp(fitValue(imp.findById(imp.getLeftID()), index),
                        fitValue(imp.findById(imp.getRighID()), index)));
                return nodeTree.getFitness();
            case EQV:
                nodeTree = (NodeTree) node;
                Iterator<Node> iterator = nodeTree.iterator();
                nodeTree.setFitness(logic.eqv(fitValue(iterator.next(), index), fitValue(iterator.next(), index)));
                return nodeTree.getFitness();
            case STATE:
                StateNode st = (StateNode) node;
                return Double.valueOf(fuzzyData.getString(index, st.getLabel()));
            case OPERATOR:
                nodeTree = (NodeTree) node;
                return fitValue(nodeTree.iterator().next(), index);
            default:
                logger.error("Error fit value, invalid node type");
                throw new UnsupportedOperationException("Dont supported: " + node.getType() + " : " + node.getId());
        }

    }

    @SuppressWarnings("unchecked")
    private void dataFuzzy() {
        fuzzyData = Table.create();
        ArrayList<Node> nodes = NodeTree.getNodesByType(predicate, NodeType.STATE);
        for (Node v : nodes) {
            StateNode s = (StateNode) v;
            if (!fuzzyData.columnNames().contains(s.getLabel())) {
                ColumnType type = data.column(s.getColName()).type();
                DoubleColumn dc = DoubleColumn.create(s.getLabel());
                dc.setPrintFormatter(NumberColumnFormatter.standard());
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
                        dc.append(s.getMembershipFunction().evaluate((valueString)));
                    }

                } else {
                    logger.info("ColumnType: " + type);
                }
                fuzzyData.addColumns(dc);
            }
        }
    }

    public void setPredicate(NodeTree predicate) {
        this.predicate = predicate;
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
        return predicate;
    }

}
