package com.castellanos.fuzzylogicgp.core;

import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.castellanos.fuzzylogicgp.base.StateNode;
import com.castellanos.fuzzylogicgp.logic.ALogic;
import com.castellanos.fuzzylogicgp.logic.GMBC;
import com.castellanos.fuzzylogicgp.membershipfunction.FPG;
import com.castellanos.fuzzylogicgp.parser.DummyState;
import com.castellanos.fuzzylogicgp.parser.LogicType;
import com.castellanos.fuzzylogicgp.parser.Query;
import com.castellanos.fuzzylogicgp.parser.Task;
import com.castellanos.fuzzylogicgp.parser.TaskType;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        Task task = new Task();
        task.setJob(TaskType.EVALUATION);
        Query query = new Query();

        DummyState fa = new DummyState("quality", "quality");
        fa.setF(new FPG("3.008904979613364", "4.911744699566296", "0.587451293429636"));

        FPG fpg = new FPG("9.132248919293149", "12.468564784808557", "0.24484459229131095");
        DummyState ca = new DummyState("fixed_acidity", "fixed_acidity", fpg);
        DummyState sa = new DummyState("alcohol", "alcohol");
        sa.setF(new FPG("9.087011333223336", "8.575778989810821", "0.3737520451234506"));
        // beta gamma m
        ArrayList<DummyState> states = new ArrayList<>();
        states.add(fa);
        states.add(ca);
        states.add(sa);
        query.setStates(states);
        String db_uri = "src/main/resources/datasets/tinto.csv";
        query.setDb_uri(db_uri);
        String out_file = "result.out";
        query.setOut_file(out_file);
        query.setLogic(LogicType.GMBC);
        String predicate = "(IMP (NOT \"fixed_acidity\") (AND \"alcohol\" \"quality\"))";
        query.setPredicate(predicate);
        task.setQuery(query);
        System.out.println(task.toJSON());

        Task t = Task.fromJson(Paths.get("evaluation-script.txt"));
        System.out.println(t.getJob());
    }

}