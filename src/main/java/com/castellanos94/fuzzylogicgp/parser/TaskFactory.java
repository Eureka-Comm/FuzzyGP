package com.castellanos94.fuzzylogicgp.parser;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Locale;

import com.castellanos94.fuzzylogicgp.base.NodeTree;
import com.castellanos94.fuzzylogicgp.base.OperatorException;
import com.castellanos94.fuzzylogicgp.base.StateNode;
import com.castellanos94.fuzzylogicgp.core.EvaluatePredicate;
import com.castellanos94.fuzzylogicgp.core.KDFLC;
import com.castellanos94.fuzzylogicgp.logic.Logic;

import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.io.xlsx.XlsxReadOptions;
import tech.tablesaw.io.xlsx.XlsxReader;

public class TaskFactory {
    public static void execute(Query query) throws OperatorException, CloneNotSupportedException, IOException {
        ParserPredicate parserPredicate;
        NodeTree p;
        Logic logic = query.getLogic().build();
        switch (query.getType()) {
            case EVALUATION:
                EvaluationQuery evaluationQuery = (EvaluationQuery) query;
                parserPredicate = new ParserPredicate(evaluationQuery.getPredicate(), evaluationQuery.getStates(),
                        new ArrayList<>());
                p = parserPredicate.parser();
                EvaluatePredicate evaluator = new EvaluatePredicate(p, logic, evaluationQuery.getDb_uri(),
                        evaluationQuery.getOut_file());
                double forall = evaluator.evaluate();
                evaluationQuery.setJsonPredicate(p.toJson());
                System.out.println("For all: " + forall);

                evaluator.exportToCsv();

                if (((EvaluationQuery) query).isShowTree()) {
                    String stP = new File(query.getOut_file()).getParent();
                    if (stP == null)
                        stP = "";
                    String name = new File(query.getOut_file()).getName().replace(".xlsx", ".json").replace(".csv",
                            ".json");
                    Path path = Paths.get(stP, "tree-" + name);
                    Files.write(path, p.toJson().getBytes(), StandardOpenOption.CREATE);
                }
                break;
            case DISCOVERY:
                DiscoveryQuery discoveryQuery = (DiscoveryQuery) query;

                parserPredicate = new ParserPredicate(query.getPredicate(), query.getStates(),
                        discoveryQuery.getGenerators());

                p = parserPredicate.parser();
                Table data;
                if (discoveryQuery.getDb_uri().contains(".csv")) {
                    CsvReadOptions build = CsvReadOptions.builder(new File(discoveryQuery.getDb_uri()))
                            .locale(Locale.US).header(true).build();
                    data = Table.read().csv(build);

                } else {
                    XlsxReader reader = new XlsxReader();
                    XlsxReadOptions options = XlsxReadOptions.builder(discoveryQuery.getDb_uri()).locale(Locale.US)
                            .build();
                    data = reader.read(options);
                }

                KDFLC discovery = new KDFLC(logic, discoveryQuery.getNum_pop(), discoveryQuery.getNum_iter(),
                        discoveryQuery.getNum_result(), discoveryQuery.getMin_truth_value(),
                        discoveryQuery.getMut_percentage(), discoveryQuery.getAdj_num_pop(),
                        discoveryQuery.getAdj_num_iter(), discoveryQuery.getAdj_min_truth_value(), data);
                discovery.execute(p);
                /*
                 * for (int i = 0; i < discovery.getResultList().size(); i++) {
                 * System.out.print((i+1)+": "+discovery.getResultList().get(i).getFitness()+" "
                 * ); EvaluatePredicate _evaluator = new
                 * EvaluatePredicate(discovery.getResultList().get(i), logic, data);
                 * System.out.println(_evaluator.evaluate());
                 * 
                 * }
                 */
                discovery.exportToCsv(discoveryQuery.getOut_file());
                break;
            default:
                throw new IllegalArgumentException("Unsupported query.");
        }
    }

    /*
     * private static Logic getLogic(Query query) { switch (query.getLogic()) { case
     * AMBC: return new AMBC_Logic(); case GMBC: return new GMBC_Logic(); case
     * ZADEH: return new Zadeh_Logic(); case ACF: return new ACF_Logic(); default:
     * return null;
     * 
     * } }
     */

    public static void plotting(Query query, ArrayList<String> labels)
            throws OperatorException, CloneNotSupportedException, URISyntaxException, UnsupportedEncodingException {
        System.out.println("Plotting states...");
        switch (query.getType()) {
            case EVALUATION:
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