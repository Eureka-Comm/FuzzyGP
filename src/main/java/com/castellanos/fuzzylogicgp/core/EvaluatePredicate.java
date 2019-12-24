/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.core;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.castellanos.fuzzylogicgp.base.GeneratorNode;
import com.castellanos.fuzzylogicgp.base.IMPNode;
import com.castellanos.fuzzylogicgp.base.Node;
import com.castellanos.fuzzylogicgp.base.NodeType;
import com.castellanos.fuzzylogicgp.base.OperatorException;
import com.castellanos.fuzzylogicgp.base.Predicate;
import com.castellanos.fuzzylogicgp.base.StateNode;
import com.castellanos.fuzzylogicgp.logic.ALogic;
import com.castellanos.fuzzylogicgp.logic.GMBC;
import com.castellanos.fuzzylogicgp.membershipfunction.FPG;
import com.castellanos.fuzzylogicgp.parser.ParserPredicate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

/**
 *
 * @author Castellanos Alvarez, Alejadro
 * @version 1.0.0
 */
public class EvaluatePredicate {

    private Predicate p;
    private ALogic logic;
    private Table data;
    private Table fuzzyData;
    private DoubleColumn resultColumn;
    private String outPath;

    public EvaluatePredicate(ALogic logic, Table data) {
        this.logic = logic;
        this.data = data;
    }

    public EvaluatePredicate(Predicate p, ALogic logic, Table data) {
        this.p = p;
        this.logic = logic;
        this.data = data;
    }

