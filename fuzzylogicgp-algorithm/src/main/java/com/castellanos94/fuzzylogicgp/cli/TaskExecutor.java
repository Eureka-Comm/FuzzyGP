package com.castellanos94.fuzzylogicgp.cli;

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

import com.castellanos94.fuzzylogicgp.algorithm.EvaluatePredicate;
import com.castellanos94.fuzzylogicgp.algorithm.KDFLC;
import com.castellanos94.fuzzylogicgp.core.DiscoveryQuery;
import com.castellanos94.fuzzylogicgp.core.EvaluationQuery;
import com.castellanos94.fuzzylogicgp.core.NodeTree;
import com.castellanos94.fuzzylogicgp.core.OperatorException;
import com.castellanos94.fuzzylogicgp.core.Query;
import com.castellanos94.fuzzylogicgp.core.StateNode;
import com.castellanos94.fuzzylogicgp.logic.Logic;
import com.castellanos94.fuzzylogicgp.parser.ParserPredicate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.io.xlsx.XlsxReadOptions;
import tech.tablesaw.io.xlsx.XlsxReader;

public class TaskExecutor {

    private static final Logger logger = LogManager.getLogger(TaskExecutor.class);
    public static boolean parallelSupport = true;

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
                        evaluationQuery.getOut_file(), evaluationQuery.isIncludeFuzzyData());
                double forall = evaluator.evaluate();
                evaluationQuery.setJsonPredicate(p.toJson());
                logger.info("For all: " + forall);

                evaluator.exportToCsv();
                if (((EvaluationQuery) query).isShowTree()) {
                    String stP = new File(query.getOut_file()).getParent();
                    if (stP == null) {
                        stP = "";
                    }
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
                        discoveryQuery.getAdj_num_iter(), discoveryQuery.getAdj_min_truth_value(), data,
                        discoveryQuery.getMaxTime());
                discovery.setParallelSupport(parallelSupport);
                logger.info("Parallel support : " + parallelSupport);
                discovery.execute(p);
                discovery.exportResult(new File(discoveryQuery.getOut_file()));
                break;
            default:
                throw new IllegalArgumentException("Unsupported query.");
        }
    }

    public static void plotting(Query query, ArrayList<String> labels)
            throws OperatorException, CloneNotSupportedException, URISyntaxException, UnsupportedEncodingException {
        logger.info("Plotting states...");
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
