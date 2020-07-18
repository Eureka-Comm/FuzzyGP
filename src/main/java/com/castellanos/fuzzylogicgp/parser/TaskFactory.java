package com.castellanos.fuzzylogicgp.parser;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import com.castellanos.fuzzylogicgp.base.NodeTree;
import com.castellanos.fuzzylogicgp.base.OperatorException;
import com.castellanos.fuzzylogicgp.base.StateNode;
import com.castellanos.fuzzylogicgp.core.EvaluatePredicate;
import com.castellanos.fuzzylogicgp.core.KDFLC;
import com.castellanos.fuzzylogicgp.logic.Logic;
import com.castellanos.fuzzylogicgp.logic.Zadeh_Logic;
import com.castellanos.fuzzylogicgp.logic.AMBC_Logic;
import com.castellanos.fuzzylogicgp.logic.GMBC_Logic;
import com.castellanos.fuzzylogicgp.logic.ACF_Logic;

import tech.tablesaw.api.Table;
import tech.tablesaw.io.xlsx.XlsxReadOptions;
import tech.tablesaw.io.xlsx.XlsxReader;

public class TaskFactory {
    public static void execute(Query query) throws OperatorException, CloneNotSupportedException, IOException {
        ParserPredicate parserPredicate;
        NodeTree p;
        Logic logic = getLogic(query);
        switch (query.getType()) {
            case EVALUATION:
                parserPredicate = new ParserPredicate(query.getPredicate(), query.getStates(), new ArrayList<>());
                p = parserPredicate.parser();
                EvaluatePredicate evaluator = new EvaluatePredicate(p, logic, query.getDb_uri(), query.getOut_file());
                evaluator.evaluate();
                evaluator.exportToCsv();
                if (((EvaluationQuery) query).isShowTree()) {
                    String stP = new File(query.getOut_file()).getParent();
                    if (stP == null)
                        stP = "";
                    Path path = Paths.get(stP, "tree-" + System.currentTimeMillis() + ".json");
                    Files.write(path, p.toJson().getBytes(), StandardOpenOption.CREATE_NEW);
                }
                break;
            case DISCOVERY:
                DiscoveryQuery discoveryQuery = (DiscoveryQuery) query;

                parserPredicate = new ParserPredicate(query.getPredicate(), query.getStates(),
                        discoveryQuery.getGenerators());
                Table data;
                if (discoveryQuery.getDb_uri().contains(".csv")) {
                    data = Table.read().file(new File(discoveryQuery.getDb_uri()));
                } else {
                    XlsxReader reader = new XlsxReader();
                    XlsxReadOptions options = XlsxReadOptions.builder(discoveryQuery.getDb_uri()).build();
                    data = reader.read(options);
                }

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

    private static Logic getLogic(Query query) {
        switch (query.getLogic()) {
            case AMBC:
                return new AMBC_Logic();
            case GMBC:
                return new GMBC_Logic();
            case ZADEH:
                return new Zadeh_Logic();
            case ACF:
                return new ACF_Logic();
            default:
                return null;
            
        }
    }

    public static void plotting(Query query, ArrayList<String> labels)
            throws OperatorException, CloneNotSupportedException, URISyntaxException, UnsupportedEncodingException {
        System.out.println("Plotting states...");
        ParserPredicate parserPredicate;
        NodeTree p;

        switch (query.getType()) {
            case EVALUATION:
                parserPredicate = new ParserPredicate(query.getPredicate(), query.getStates(), new ArrayList<>());
                p = parserPredicate.parser();
                String dir = new File(TaskFactory.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                        .getParent();
                for (StateNode stateNode : query.getStates()) {
                    for (String string : labels) {
                        if (stateNode.getLabel().equals(string)) {
                            stateNode.plot(null, null);
                            break;
                        }
                    }
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported query.");
        }
    }

}