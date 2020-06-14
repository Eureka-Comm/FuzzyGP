package com.castellanos.fuzzylogicgp.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.castellanos.fuzzylogicgp.base.GeneratorNode;
import com.castellanos.fuzzylogicgp.base.NodeTree;
import com.castellanos.fuzzylogicgp.base.OperatorException;
import com.castellanos.fuzzylogicgp.base.StateNode;
import com.castellanos.fuzzylogicgp.core.EvaluatePredicate;
import com.castellanos.fuzzylogicgp.core.KDFLC;
import com.castellanos.fuzzylogicgp.logic.ALogic;
import com.castellanos.fuzzylogicgp.logic.AMBC;
import com.castellanos.fuzzylogicgp.logic.GMBC;

import tech.tablesaw.api.Table;

public class TaskFactory {
    public static void execute(Query query) throws OperatorException, CloneNotSupportedException, IOException {
        ParserPredicate parserPredicate;
        NodeTree p;
        ALogic logic = getLogic(query);
        switch (query.getType()) {
            case EVALUATION:
                parserPredicate = new ParserPredicate(query.getPredicate(), query.getStates(), new ArrayList<>());
                p = parserPredicate.parser();
                EvaluatePredicate evaluator = new EvaluatePredicate(p, logic, query.getDb_uri(), query.getOut_file());
                evaluator.evaluate();
                evaluator.exportToCsv();
                break;
            case DISCOVERY:
                DiscoveryQuery discoveryQuery = (DiscoveryQuery) query;

                parserPredicate = new ParserPredicate(query.getPredicate(), query.getStates(),
                        discoveryQuery.getGenerators());
                Table data = Table.read().file(new File(discoveryQuery.getDb_uri()));
                KDFLC discovery = new KDFLC(parserPredicate, logic, discoveryQuery.getDepth(),
                        discoveryQuery.getNum_pop(), discoveryQuery.getNum_iter(), discoveryQuery.getNum_result(),
                        discoveryQuery.getMin_truth_value(), discoveryQuery.getMut_percentage(),
                        discoveryQuery.getAdj_num_pop(), discoveryQuery.getAdj_num_iter(),
                        discoveryQuery.getAdj_min_truth_value(), data);
                discovery.execute();
                discovery.exportToCsv(discoveryQuery.getOut_file());
                break;
            default:
                throw new IllegalArgumentException("Unsupported query.");
        }
    }

    private static ALogic getLogic(Query query) {
        switch (query.getLogic()) {
            case AMBC:
                return new AMBC();
            case GMBC:
                return new GMBC();
            default:
                return null;
        }
    }

}