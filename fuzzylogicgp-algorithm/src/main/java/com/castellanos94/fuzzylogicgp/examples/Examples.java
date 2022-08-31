package com.castellanos94.fuzzylogicgp.examples;

import java.time.Instant;
import java.util.ArrayList;

import com.castellanos94.fuzzylogicgp.core.DiscoveryQuery;
import com.castellanos94.fuzzylogicgp.core.DummyGenerator;
import com.castellanos94.fuzzylogicgp.core.EvaluationQuery;
import com.castellanos94.fuzzylogicgp.core.NodeType;
import com.castellanos94.fuzzylogicgp.core.Query;
import com.castellanos94.fuzzylogicgp.core.StateNode;
import com.castellanos94.fuzzylogicgp.logic.LogicBuilder;
import com.castellanos94.fuzzylogicgp.logic.LogicType;
import com.castellanos94.fuzzylogicgp.membershipfunction.FPG;
import com.castellanos94.fuzzylogicgp.membershipfunction.Gamma;
import com.castellanos94.fuzzylogicgp.membershipfunction.Gaussian;
import com.castellanos94.fuzzylogicgp.membershipfunction.LGamma;
import com.castellanos94.fuzzylogicgp.membershipfunction.LTrapezoidal;
import com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunction;
import com.castellanos94.fuzzylogicgp.membershipfunction.NSigmoid;
import com.castellanos94.fuzzylogicgp.membershipfunction.Nominal;
import com.castellanos94.fuzzylogicgp.membershipfunction.PSeudoExp;
import com.castellanos94.fuzzylogicgp.membershipfunction.RTrapezoidal;
import com.castellanos94.fuzzylogicgp.membershipfunction.SForm;
import com.castellanos94.fuzzylogicgp.membershipfunction.Sigmoid;
import com.castellanos94.fuzzylogicgp.membershipfunction.Singleton;
import com.castellanos94.fuzzylogicgp.membershipfunction.Trapezoidal;
import com.castellanos94.fuzzylogicgp.membershipfunction.Triangular;
import com.castellanos94.fuzzylogicgp.membershipfunction.ZForm;

public class Examples {

    public static Query irisQuery() {
        DiscoveryQuery query = new DiscoveryQuery();

        ArrayList<StateNode> states = new ArrayList<>();
        states.add(new StateNode("sepal lenght", "sepal.length"));
        states.add(new StateNode("sepal width", "sepal.width"));
        states.add(new StateNode("petal lenght", "petal.length"));
        states.add(new StateNode("petal width", "petal.width"));
        StateNode Setosa = new StateNode("Setosa", "variety", new Nominal("Setosa", 1.0));
        StateNode Versicolor = new StateNode("Versicolor", "variety", new Nominal("Versicolor", 1.0));
        StateNode Virginica = new StateNode("Virginica", "variety", new Nominal("Virginica", 1.0));

        //
        query.setStates(states);
        query.setDb_uri("iris.csv");
        String out_file = "result-discovery-irs.csv";
        query.setOut_file(out_file);
        query.setLogic(LogicBuilder.newBuilder(LogicType.GMBC));
        DummyGenerator generator = new DummyGenerator();
        generator.setLabel("properties");
        ArrayList<String> variables = new ArrayList<>();
        for (StateNode stateNode : states) {
            variables.add(stateNode.getLabel());
        }
        generator.setVariables(variables);
        generator.setOperators(new NodeType[] { NodeType.AND, NodeType.OR, NodeType.NOT });

        states.add(Setosa);
        states.add(Versicolor);
        states.add(Virginica);
        ArrayList<String> vrs = new ArrayList<>();
        vrs.add(Setosa.getLabel());
        vrs.add(Versicolor.getLabel());
        vrs.add(Virginica.getLabel());
        DummyGenerator classG = new DummyGenerator("class", new NodeType[] { NodeType.NOT }, vrs, 2);

        ArrayList<DummyGenerator> generators = new ArrayList<>();
        generators.add(generator);
        generators.add(classG);

        String predicate = "(EQV \"properties\" \"class\")";

        query.setGenerators(generators);
        query.setPredicate(predicate);
        query.setAdj_min_truth_value(0.1f);
        query.setAdj_num_pop(10);

        query.setMut_percentage(0.05f);
        query.setNum_iter(30);
        query.setMin_truth_value(0.95f);
        query.setNum_pop(100);
        query.setNum_result(25);
        query.setAdj_num_iter(2);
        return query;

    }

    public static Query evaluation() {

        EvaluationQuery query = new EvaluationQuery();
        query.setDb_uri("tinto.csv");

        query.setLogic(LogicBuilder.newBuilder(LogicType.GMBC));
        query.setOut_file("result-evaluation-prop.csv");
        query.setShowTree(true);
        ArrayList<StateNode> states = new ArrayList<>();
        //states.add(new StateNode("high alcohol", "alcohol", new FPG(0, 0, 0)));
        states.add(new StateNode("high alcohol", "alcohol", new Sigmoid(11.65, 9)));
        states.add(new StateNode("low pH", "pH", new NSigmoid(3.375, 2.93)));
        states.add(new StateNode("high quality", "quality", new Sigmoid(5.5, 4)));
        states.add(new StateNode("high pH", "pH", new FPG(1.3, 1.93, 0.5)));

        states.forEach(s -> s.setDescription(Instant.now().toString()));
        query.setStates(states);
        query.setPredicate("(IMP (NOT (AND \"high alcohol\" \"low pH\")) \"high quality\")");
        query.setIncludeFuzzyData(true);
        return query;
    }

