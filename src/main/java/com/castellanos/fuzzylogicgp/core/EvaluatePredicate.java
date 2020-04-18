/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.castellanos.fuzzylogicgp.base.GeneratorNode;
import com.castellanos.fuzzylogicgp.base.Node;
import com.castellanos.fuzzylogicgp.base.NodeTree;
import com.castellanos.fuzzylogicgp.base.NodeType;
import com.castellanos.fuzzylogicgp.base.OperatorException;
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

    private NodeTree p;
    private ALogic logic;
    private Table data;
    private Table fuzzyData;
    private DoubleColumn resultColumn;
    private String outPath;

    public EvaluatePredicate(ALogic logic, Table data) {
        this.logic = logic;
        this.data = data;
    }

    public EvaluatePredicate(NodeTree p, ALogic logic, Table data) {
        this.p = p;
        this.logic = logic;
        this.data = data;
    }

    public EvaluatePredicate(NodeTree p, ALogic logic, String path) {
        this.p = p;
        this.logic = logic;
        try {
            this.data = Table.read().csv(path);
        } catch (IOException ex) {
            Logger.getLogger(EvaluatePredicate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public EvaluatePredicate(NodeTree p, ALogic logic, String path, String outPath) {
        this.p = p;
        this.logic = logic;
        this.outPath = outPath;
        try {
            this.data = Table.read().csv(path);
        } catch (IOException ex) {
            Logger.getLogger(EvaluatePredicate.class.getName()).log(Level.SEVERE, null, ex);
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
        Double aux = 1.0;
        List<Node> child;
        NodeTree nodeTree;
        switch (node.getType()) {
            case AND:
                nodeTree = (NodeTree) node;
                child = nodeTree.getChildrens();
                for (int i = 1; i < child.size(); i++) {
                    aux *= (fitValue(child.get(i), index));
                }
                nodeTree.setFitness(logic.and(aux, fitValue(child.get(0), index)));
                return nodeTree.getFitness();
            case OR:
                nodeTree = (NodeTree) node;
                child = nodeTree.getChildrens();
                for (int i = 0; i < child.size(); i++) {
                    aux *= (1 - fitValue(child.get(i), index));
                    // aux = aux.multiply(BigDecimal.ONE.subtract(fitValue(child.get(i), index)));
                }
                nodeTree.setFitness(logic.or(aux, fitValue(child.get(0), index)));
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
                nodeTree.setFitness(logic.imp(fitValue(imp.findById(imp.getLeftID()), index),fitValue(imp.findById(imp.getRighID()), index)));
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
        p.getAllStates().forEach(v -> {
            StateNode s = (StateNode) v;
            if (!fuzzyData.columnNames().contains(s.getColName())) {
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

                } else {
                    System.out.println("ColumnType: " + type);
                }
                fuzzyData.addColumns(dc);
            }
        });
        // System.out.println(fuzzyData);
    }

    public void setPredicate(NodeTree p) {
        this.p = p;
    }

    public static void main(String[] args) throws OperatorException, IOException, CloneNotSupportedException {
        // gamma beta m

        StateNode fa = new StateNode("quality", "quality");
        fa.setMembershipFunction(new FPG("3.008904979613364", "4.911744699566296", "0.587451293429636"));

        FPG fpg = new FPG("9.132248919293149", "12.468564784808557", "0.24484459229131095");
        StateNode ca = new StateNode("fixed_acidity", "fixed_acidity", fpg);
        StateNode sa = new StateNode("alcohol", "alcohol");
        sa.setMembershipFunction(new FPG("9.087011333223336", "8.575778989810821","0.3737520451234506"));
        // beta gamma m
        List<StateNode> states = new ArrayList<>();
        states.add(fa);
        states.add(ca);
        states.add(sa);

        GeneratorNode g = new GeneratorNode("comodin", new NodeType[] {}, new ArrayList<>());
        List<GeneratorNode> gs = new ArrayList<>();
        NodeTree imp = new NodeTree(NodeType.IMP);
        imp.addChild(sa);
        NodeTree and = new NodeTree(NodeType.AND);
        NodeTree not = new NodeTree(NodeType.NOT);
        not.addChild(ca);
        and.addChild(not);
        and.addChild(fa);
        and.addChild(g);
        imp.addChild(and);
        // imp.addChild(new StateNode("label","colname"));
        // System.out.println(imp);
        String expression = "(IMP (AND \"high alcohol\" \"low pH\") \"high quality\")";
        expression = "(NOT \"quality\" )";
       /// expression = "(IMP (NOT \"fixed_acidity\") (AND \"alcohol\" \"quality\"))";
       // expression = "(IMP (AND \"alcohol\" \"quality\") \"quality\")";
        //expression = " \"quality\"";
        expression = "(IMP \"alcohol\" \"quality\")";

        ParserPredicate parser = new ParserPredicate(expression, states, gs);
        NodeTree pp = parser.parser();
       
        EvaluatePredicate ep = new EvaluatePredicate(pp, new GMBC(), "src/main/resources/datasets/tinto.csv",
                "evaluation-nodetree.csv");
        long startTime = System.nanoTime();
        Double evaluate = ep.evaluate();
        System.out.println(evaluate);
        long endTime = System.nanoTime();

        long duration = (endTime - startTime); // divide by 1000000 to get milliseconds.
        System.out.println("That took " + (duration / 1000000) + " milliseconds");
        ep.resultPrint();
        System.out.println(pp);
        System.out.println(pp.toJson());
        NodeTree clon = (NodeTree) pp.clone();
        System.out.println(clon);
        System.out.println(clon.toJson());
        // ep.exportToCsv();
        // System.out.println(ep.exportToJSON());
        // StateNode.parseState("{:label \"quality \" :colname \"quality\" :f [FPG
        // 3.39817096929029727192528298473916947841644287109375 7.34277098376588
        // 0.74726864798339953654959799678181298077106475830078125]}");
    }

}
