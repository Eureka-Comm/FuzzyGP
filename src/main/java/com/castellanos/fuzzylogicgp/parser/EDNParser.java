/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.parser;

import com.castellanos.fuzzylogicgp.base.GeneratorNode;
import com.castellanos.fuzzylogicgp.base.NodeType;
import com.castellanos.fuzzylogicgp.base.StateNode;
import com.castellanos.fuzzylogicgp.logic.LogicType;
import com.castellanos.fuzzylogicgp.membershipfunction.FPG;
import com.castellanos.fuzzylogicgp.membershipfunction.Gamma;
import com.castellanos.fuzzylogicgp.membershipfunction.Gaussian;
import com.castellanos.fuzzylogicgp.membershipfunction.LGamma;
import com.castellanos.fuzzylogicgp.membershipfunction.LTrapezoidal;
import com.castellanos.fuzzylogicgp.membershipfunction.MapNominal;
import com.castellanos.fuzzylogicgp.membershipfunction.NSigmoid;
import com.castellanos.fuzzylogicgp.membershipfunction.PSeudoExp;
import com.castellanos.fuzzylogicgp.membershipfunction.RTrapezoidal;
import com.castellanos.fuzzylogicgp.membershipfunction.SForm;
import com.castellanos.fuzzylogicgp.membershipfunction.Sigmoid;
import com.castellanos.fuzzylogicgp.membershipfunction.Trapezoidal;
import com.castellanos.fuzzylogicgp.membershipfunction.Triangular;
import com.castellanos.fuzzylogicgp.membershipfunction.ZForm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    private File file;

    public EDNParser(String path) throws FileNotFoundException {
        file = new File(path);
        pbr = Parsers.newParseable(new FileReader(file));
    }

    @SuppressWarnings("rawtypes")
    public Query parser() throws IOException {
        Parser p = Parsers.newParser(Parsers.defaultConfiguration());
        Map<?, ?> map = (Map<?, ?>) p.nextValue(pbr);

        Keyword jobKey = Keyword.newKeyword("job"), queryKey = Keyword.newKeyword("query"),
                dbKey = Keyword.newKeyword("db-uri"), dbOut = Keyword.newKeyword("out-file"),
                statesKey = Keyword.newKeyword("states"), logicKey = Keyword.newKeyword("logic");
        Map<?, ?> queryMap = (Map<?, ?>) map.get(queryKey);
        String path = (String) queryMap.get(dbKey);
        String out = (String) queryMap.get(dbOut);
        List<StateNode> convertToState = convertToState((Collection) queryMap.get(statesKey));
        String logicSt = queryMap.get(logicKey).toString();
        switch (map.get(jobKey).toString()) {
            case ":evaluation":
                Query query = new EvaluationQuery();
                query.setDb_uri(path);
                query.setOut_file(out);
                query.setStates(new ArrayList<>(convertToState));
                query.setLogic(
                        LogicType.valueOf(logicSt.replace(":", "").replace("[", "").replace("]", "").toUpperCase()));
                query.setPredicate(findPredicate());
                return query;
            case ":discovery":

                DiscoveryQuery discoveryQuery = new DiscoveryQuery();
                discoveryQuery.setDb_uri(path);
                discoveryQuery.setOut_file(out);
                discoveryQuery.setStates(new ArrayList<>(convertToState));
                discoveryQuery.setLogic(LogicType.valueOf(logicSt.replace(":", "").replace("[", "").replace("]", "")));
                discoveryQuery.setPredicate(
                        findPredicate().replaceAll(":generator", "").replace("{", "").replace("}", "").trim());
                // discoveryQuery.setDepth(Integer.parseInt(queryMap.get(Keyword.newKeyword("depth")).toString().trim()));
                discoveryQuery
                        .setNum_pop(Integer.parseInt(queryMap.get(Keyword.newKeyword("num-pop")).toString().trim()));
                discoveryQuery
                        .setNum_iter(Integer.parseInt(queryMap.get(Keyword.newKeyword("num-iter")).toString().trim()));
                discoveryQuery.setNum_result(
                        Integer.parseInt(queryMap.get(Keyword.newKeyword("num-result")).toString().trim()));
                discoveryQuery.setMin_truth_value(
                        Float.parseFloat(queryMap.get(Keyword.newKeyword("min-truth-value")).toString().trim()));
                discoveryQuery.setMut_percentage(
                        Float.parseFloat(queryMap.get(Keyword.newKeyword("mut-percentage")).toString().trim()));
                discoveryQuery.setAdj_num_pop(
                        Integer.parseInt(queryMap.get(Keyword.newKeyword("adj-num-pop")).toString().trim()));
                discoveryQuery.setAdj_num_iter(
                        Integer.parseInt(queryMap.get(Keyword.newKeyword("adj-num-iter")).toString().trim()));
                discoveryQuery.setAdj_min_truth_value(
                        Float.parseFloat(queryMap.get(Keyword.newKeyword("adj-min-truth-value")).toString().trim()));
                discoveryQuery.setGenerators(
                        convertToGenerator((Map<?, ?>) queryMap.get(Keyword.newKeyword("generator")), convertToState));
                for (GeneratorNode g : discoveryQuery.getGenerators()) {
                    g.setDepth(Integer.parseInt(queryMap.get(Keyword.newKeyword("depth")).toString().trim()));
                }
                return discoveryQuery;
            default:
                throw new IllegalArgumentException("Unsupported query.");
        }
    }

    private ArrayList<GeneratorNode> convertToGenerator(Map<?, ?> gen, List<StateNode> stateNodes) {
        ArrayList<GeneratorNode> lst = new ArrayList<>();
        GeneratorNode geneNode = new GeneratorNode();
        String[] opeStrings = gen.get(Keyword.newKeyword("operators")).toString().replace("[", "").replace("]", "")
                .split(",");
        NodeType[] oNodeTypes = new NodeType[opeStrings.length];
        for (int i = 0; i < oNodeTypes.length; i++) {
            oNodeTypes[i] = NodeType.valueOf(opeStrings[i].trim());
        }

        geneNode.setOperators(oNodeTypes);
        String[] vars = gen.get(Keyword.newKeyword("variables")).toString().replace("[", "").replace("]", "")
                .split(",");
        ArrayList<String> lstvar = new ArrayList<>();
        for (String string : vars) {
            for (StateNode stateNode : stateNodes) {
                if (string.trim().equals(stateNode.getLabel().trim())) {
                    lstvar.add(stateNode.getLabel().trim());
                }
            }
        }
        geneNode.setVariables(lstvar);
        String predicaString = gen.get(Keyword.newKeyword("predicate")).toString().replaceAll("\\[", "")
                .replaceAll("\\]", "").replaceAll(",", "");
        // System.out.println(predicaString);
        for (StateNode stateNode : stateNodes) {
            if (predicaString.contains(stateNode.getLabel())) {
                predicaString = predicaString.replaceAll(stateNode.getLabel(), "");
            }
        }
        for (NodeType type : new NodeType[] { NodeType.AND, NodeType.EQV, NodeType.NOT, NodeType.IMP, NodeType.OR }) {
            if (predicaString.contains(type.toString())) {
                predicaString = predicaString.replaceAll(type.toString(), "");
            }
        }
        String tmp[] = predicaString.trim().split(" ");
        geneNode.setLabel(tmp[0]);
        lst.add(geneNode);
        return lst;
    }

    private String findPredicate() throws IOException {
        List<String> readAllLines = Files.readAllLines(Paths.get(file.getAbsolutePath()));
        Keyword predicateKey = Keyword.newKeyword("predicate");

        for (String string : readAllLines) {
            if (string.contains(predicateKey.toString())) {
                return string.replace(predicateKey.toString(), "").trim();
            }
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    private List<StateNode> convertToState(Collection cstates) {
        List<StateNode> states = new ArrayList<>();
        Keyword labelKey = Keyword.newKeyword("label"), colNameKey = Keyword.newKeyword("colname"),
                fKey = Keyword.newKeyword("f");
        for (Iterator it = cstates.iterator(); it.hasNext();) {
            Map<?, ?> cstate = (Map<?, ?>) it.next();
            // System.out.println(+" "+cstate.get(fKey));
            StateNode sn = new StateNode(cstate.get(labelKey).toString().trim(),
                    cstate.get(colNameKey).toString().trim());
            String[] split = cstate.get(fKey).toString().replaceAll("\\[", "").replaceAll("]", "").split(",");
            switch (split[0].trim().toLowerCase()) {
                case "sigmoid":
                    sn.setMembershipFunction(new Sigmoid(split[1], split[2]));
                    break;
                case "-sigmoid":
                    sn.setMembershipFunction(new NSigmoid(split[1], split[2]));
                    break;
                case "fpg":
                    if (split.length > 3) {
                        double a = Double.parseDouble(split[1]), b = Double.parseDouble(split[2]),
                                m = Double.parseDouble(split[3]);
                        sn.setMembershipFunction(new FPG(Math.min(a, b), Math.max(a, b), m));
                    }
                    break;
                case "map-nominal":
                    String fm = cstate.get(fKey).toString().replaceAll("\\[", "").replaceAll("]", "");
                    String values[] = fm.substring(fm.indexOf("{"), fm.indexOf("}")).replaceAll("\"", "")
                            .replace("{", "").replace("}", "").split(",");
                    MapNominal map = new MapNominal();
                    for (String string : values) {
                        String k[] = string.trim().split(" ");
                        map.addParameter(k[0], Double.parseDouble(k[1]));
                    }
                    map.setNotFoundValue(Double.parseDouble(fm.substring(fm.indexOf("")).replace("{", "").trim()));
                    break;
                case "trapezoidal":
                    sn.setMembershipFunction(new Trapezoidal(split[1], split[2], split[3], split[4]));
                    break;
                case "triangular":
                    sn.setMembershipFunction(new Triangular(split[1], split[2], split[3]));
                    break;
                case "gaussian":
                    sn.setMembershipFunction(new Gaussian(split[1], split[2]));
                    break;
                case "sform":
                    sn.setMembershipFunction(new SForm(split[1], split[2]));
                    break;
                case "zform":
                    sn.setMembershipFunction(new ZForm(split[1], split[2]));
                    break;
                case "lgamma":
                    sn.setMembershipFunction(new LGamma(split[1], split[2]));
                    break;
                case "gamma":
                    sn.setMembershipFunction(new Gamma(split[1], split[2]));
                    break;
                case "ltrapezoidal":
                    sn.setMembershipFunction(new LTrapezoidal(split[1], split[2]));
                    break;
                case "rtrapezoidal":
                    sn.setMembershipFunction(new RTrapezoidal(split[1], split[2]));
                    break;
                case "pseudo-exp":
                    sn.setMembershipFunction(new PSeudoExp(split[1], split[2]));
                    break;
                default:
                    System.out.println("Unsupported : " + split[0]);

            }
            states.add(sn);
        }
        return states;
    }
}
