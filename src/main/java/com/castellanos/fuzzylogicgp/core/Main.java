package com.castellanos.fuzzylogicgp.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;

import com.castellanos.fuzzylogicgp.base.GeneratorNode;
import com.castellanos.fuzzylogicgp.base.NodeType;
import com.castellanos.fuzzylogicgp.base.OperatorException;
import com.castellanos.fuzzylogicgp.base.StateNode;
import com.castellanos.fuzzylogicgp.membershipfunction.NSigmoid;
import com.castellanos.fuzzylogicgp.membershipfunction.Sigmoid;
import com.castellanos.fuzzylogicgp.parser.DiscoveryQuery;
import com.castellanos.fuzzylogicgp.parser.EDNParser;
import com.castellanos.fuzzylogicgp.parser.LogicType;
import com.castellanos.fuzzylogicgp.parser.Query;
import com.castellanos.fuzzylogicgp.parser.TaskFactory;

import com.castellanos.fuzzylogicgp.parser.EvaluationQuery;

public class Main {
    public static void main(String[] args)
            throws OperatorException, CloneNotSupportedException, IOException, URISyntaxException {
        // evaluation();
        // discovery();
        System.out.println(Arrays.toString(args));
        if (args.length > 0 && !args[0].trim().equals("-h")) {
            Query query;
            switch (args[0]) {
                case "demo-evaluation":
                    System.out.println("Running demo evaluation");
                    query = evaluation();
                    demoToFile(query);
                    TaskFactory.execute(query);
                    break;
                case "demo-discovery":
                    System.out.println("Running demo discovery");
                    query = discovery();
                    demoToFile(query);
                    TaskFactory.execute(query);
                    break;
                default:
                    if (args.length >= 2 && args[1].trim().equals("-format=edn")) {
                        EDNParser ednParser = new EDNParser(args[0].trim());
                        query = ednParser.parser();
                    } else {
                        query = Query.fromJson(Paths.get(args[0].trim()));
                    }
                    TaskFactory.execute(query);
                    break;
            }

        } else {
            System.out.println("Usage:");
            System.out.println("Load a job to process by its file path ");
            System.out.println("or check demo");
            System.out.println("\tjava App.jar demo-evaluation");
            System.out.println("\tjava App.jar demo-discovery");
            System.out.println("For EDN script support, use: -format=edn");
        }

    }

    private static void demoToFile(Query query) throws IOException {
        InputStream resourceAsStream = ClassLoader.getSystemClassLoader().getResourceAsStream("datasets/tinto.csv");
        Path path = Paths.get("dataset.csv");
        Files.copy(resourceAsStream, path, StandardCopyOption.REPLACE_EXISTING);
        query.setDb_uri(path.toFile().getAbsolutePath());
        Path p = Paths.get("demo-script.txt");
        if (p.toFile().exists())
            p.toFile().delete();
        Files.write(p, query.toJSON().getBytes(), StandardOpenOption.CREATE_NEW);

    }

    private static Query discovery() {
        DiscoveryQuery query = new DiscoveryQuery();

        ArrayList<StateNode> states = new ArrayList<>();
        states.add(new StateNode("citric_acid", "citric_acid"));
        states.add(new StateNode("volatile_acidity", "volatile_acidity"));
        states.add(new StateNode("fixed_acidity", "fixed_acidity"));
        states.add(new StateNode("free_sulfur_dioxide", "free_sulfur_dioxide"));
        states.add(new StateNode("sulphates", "sulphates"));
        states.add(new StateNode("alcohol", "alcohol"));
        states.add(new StateNode("residual_sugar", "residual_sugar"));
        states.add(new StateNode("pH", "pH"));
        states.add(new StateNode("total_sulfur_dioxide", "total_sulfur_dioxide"));
        states.add(new StateNode("quality", "quality"));
        states.add(new StateNode("density", "density"));
        states.add(new StateNode("chlorides", "chlorides"));

        query.setStates(states);
        String db_uri = "src/main/resources/datasets/tinto.csv";
        query.setDb_uri(db_uri);
        String out_file = "result-discovery-prop.csv";
        query.setOut_file(out_file);
        query.setLogic(LogicType.GMBC);
        String predicate = "(IMP \"comodin\" \"quality\")";
        GeneratorNode generator = new GeneratorNode();
        generator.setLabel("comodin");
        ArrayList<String> variables = new ArrayList<>();
        for (StateNode stateNode : states) {
            variables.add(stateNode.getLabel());
        }
        generator.setVariables(variables);
        generator.setOperators(new NodeType[] { NodeType.AND, NodeType.OR, NodeType.IMP, NodeType.EQV, NodeType.NOT });
        ArrayList<GeneratorNode> generators = new ArrayList<>();
        generators.add(generator);
        query.setGenerators(generators);
        query.setPredicate(predicate);
        query.setAdj_min_truth_value(0.1f);
        query.setAdj_num_pop(100);
        query.setDepth(2);
        query.setMut_percentage(0.05f);
        query.setNum_iter(100);
        query.setMin_truth_value(0.9f);
        query.setNum_pop(50);
        query.setNum_result(20);
        query.setAdj_num_iter(1);
        return query;

    }

    private static Query evaluation() {

        EvaluationQuery query = new EvaluationQuery();
        query.setDb_uri("src/main/resources/datasets/tinto.csv");
        query.setLogic(LogicType.GMBC);
        query.setOut_file("result-evaluation-prop.csv");
        query.setShowTree(true);
        ArrayList<StateNode> states = new ArrayList<>();
        states.add(new StateNode("high alcohol", "alcohol", new Sigmoid(11.65, 9)));
        states.add(new StateNode("low pH", "pH", new NSigmoid(3.375, 2.93)));
        states.add(new StateNode("high quality", "quality", new Sigmoid(5.5, 4)));
        query.setStates(states);
        query.setPredicate("(IMP (NOT (AND \"high alcohol\" \"low pH\")) \"high quality\")");
        return query;
    }

}