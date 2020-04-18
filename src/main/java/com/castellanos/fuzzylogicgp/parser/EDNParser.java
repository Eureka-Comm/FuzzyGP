/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.parser;

import com.castellanos.fuzzylogicgp.base.StateNode;
import com.castellanos.fuzzylogicgp.membershipfunction.FPG;
import com.castellanos.fuzzylogicgp.membershipfunction.NSigmoid;
import com.castellanos.fuzzylogicgp.membershipfunction.Sigmoid;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import us.bpsm.edn.Keyword;
import us.bpsm.edn.parser.Parseable;
import us.bpsm.edn.parser.Parser;
import us.bpsm.edn.parser.Parsers;

/**
 *
 * @author hp
 */
public class EDNParser {

    private Parseable pbr;

    public EDNParser(String path) throws FileNotFoundException {
        pbr = Parsers.newParseable(new FileReader(new File(path)));
    }

    public void parser() {
        Parser p = Parsers.newParser(Parsers.defaultConfiguration());
        Map<?, ?> map = (Map<?, ?>) p.nextValue(pbr);

        Keyword jobKey = Keyword.newKeyword("job"),
                queryKey = Keyword.newKeyword("query"),
                dbKey = Keyword.newKeyword("db-uri"),
                dbOut = Keyword.newKeyword("out-file"),
                statesKey = Keyword.newKeyword("states"),
                logicKey = Keyword.newKeyword("logic"),
                predicateKey = Keyword.newKeyword("predicate");

        System.out.println(map.get(jobKey));
        Map<?, ?> queryMap = (Map<?, ?>) map.get(queryKey);
        String path = (String) queryMap.get(dbKey);
        String out = (String) queryMap.get(dbOut);
        List<StateNode> convertToState = convertToState((Collection) queryMap.get(statesKey));
        System.out.println(convertToState);
        String logicSt = queryMap.get(logicKey).toString();
        String pst = queryMap.get(predicateKey).toString();
        System.out.println(queryMap.get(predicateKey).getClass());

    }

    private List<StateNode> convertToState(Collection cstates) {
        List<StateNode> states = new ArrayList<>();
        Keyword labelKey = Keyword.newKeyword("label"),
                colNameKey = Keyword.newKeyword("colname"),
                fKey = Keyword.newKeyword("f");
        for (Iterator it = cstates.iterator(); it.hasNext();) {
            Map<?, ?> cstate = (Map<?, ?>) it.next();
            //System.out.println(+" "+cstate.get(fKey));
            StateNode sn = new StateNode(cstate.get(labelKey).toString(), cstate.get(colNameKey).toString());
            String[] split = cstate.get(fKey).toString().replaceAll("\\[", "").replaceAll("]", "").split(",");
            switch (split[0]) {
                case "sigmoid":
                    sn.setMembershipFunction(new Sigmoid(split[1],split[2]));
                    break;
                case "-sigmoid":
                    sn.setMembershipFunction(new NSigmoid(split[1],split[2]));
                    break;
                case "FPG":
                    if(split.length>3)
                    sn.setMembershipFunction(new FPG(split[1], split[2],split[3]));
                    break;
                default:
                    System.out.println(split[0]);

            }
            states.add(sn);
        }
        return states;
    }

    public static void main(String[] args) throws FileNotFoundException {
        String path = "/home/hp/Dropbox/ITCM/servicio social/universe-cmd/universe-cmd for Mac OS X/eval-fpg.txt";
        path = "/home/hp/Dropbox/ITCM/servicio social/universe-cmd/universe-cmd for Mac OS X/examples/evaluateEjemploFun.txt";
          path = "/home/hp/Dropbox/ITCM/servicio social/universe-cmd/universe-cmd for Mac OS X/evaluate.txt";
        EDNParser ednp = new EDNParser(path);
        ednp.parser();

    }
}
