package com.castellanos.fuzzylogicgp.cli;

import java.io.File;
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
import com.castellanos.fuzzylogicgp.membershipfunction.MembershipFunction;
import com.castellanos.fuzzylogicgp.membershipfunction.FPG_MF;
import com.castellanos.fuzzylogicgp.membershipfunction.GAMMA_MF;
import com.castellanos.fuzzylogicgp.membershipfunction.Gaussian_MF;
import com.castellanos.fuzzylogicgp.membershipfunction.LGAMMA_MF;
import com.castellanos.fuzzylogicgp.membershipfunction.LTRAPEZOIDAL_MF;
import com.castellanos.fuzzylogicgp.membershipfunction.MapNominal_MF;
import com.castellanos.fuzzylogicgp.membershipfunction.NSigmoid_MF;
import com.castellanos.fuzzylogicgp.membershipfunction.Nominal_MF;
import com.castellanos.fuzzylogicgp.membershipfunction.PSEUDOEXP_MF;
import com.castellanos.fuzzylogicgp.membershipfunction.RTRAPEZOIDAL_MF;
import com.castellanos.fuzzylogicgp.membershipfunction.SForm_MF;
import com.castellanos.fuzzylogicgp.membershipfunction.Sigmoid_MF;
import com.castellanos.fuzzylogicgp.membershipfunction.Singleton_MF;
import com.castellanos.fuzzylogicgp.membershipfunction.Trapezoidal_MF;
import com.castellanos.fuzzylogicgp.membershipfunction.Triangular_MF;
import com.castellanos.fuzzylogicgp.membershipfunction.ZForm_MF;
import com.castellanos.fuzzylogicgp.parser.DiscoveryQuery;
import com.castellanos.fuzzylogicgp.parser.EDNParser;
import com.castellanos.fuzzylogicgp.logic.LogicType;
import com.castellanos.fuzzylogicgp.parser.Query;
import com.castellanos.fuzzylogicgp.parser.TaskFactory;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import com.castellanos.fuzzylogicgp.parser.EvaluationQuery;
import static java.lang.System.out;

@Command(name = "FLJF", description = "@|bold Demonstrating FLJF |@", headerHeading = "@|bold,underline Demonstration Usage|@:%n%n")
public class Main {

    @Option(names = { "-f", "--file" }, description = "Path and name of file")
    private String fileName;

    @Option(names = { "-h", "--help" }, description = "Display help/usage.", help = true)
    private boolean help;

    @Option(names = { "-p", "--plot" }, description = "Plot linguistic states, Evaluation script is requiered.")
    private ArrayList<String> plot;

    @Option(names = { "--evaluation-demo" }, description = "Run a evaluation demo.")
    private boolean evaluationDemo;
    @Option(names = { "--discovery-demo" }, description = "Run a discovery demo.")
    private boolean discoveryDemo;

    @Option(names = { "--iris" }, description = "Run a discovery demo with iris dataset.")
    private boolean irisDemo;
    @Option(names = { "--EDN" }, description = "Supported EDN script.")
    private boolean formatEdn;
    @Option(names = { "--N" }, description = "No run task.")
    private boolean executeTask;

    public static void main(String[] args)
            throws OperatorException, CloneNotSupportedException, IOException, URISyntaxException {
        final Main main = CommandLine.populateCommand(new Main(), args);
        if (main.help) {
            CommandLine.usage(main, out, CommandLine.Help.Ansi.AUTO);
        } else {
            Query query = null;
            if (main.fileName != null) {

                if (main.formatEdn) {
                    EDNParser ednParser = new EDNParser(main.fileName);
                    query = ednParser.parser();
                } else {
                    query = Query.fromJson(Paths.get(main.fileName));
                }
                if (!main.executeTask)
                    TaskFactory.execute(query);

                if (main.plot != null && main.plot.size() > 0) {
                    TaskFactory.plotting(query, main.plot);
                }
            }
            if (main.evaluationDemo) {
                out.println("Running demo evaluation");
                query = evaluation();
                TaskFactory.execute(demoToFile(query));

                if (main.plot != null && main.plot.size() > 0) {
                    TaskFactory.plotting(query, main.plot);
                }
            } else if (main.discoveryDemo) {
                out.println("Running demo discovery");
                query = discovery();
                TaskFactory.execute(demoToFile(query));
            }else if(main.irisDemo){
                out.println("Running irs demo");
                query = irisQuery();
                TaskFactory.execute(demoToFile(query));
            }

        }
    }

