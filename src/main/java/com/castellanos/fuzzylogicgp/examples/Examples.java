package com.castellanos.fuzzylogicgp.examples;

import java.util.ArrayList;

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
import com.castellanos.fuzzylogicgp.parser.EvaluationQuery;
import com.castellanos.fuzzylogicgp.logic.LogicType;
import com.castellanos.fuzzylogicgp.parser.Query;
import com.castellanos.fuzzylogicgp.parser.TaskFactory;

public class Examples {
    public static void main(String[] args) {
        StateNode node = new StateNode("dummy","dummy");
        MembershipFunction mf = new FPG_MF(1.9604408277229095,1.9649108603405534,0.4111713336297085);
       //mf = new FPG_MF(1952.94681253064,4582.599891699062,0.26771988761419474);
        //mf = new Sigmoid_MF(5.5,4);
       // mf = new FPG_MF(596.4881580779418,1512.0808103003033,0.16039005160976927);
        //mf = new Triangular_MF(0.0,1.0,1.0);
      //  mf = new Triangular_MF(3.,6.,8.);
        node.setMembershipFunction(mf);
        node.plot("","dummy");

    }
    public static Query irisQuery() {
        DiscoveryQuery query = new DiscoveryQuery();

        ArrayList<StateNode> states = new ArrayList<>();
        states.add(new StateNode("sepal lenght", "sepal.length"));
        states.add(new StateNode("sepal width", "sepal.width"));
        states.add(new StateNode("petal lenght", "petal.length"));
        states.add(new StateNode("petal width", "petal.width"));
        StateNode Setosa = new StateNode("Setosa", "variety", new Nominal_MF("Setosa", 1.0));
        StateNode Versicolor = new StateNode("Versicolor", "variety", new Nominal_MF("Versicolor", 1.0));
        StateNode Virginica = new StateNode("Virginica", "variety", new Nominal_MF("Virginica", 1.0));

        //

        query.setStates(states);
        query.setDb_uri("iris.csv");
        String out_file = "result-discovery-irs.csv";
        query.setOut_file(out_file);
        query.setLogic(LogicType.GMBC);
        GeneratorNode generator = new GeneratorNode();
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
        GeneratorNode classG = new GeneratorNode("class", new NodeType[] { NodeType.NOT }, vrs);

        ArrayList<GeneratorNode> generators = new ArrayList<>();
        generators.add(generator);
        generators.add(classG);

        String predicate = "(EQV \"properties\" \"class\")";

        query.setGenerators(generators);
        query.setPredicate(predicate);
        query.setAdj_min_truth_value(0.1f);
        query.setAdj_num_pop(10);
        query.setDepth(2);
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

    public static void testMembershipFunction() {

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

}