    public EvaluatePredicate(Predicate p, ALogic logic, String path) {
        this.p = p;
        this.logic = logic;
        try {
            this.data = Table.read().csv(path);
        } catch (IOException ex) {
            Logger.getLogger(EvaluatePredicate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public EvaluatePredicate(Predicate p, ALogic logic, String path, String outPath) {
        this.p = p;
        this.logic = logic;
        this.outPath = outPath;
        try {
            this.data = Table.read().csv(path);
        } catch (IOException ex) {
            Logger.getLogger(EvaluatePredicate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public BigDecimal evaluate() {

        dataFuzzy();
        fitCompute();
        if (p.getIdFather() != null && !p.getNode(p.getIdFather()).getType().equals(NodeType.STATE)) {
            StringColumn fa = StringColumn.create("For All");
            List<BigDecimal> rsColumn = new ArrayList<>();
            for (Double x : resultColumn) {
                rsColumn.add(new BigDecimal(x.toString(),MathContext.DECIMAL64));
            }
            BigDecimal forAllValue = logic.forAll(rsColumn);
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
        }
        return BigDecimal.ONE.negate();
    }

    public void exportToCsv() throws IOException {
        fuzzyData.write().csv(outPath);
    }

    public String exportToJSON() {
        Gson print = new GsonBuilder().setPrettyPrinting().create();
        return print.toJson(fuzzyData);
    }

    public void exportToCsv(String outPath) throws IOException {
        fuzzyData.write().csv(outPath);
    }

    public void resultPrint() {
        System.out.println(fuzzyData.toString());
    }

    private void fitCompute() {
        BigDecimal result;
        resultColumn = DoubleColumn.create("result");
        for (int i = 0; i < fuzzyData.rowCount(); i++) {
            try {
                result = fitValue(p.getNode(p.getIdFather()), i);
                resultColumn.append(result.doubleValue());
            } catch (OperatorException ex) {
                Logger.getLogger(EvaluatePredicate.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private BigDecimal fitValue(Node node, int index) throws OperatorException {
        BigDecimal aux = BigDecimal.ONE;
        List<Node> child;
        switch (node.getType()) {
        case AND:
            child = p.searchChilds(node);
            for (int i = 1; i < child.size(); i++) {
                aux = aux.multiply(fitValue(child.get(i), index));
            }
            return logic.and(aux, fitValue(child.get(0), index));
        case OR:
            child = p.searchChilds(node);
            for (int i = 0; i < child.size(); i++) {
                //aux *= (1 - fitValue(child.get(i), index));
                aux = aux.multiply(BigDecimal.ONE.subtract(fitValue(child.get(i), index)));
            }
            // return logic.or(aux, fitValue(child.get(0), index));
            return logic.or(aux, new BigDecimal(child.size()));
        case NOT:
            return logic.not(fitValue(p.searchChilds(node).get(0), index));
        case IMP:
            IMPNode imp = (IMPNode) node;
            return logic.imp(fitValue(p.getNode(imp.getLeftID()), index), fitValue(p.getNode(imp.getRighID()), index));
        case EQV:
            child = p.searchChilds(node);
            return logic.eqv(fitValue(child.get(0), index), fitValue(child.get(1), index));
        case STATE:
            StateNode st = (StateNode) node;
            //return Double.valueOf(fuzzyData.getString(index, st.getLabel()));
            return new BigDecimal(fuzzyData.getString(index, st.getLabel()),MathContext.DECIMAL64);
        default:
            throw new UnsupportedOperationException("Dont supported: " + node.getType() + " : " + node.getId());
        }

    }

    private void dataFuzzy() {
        fuzzyData = Table.create();
        
        p.getNodes().forEach((String k, Node v) -> {
            if (v instanceof StateNode) {
                StateNode s = (StateNode) v;
                if (!fuzzyData.columnNames().contains(s.getColName())) {
                    ColumnType type = data.column(s.getColName()).type();

                    StringColumn dc = StringColumn.create(s.getLabel());

                    if (type == ColumnType.DOUBLE) {
                        Column<Double> column = (Column<Double>) data.column(s.getColName());

                        for (Double cell : column) {
                            dc.append(s.getMembershipFunction().evaluate(new BigDecimal(cell)).toString());
                        }

                    } else if (type == ColumnType.FLOAT) {
                        Column<Float> column = (Column<Float>) data.column(s.getColName());
                        for (Float cell : column) {
                            dc.append(s.getMembershipFunction().evaluate(new BigDecimal(cell)).toString());
                        }

                    } else if (type == ColumnType.INTEGER) {
                        Column<Integer> column = (Column<Integer>) data.column(s.getColName());
                        for (Integer cell : column) {
                            dc.append(s.getMembershipFunction().evaluate(new BigDecimal(cell)).toString());
                        }

                    } else if (type == ColumnType.LONG) {
                        Column<Long> column = (Column<Long>) data.column(s.getColName());
                        for (Long cell : column) {
                            dc.append(s.getMembershipFunction().evaluate(new BigDecimal(cell)).toString());
                        }

                    } else {
                        System.out.println("ColumnType: " + type);
                    }
                    fuzzyData.addColumns(dc);
                }
            }
        });
        // System.out.println(fuzzyData);
    }

    public void setPredicate(Predicate p) {
        this.p = p;
    }

    public static void main(String[] args) throws OperatorException, IOException {

        FPG sfa = new FPG("7.96975201556474", "8.54628394558732", "0.7419780861262811");
        StateNode fa = new StateNode("quality", "quality", sfa);
        FPG fpg = new FPG("9.132248919293149", "12.468564784808557", "0.24484459229131095");
        StateNode ca = new StateNode("fixed_acidity", "fixed_acidity", fpg);
        List<StateNode> states = new ArrayList<>();
        states.add(fa);
        states.add(ca);

        GeneratorNode g = new GeneratorNode("*", new NodeType[] {}, new ArrayList<>());
        List<GeneratorNode> gs = new ArrayList<>();

        String expression = "(IMP (AND \"high alcohol\" \"low pH\") \"high quality\")";
        expression = "(NOT \"quality\" )";
        expression = "(IMP (NOT \"fixed_acidity\") \"quality\")";
        ParserPredicate parser = new ParserPredicate(expression, states, gs);
        Predicate pp = parser.parser();

        EvaluatePredicate ep = new EvaluatePredicate(pp, new GMBC(), "src/main/resources/datasets/tinto.csv",
                "evaluation-result-fpg.csv");
        long startTime = System.nanoTime();
        BigDecimal evaluate = ep.evaluate();
        System.out.println(evaluate);
        long endTime = System.nanoTime();

        long duration = (endTime - startTime); // divide by 1000000 to get milliseconds.
        System.out.println("That took " + (duration / 1000000) + " milliseconds");
        ep.resultPrint();
        // ep.exportToCsv();
        System.out.println(ep.exportToJSON());
    }

}