    private static void testMembershipFunction() {

        // Triangular triangular = new Triangular(1.0,5.0,9.0);
        MembershipFunction mf = new Gaussian_MF(5.0, 2.0);
        mf = new Triangular_MF(1.0, 5.0, 9.0);
        mf = new Trapezoidal_MF(1.0, 5.0, 7.0, 8.0);
        mf = new SForm_MF(1.0, 8.0);// *
        mf = new ZForm_MF(2.0, 8.0);
        mf = new Sigmoid_MF(5.0, 1.0);
        mf = new FPG_MF(9.23, 12.30, 0.5);
        mf = new PSEUDOEXP_MF(5.0, 2.0);
        mf = new LTRAPEZOIDAL_MF(3.0, 7.0);
        mf = new RTRAPEZOIDAL_MF(3.0, 7.0);
        mf = new Singleton_MF(5);
        mf = new GAMMA_MF(4, 3);
        mf = new LGAMMA_MF(4, 3);

        StateNode state = new StateNode("high quality", "quality", mf);
        state.plot("/home/thinkpad/Documents/FuzzyLogicGP", "membershipFunctionGrap");
    }

    private static Query demoToFile(Query query) throws IOException {
        InputStream resourceAsStream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream("datasets" + File.separator + query.getDb_uri());
        Path path = Paths.get("dataset.csv");
        Files.copy(resourceAsStream, path, StandardCopyOption.REPLACE_EXISTING);
        query.setDb_uri(path.toFile().getAbsolutePath());
        Path p = Paths.get("demo-script.txt");
        if (p.toFile().exists())
            p.toFile().delete();
        Files.write(p, query.toJSON().getBytes(), StandardOpenOption.CREATE_NEW);
        return query;

    }

    private static Query irisQuery() {
        DiscoveryQuery query = new DiscoveryQuery();

        ArrayList<StateNode> states = new ArrayList<>();
        states.add(new StateNode("sepal lenght", "sepal.length"));
        states.add(new StateNode("sepal width", "sepal.width"));
        states.add(new StateNode("petal lenght", "petal.length"));
        states.add(new StateNode("petal width", "petal.width"));
        StateNode variety = new StateNode("class", "variety");
        // MapNominal_MF mapNominal = new MapNominal_MF();
        // mapNominal.addParameter("Setosa", 1.0);
        // mapNominal.addParameter("Versicolor", 0.33);
        // mapNominal.addParameter("Virginica", 0.33);
        Nominal_MF nominal_MF = new Nominal_MF("Setosa", 1.0);
        variety.setMembershipFunction(nominal_MF);
        //

        query.setStates(states);
        query.setDb_uri("iris.csv");
        String out_file = "result-discovery-irs.csv";
        query.setOut_file(out_file);
        query.setLogic(LogicType.GMBC);
        String predicate = "(IMP \"properties\" \"class\")";
        GeneratorNode generator = new GeneratorNode();
        generator.setLabel("properties");
        ArrayList<String> variables = new ArrayList<>();
        for (StateNode stateNode : states) {
            variables.add(stateNode.getLabel());
        }
        states.add(variety);

        generator.setVariables(variables);
        generator.setOperators(new NodeType[] { NodeType.AND, NodeType.OR, NodeType.NOT });
        ArrayList<GeneratorNode> generators = new ArrayList<>();
        generators.add(generator);
        query.setGenerators(generators);
        query.setPredicate(predicate);
        query.setAdj_min_truth_value(0.1f);
        query.setAdj_num_pop(10);
        query.setDepth(2);
        query.setMut_percentage(0.05f);
        query.setNum_iter(20);
        query.setMin_truth_value(0.95f);
        query.setNum_pop(100);
        query.setNum_result(20);
        query.setAdj_num_iter(2);
        return query;

    }

    private static Query evaluation() {

        EvaluationQuery query = new EvaluationQuery();
        query.setDb_uri("tinto.csv");
        query.setLogic(LogicType.GMBC);
        query.setOut_file("result-evaluation-prop.csv");
        query.setShowTree(true);
        ArrayList<StateNode> states = new ArrayList<>();
        states.add(new StateNode("high alcohol", "alcohol", new Sigmoid_MF(11.65, 9)));
        states.add(new StateNode("low pH", "pH", new NSigmoid_MF(3.375, 2.93)));
        states.add(new StateNode("high quality", "quality", new Sigmoid_MF(5.5, 4)));
        query.setStates(states);
        query.setPredicate("(IMP (NOT (AND \"high alcohol\" \"low pH\")) \"high quality\")");
        return query;
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
        query.setDb_uri("tinto.csv");
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
        query.setAdj_num_pop(10);
        query.setDepth(2);
        query.setMut_percentage(0.05f);
        query.setNum_iter(20);
        query.setMin_truth_value(0.95f);
        query.setNum_pop(100);
        query.setNum_result(20);
        query.setAdj_num_iter(2);
        return query;
    }
}