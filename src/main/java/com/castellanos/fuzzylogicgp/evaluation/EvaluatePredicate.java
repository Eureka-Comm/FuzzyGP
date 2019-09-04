/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.evaluation;

import com.castellanos.fuzzylogicgp.base.GeneratorNode;
import com.castellanos.fuzzylogicgp.base.IMPNode;
import com.castellanos.fuzzylogicgp.base.Node;
import com.castellanos.fuzzylogicgp.base.NodeType;
import com.castellanos.fuzzylogicgp.base.OperatorException;
import com.castellanos.fuzzylogicgp.base.Predicate;
import com.castellanos.fuzzylogicgp.base.StateNode;
import com.castellanos.fuzzylogicgp.logic.ALogic;
import com.castellanos.fuzzylogicgp.logic.GMBC;
import com.castellanos.fuzzylogicgp.membershipfunction.NSigmoid;
import com.castellanos.fuzzylogicgp.membershipfunction.Sigmoid;
import com.castellanos.fuzzylogicgp.parser.ParserPredicate;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

/**
 *
 * @author hp
 */
public class EvaluatePredicate {

    private Predicate p;
    private ALogic logic;
    private Table data;
    private Table fuzzyData;
    private DoubleColumn resultColumn;
    private double forAll;
    private double exists;

    public EvaluatePredicate(Predicate p, ALogic logic, String path) {
        this.p = p;
        this.logic = logic;
        try {
            this.data = Table.read().csv(path);
        } catch (IOException ex) {
            Logger.getLogger(EvaluatePredicate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void evaluate() {
        dataFuzzy();
        fitCompute();
       
        //fuzzyData.addColumns(,allColumn,resultColumn);
       
        System.out.println(resultColumn.print());
         System.out.println(forAll );
        System.out.println(exists);
    }

    public void fitCompute() {
        double fit = 0.0;
        double result;
        double aux;
        resultColumn = DoubleColumn.create("result");
        for (int i = 0; i < fuzzyData.rowCount(); i++) {
            try {
                result = fitValue(p.getNode(p.getIdFather()), i);
                resultColumn.append(result);
                if (result == 0) {
                    fit += 0;
                } else {
                    fit += Math.log(result);
                }
            } catch (OperatorException ex) {
                Logger.getLogger(EvaluatePredicate.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        aux = (double) (1.0 / fuzzyData.rowCount());
        aux = aux * fit;
        forAll = (Math.pow(Math.E, aux));
        exists = (1 - (double)forAll);

    }

    private Double fitValue(Node node, int index) throws OperatorException {
        double aux = 1;
        List<Node> child;
        switch (node.getType()) {
            case AND:
                child = p.searchChilds(node);
                for (int i = 1; i < child.size(); i++) {
                    aux *= fitValue(child.get(i), index);
                }
                return logic.and(aux, fitValue(child.get(0), index));
            case OR:
                child = p.searchChilds(node);
                for (int i = 1; i < child.size(); i++) {
                    aux *= (1 - fitValue(child.get(i), index));
                }
                return logic.or(aux, fitValue(child.get(0), index));
            case NOT:
                return fitValue(p.searchChilds(node).get(0), index);
            case IMP:
                IMPNode imp = (IMPNode) node;
                return logic.imp(fitValue(p.getNode(imp.getLeftID()), index), fitValue(p.getNode(imp.getRighID()), index));
            case EQV:
                child = p.searchChilds(node);
                return logic.eqv(fitValue(child.get(0), index), fitValue(child.get(1), index));
            case STATE:
                StateNode st = (StateNode) node;
                return Double.valueOf(fuzzyData.getString(index, st.getLabel()));
            default:
                throw new UnsupportedOperationException("Dont supported: " + node.getType() + " : " + node.getId()); //To change body of generated methods, choose Tools | Templates.
        }

    }

    private void dataFuzzy() {
        fuzzyData = Table.create();
        p.getNodes().forEach((String k, Node v) -> {
            if (v instanceof StateNode) {
                StateNode s = (StateNode) v;
                ColumnType type = data.column(s.getColName()).type();

                DoubleColumn dc = DoubleColumn.create(s.getLabel());

                if (type == ColumnType.DOUBLE) {
                    Column<Double> column = (Column<Double>) data.column(s.getColName());

                    for (Double cell : column) {
                        dc.append(s.getMembershipFunction().evaluate(cell));
                    }

                } else if (type == ColumnType.FLOAT) {
                    Column<Float> column = (Column<Float>) data.column(s.getColName());
                    for (Float cell : column) {
                        dc.append(s.getMembershipFunction().evaluate(cell));
                    }

                } else if (type == ColumnType.INTEGER) {
                    Column<Integer> column = (Column<Integer>) data.column(s.getColName());
                    for (Integer cell : column) {
                        dc.append(s.getMembershipFunction().evaluate(cell));
                    }

                } else if (type == ColumnType.LONG) {
                    Column<Long> column = (Column<Long>) data.column(s.getColName());
                    for (Long cell : column) {
                        dc.append(s.getMembershipFunction().evaluate(cell));
                    }

                } else {
                    System.out.println("ColumnType: " + type);
                }
                fuzzyData.addColumns(dc);
            }
        });
        //System.out.println(fuzzyData);
    }

    public static void main(String[] args) throws OperatorException {
        /**
         * :states [ { :label "high alcohol" :colname "alcohol" :f [sigmoid
         * 11.65 9] }
         *
         * {
         * :label "low pH" :colname "pH" :f [-sigmoid 3.375 2.93] }
         *
         * {
         * :label "high quality" :colname "quality" :f [sigmoid 5.5 4] } ]
         * :logic [:GMBC] :predicate (IMP (AND "high alcohol" "low pH") "high quality")
         */
        Sigmoid fha = new Sigmoid(11.65, 9);
        StateNode ha = new StateNode("high alcohol", "alcohol", fha);
        NSigmoid flp = new NSigmoid(3.375, 2.93);
        StateNode lp = new StateNode("low pH", "pH", flp);
        Sigmoid fhq = new Sigmoid(5.5, 4);
        StateNode hq = new StateNode("high quality", "quality", fhq);

        List<StateNode> states = new ArrayList<>();
        states.add(ha);
        states.add(lp);
        states.add(hq);

        GeneratorNode g = new GeneratorNode("*", new NodeType[]{}, new ArrayList<>());
        List<GeneratorNode> gs = new ArrayList<>();

        String expression = "(IMP (AND \"high alcohol\" \"low pH\") \"high quality\")";
        ParserPredicate parser = new ParserPredicate(expression, states, gs);
        Predicate pp = parser.parser();
        //System.out.println(pp.toJson());
//
//0.7163711308562271
//0.28362886914377294
        EvaluatePredicate ep = new EvaluatePredicate(pp, new GMBC(), "src/main/resources/datasets/tinto.csv");
        ep.evaluate();
    }

}
