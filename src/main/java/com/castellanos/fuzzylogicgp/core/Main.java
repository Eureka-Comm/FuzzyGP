package com.castellanos.fuzzylogicgp.core;

import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.castellanos.fuzzylogicgp.base.NodeType;
import com.castellanos.fuzzylogicgp.base.StateNode;
import com.castellanos.fuzzylogicgp.membershipfunction.FPG;
import com.castellanos.fuzzylogicgp.parser.DiscoveryQuery;
import com.castellanos.fuzzylogicgp.parser.DummGenerator;
import com.castellanos.fuzzylogicgp.parser.LogicType;
import com.castellanos.fuzzylogicgp.parser.Query;
import com.castellanos.fuzzylogicgp.parser.EvaluationQuery;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
       // evaluation();
       discovery();
    }

    private static void discovery() throws FileNotFoundException {
        DiscoveryQuery query = new DiscoveryQuery();
        StateNode fa = new StateNode("quality", "quality");
        fa.setMembershipFunction(new FPG("3.008904979613364", "4.911744699566296", "0.587451293429636"));

        FPG fpg = new FPG("9.132248919293149", "12.468564784808557", "0.24484459229131095");
        StateNode ca = new StateNode("fixed_acidity", "fixed_acidity", fpg);
        StateNode sa = new StateNode("alcohol", "alcohol");
        sa.setMembershipFunction(new FPG("9.087011333223336", "8.575778989810821", "0.3737520451234506"));
        StateNode ph = new StateNode("ph", "ph");
        // beta gamma m
        ArrayList<StateNode> states = new ArrayList<>();
        states.add(fa);
        states.add(ca);
        states.add(sa);
        states.add(ph);
        query.setStates(states);
        String db_uri = "src/main/resources/datasets/tinto.csv";
        query.setDb_uri(db_uri);
        String out_file = "result.out";
        query.setOut_file(out_file);
        query.setLogic(LogicType.GMBC);
        String predicate = "(IMP (NOT \"fixed_acidity\") (AND \"alcohol\" \"quality\"))";
        predicate = "*";
        DummGenerator generator = new DummGenerator();
        generator.setLabel("*");
        generator.setOperators(new NodeType[] { NodeType.AND, NodeType.OR, NodeType.IMP, NodeType.EQV, NodeType.NOT });
        ArrayList<DummGenerator> generators = new ArrayList<>();
        generators.add(generator);
        query.setGenerators(generators);
        query.setPredicate(predicate);
        query.setAdj_min_truth_value(0.1f);
        query.setAdj_num_pop(100);
        query.setDepth(2);
        query.setMut_percentage(0.5f);
        query.setNum_iter(100);
        query.setMin_truth_value(0.9f);
        query.setNum_pop(100);
        query.setNum_result(15);
        query.setAdj_num_iter(1);

        System.out.println(query);
        System.out.println(query.toJSON());
        DiscoveryQuery t = (DiscoveryQuery) Query.fromJson(Paths.get("discovery-script.txt"));
        System.out.println(t);
        System.out.println(t.equals(query));
    }

    private static void evaluation() throws FileNotFoundException {

        EvaluationQuery query = new EvaluationQuery();

        StateNode fa = new StateNode("quality", "quality");
        fa.setMembershipFunction(new FPG("3.008904979613364", "4.911744699566296", "0.587451293429636"));

        FPG fpg = new FPG("9.132248919293149", "12.468564784808557", "0.24484459229131095");
        StateNode ca = new StateNode("fixed_acidity", "fixed_acidity", fpg);
        StateNode sa = new StateNode("alcohol", "alcohol");
        sa.setMembershipFunction(new FPG("9.087011333223336", "8.575778989810821", "0.3737520451234506"));
        StateNode ph = new StateNode("ph", "ph");
        // beta gamma m
        ArrayList<StateNode> states = new ArrayList<>();
        states.add(fa);
        states.add(ca);
        states.add(sa);
        states.add(ph);
        query.setStates(states);
        String db_uri = "src/main/resources/datasets/tinto.csv";
        query.setDb_uri(db_uri);
        String out_file = "result.out";
        query.setOut_file(out_file);
        query.setLogic(LogicType.GMBC);
        String predicate = "(IMP (NOT \"fixed_acidity\") (AND \"alcohol\" \"quality\"))";
        query.setPredicate(predicate);
        System.out.println(query.toJSON());

        EvaluationQuery t = (EvaluationQuery) Query.fromJson(Paths.get("evaluation-script.txt"));
        System.out.println(t);
        System.out.println(t.equals(query));
    }

}