    public static Query discovery() {
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
        // states.add(new StateNode("chlorides", "chlorides"));

        // states.add(new StateNode("low pH", "pH", new NSigmoid(3.375, 2.93)));
        query.setStates(states);
        query.setDb_uri("tinto.csv");
        String out_file = "result-discovery-prop.csv";
        query.setOut_file(out_file);
        query.setLogic(LogicBuilder.newBuilder(LogicType.GMBC));
        String predicate = "(IMP \"comodin\"  \"quality\")";
        //predicate = "\"comodin\"";
        DummyGenerator generator = new DummyGenerator();
        generator.setLabel("comodin");
        generator.setDepth(2);
        generator.setMax_child_number(3);
        ArrayList<String> variables = new ArrayList<>();
        for (StateNode stateNode : states) {
            if (!stateNode.getLabel().equalsIgnoreCase("quality")) {
                variables.add(stateNode.getLabel());
            }
        }
        generator.setVariables(variables);
        generator.setOperators(new NodeType[] { NodeType.AND,NodeType.OR,NodeType.EQV  });
        ArrayList<DummyGenerator> generators = new ArrayList<>();
        generators.add(generator);
        query.setGenerators(generators);
        query.setPredicate(predicate);
        query.setAdj_min_truth_value(0.5f);
        query.setAdj_num_pop(20);
        query.setAdj_num_iter(20);

        query.setMut_percentage(0.75f);
        
        query.setNum_iter(1);
        query.setMin_truth_value(0.95f);
        query.setNum_pop(50);

        query.setNum_result(20);
        return query;
    }

    public static Query generator() {
        DiscoveryQuery query = new DiscoveryQuery();

        ArrayList<StateNode> states = new ArrayList<>();
        states.add(new StateNode("c", "chlorides"));
        states.add(new StateNode("q", "quality"));
        states.add(new StateNode("a", "alcohol"));
        states.add(new StateNode("fa", "fixed_acidity"));
        states.add(new StateNode("tsd", "total_sulfur_dioxide"));
        states.add(new StateNode("va", "volatile_acidity"));
        states.add(new StateNode("d", "density"));
        states.add(new StateNode("ca", "citric_acid"));        
        states.add(new StateNode("rs", "residual_sugar"));        
        
        
        // states.add(new StateNode("chlorides", "chlorides"));

        // states.add(new StateNode("low pH", "pH", new NSigmoid(3.375, 2.93)));
        query.setStates(states);
        query.setDb_uri("tinto.csv");
        String out_file = "result-generador.csv";
        query.setOut_file(out_file);
        query.setLogic(LogicBuilder.newBuilder(LogicType.GMBC));
        String predicate = "\"generador simple\"";
        DummyGenerator generator = new DummyGenerator();
        generator.setLabel("generador simple");
        generator.setDepth(3);
        ArrayList<String> variables = new ArrayList<>();
        states.stream().map(StateNode::getLabel).forEach(variables::add);

        generator.setVariables(variables);
        generator.setOperators(new NodeType[] { NodeType.AND,NodeType.OR,NodeType.NOT,NodeType.IMP, NodeType.EQV });
        ArrayList<DummyGenerator> generators = new ArrayList<>();
        generators.add(generator);
        query.setGenerators(generators);
        query.setPredicate(predicate);
        query.setAdj_min_truth_value(0.5f);
        query.setAdj_num_pop(20);
        query.setAdj_num_iter(50);

        query.setMut_percentage(0.75f);
        
        query.setNum_iter(100);
        query.setMin_truth_value(0.75f);
        query.setNum_pop(50);

        query.setNum_result(20);
        return query;
    }

    public static void testMembershipFunction() {

        // Triangular triangular = new Triangular(1.0,5.0,9.0);
        MembershipFunction mf = new Gaussian(5.0, 2.0);
        mf = new Triangular(1.0, 5.0, 9.0);
        mf = new Trapezoidal(1.0, 5.0, 7.0, 8.0);
        mf = new SForm(1.0, 8.0);// *
        mf = new ZForm(2.0, 8.0);
        mf = new Sigmoid(5.0, 1.0);
        mf = new FPG(9.23, 12.30, 0.5);
        mf = new PSeudoExp(5.0, 2.0);
        mf = new LTrapezoidal(3.0, 7.0);
        mf = new RTrapezoidal(3.0, 7.0);
        mf = new Singleton(5);
        mf = new Gamma(4, 3);
        mf = new LGamma(4, 3);

        StateNode state = new StateNode("high quality", "quality", mf);
        state.plot("", "membershipFunctionGrap");
    }

}
