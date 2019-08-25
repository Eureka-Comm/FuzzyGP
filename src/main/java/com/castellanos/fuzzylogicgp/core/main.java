/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.core;

import com.castellanos.fuzzylogicgp.base.Generator;
import com.castellanos.fuzzylogicgp.base.NodeType;
import com.castellanos.fuzzylogicgp.base.OperatorException;
import com.castellanos.fuzzylogicgp.base.Predicate;
import com.castellanos.fuzzylogicgp.base.State;
import com.castellanos.fuzzylogicgp.membershipfunction.FPG;
import com.castellanos.fuzzylogicgp.membershipfunction.NSigmoid;
import com.castellanos.fuzzylogicgp.membershipfunction.Sigmoid;
import com.castellanos.fuzzylogicgp.parser.ParserPredicate;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tech.tablesaw.api.Table;
import tech.tablesaw.io.Source;
import tech.tablesaw.io.xlsx.XlsxReader;
/**
 *
 * @author hp
 */
public class main {

    public static void main(String[] args) throws OperatorException, IOException {
        String st = "{\n"
                + "  \"nodes\": {\n"
                + "    \"437eea67-84d0-40af-9a4e-7179d665930a\": {\n"
                + "      \"label\": \"l3\",\n"
                + "      \"colName\": \"col3\",\n"
                + "      \"membershipFunction\": {\n"
                + "        \"center\": 0.4,\n"
                + "        \"beta\": 0.7,\n"
                + "        \"type\": \"SIGMOID\"\n"
                + "      },\n"
                + "      \"id\": \"437eea67-84d0-40af-9a4e-7179d665930a\",\n"
                + "      \"father\": \"65837858-2bdb-4f78-957e-12c7d7a2f6c1\",\n"
                + "      \"type\": \"STATE\",\n"
                + "      \"mutable\": false\n"
                + "    },\n"
                + "    \"026e9de3-ec2a-4e81-a852-afaf8612facd\": {\n"
                + "      \"label\": \"high_alcohol\",\n"
                + "      \"colName\": \"alcohol\",\n"
                + "      \"membershipFunction\": {\n"
                + "        \"center\": 0.8,\n"
                + "        \"beta\": 0.3,\n"
                + "        \"type\": \"NSIGMOID\"\n"
                + "      },\n"
                + "      \"id\": \"026e9de3-ec2a-4e81-a852-afaf8612facd\",\n"
                + "      \"father\": \"65837858-2bdb-4f78-957e-12c7d7a2f6c1\",\n"
                + "      \"type\": \"STATE\",\n"
                + "      \"mutable\": false\n"
                + "    },\n"
                + "    \"cb163287-46db-4e24-8931-add89a9744bd\": {\n"
                + "      \"childrens\": [\n"
                + "        {\n"
                + "          \"childrens\": [\n"
                + "            {\n"
                + "              \"label\": \"l1\",\n"
                + "              \"colName\": \"col1\",\n"
                + "              \"membershipFunction\": {\n"
                + "                \"type\": \"FPG\"\n"
                + "              },\n"
                + "              \"id\": \"4f174b67-d390-4057-91fa-dac58316e89e\",\n"
                + "              \"father\": \"65837858-2bdb-4f78-957e-12c7d7a2f6c1\",\n"
                + "              \"type\": \"STATE\",\n"
                + "              \"mutable\": false\n"
                + "            },\n"
                + "            {\n"
                + "              \"label\": \"l3\",\n"
                + "              \"colName\": \"col3\",\n"
                + "              \"membershipFunction\": {\n"
                + "                \"center\": 0.4,\n"
                + "                \"beta\": 0.7,\n"
                + "                \"type\": \"SIGMOID\"\n"
                + "              },\n"
                + "              \"id\": \"437eea67-84d0-40af-9a4e-7179d665930a\",\n"
                + "              \"father\": \"65837858-2bdb-4f78-957e-12c7d7a2f6c1\",\n"
                + "              \"type\": \"STATE\",\n"
                + "              \"mutable\": false\n"
                + "            },\n"
                + "            {\n"
                + "              \"label\": \"high_alcohol\",\n"
                + "              \"colName\": \"alcohol\",\n"
                + "              \"membershipFunction\": {\n"
                + "                \"center\": 0.8,\n"
                + "                \"beta\": 0.3,\n"
                + "                \"type\": \"NSIGMOID\"\n"
                + "              },\n"
                + "              \"id\": \"026e9de3-ec2a-4e81-a852-afaf8612facd\",\n"
                + "              \"father\": \"65837858-2bdb-4f78-957e-12c7d7a2f6c1\",\n"
                + "              \"type\": \"STATE\",\n"
                + "              \"mutable\": false\n"
                + "            }\n"
                + "          ],\n"
                + "          \"fitness\": 0.0,\n"
                + "          \"id\": \"65837858-2bdb-4f78-957e-12c7d7a2f6c1\",\n"
                + "          \"father\": \"cb163287-46db-4e24-8931-add89a9744bd\",\n"
                + "          \"type\": \"AND\",\n"
                + "          \"mutable\": false\n"
                + "        },\n"
                + "        {\n"
                + "          \"childrens\": [\n"
                + "            {\n"
                + "              \"label\": \"quality\",\n"
                + "              \"colName\": \"quality\",\n"
                + "              \"membershipFunction\": {\n"
                + "                \"type\": \"FPG\"\n"
                + "              },\n"
                + "              \"id\": \"dafc3b96-3939-4138-b99e-8189a78bc38a\",\n"
                + "              \"father\": \"5c348ba7-c998-4d2d-910d-cb884c7cb319\",\n"
                + "              \"type\": \"STATE\",\n"
                + "              \"mutable\": false\n"
                + "            }\n"
                + "          ],\n"
                + "          \"fitness\": 0.0,\n"
                + "          \"id\": \"5c348ba7-c998-4d2d-910d-cb884c7cb319\",\n"
                + "          \"father\": \"cb163287-46db-4e24-8931-add89a9744bd\",\n"
                + "          \"type\": \"NOT\",\n"
                + "          \"mutable\": false\n"
                + "        }\n"
                + "      ],\n"
                + "      \"fitness\": 0.0,\n"
                + "      \"id\": \"cb163287-46db-4e24-8931-add89a9744bd\",\n"
                + "      \"type\": \"IMP\",\n"
                + "      \"mutable\": false\n"
                + "    },\n"
                + "    \"dafc3b96-3939-4138-b99e-8189a78bc38a\": {\n"
                + "      \"label\": \"quality\",\n"
                + "      \"colName\": \"quality\",\n"
                + "      \"membershipFunction\": {\n"
                + "        \"type\": \"FPG\"\n"
                + "      },\n"
                + "      \"id\": \"dafc3b96-3939-4138-b99e-8189a78bc38a\",\n"
                + "      \"father\": \"5c348ba7-c998-4d2d-910d-cb884c7cb319\",\n"
                + "      \"type\": \"STATE\",\n"
                + "      \"mutable\": false\n"
                + "    },\n"
                + "    \"5c348ba7-c998-4d2d-910d-cb884c7cb319\": {\n"
                + "      \"childrens\": [\n"
                + "        {\n"
                + "          \"label\": \"quality\",\n"
                + "          \"colName\": \"quality\",\n"
                + "          \"membershipFunction\": {\n"
                + "            \"type\": \"FPG\"\n"
                + "          },\n"
                + "          \"id\": \"dafc3b96-3939-4138-b99e-8189a78bc38a\",\n"
                + "          \"father\": \"5c348ba7-c998-4d2d-910d-cb884c7cb319\",\n"
                + "          \"type\": \"STATE\",\n"
                + "          \"mutable\": false\n"
                + "        }\n"
                + "      ],\n"
                + "      \"fitness\": 0.0,\n"
                + "      \"id\": \"5c348ba7-c998-4d2d-910d-cb884c7cb319\",\n"
                + "      \"father\": \"cb163287-46db-4e24-8931-add89a9744bd\",\n"
                + "      \"type\": \"NOT\",\n"
                + "      \"mutable\": false\n"
                + "    },\n"
                + "    \"4f174b67-d390-4057-91fa-dac58316e89e\": {\n"
                + "      \"label\": \"l1\",\n"
                + "      \"colName\": \"col1\",\n"
                + "      \"membershipFunction\": {\n"
                + "        \"type\": \"FPG\"\n"
                + "      },\n"
                + "      \"id\": \"4f174b67-d390-4057-91fa-dac58316e89e\",\n"
                + "      \"father\": \"65837858-2bdb-4f78-957e-12c7d7a2f6c1\",\n"
                + "      \"type\": \"STATE\",\n"
                + "      \"mutable\": false\n"
                + "    },\n"
                + "    \"65837858-2bdb-4f78-957e-12c7d7a2f6c1\": {\n"
                + "      \"childrens\": [\n"
                + "        {\n"
                + "          \"label\": \"l1\",\n"
                + "          \"colName\": \"col1\",\n"
                + "          \"membershipFunction\": {\n"
                + "            \"type\": \"FPG\"\n"
                + "          },\n"
                + "          \"id\": \"4f174b67-d390-4057-91fa-dac58316e89e\",\n"
                + "          \"father\": \"65837858-2bdb-4f78-957e-12c7d7a2f6c1\",\n"
                + "          \"type\": \"STATE\",\n"
                + "          \"mutable\": false\n"
                + "        },\n"
                + "        {\n"
                + "          \"label\": \"l3\",\n"
                + "          \"colName\": \"col3\",\n"
                + "          \"membershipFunction\": {\n"
                + "            \"center\": 0.4,\n"
                + "            \"beta\": 0.7,\n"
                + "            \"type\": \"SIGMOID\"\n"
                + "          },\n"
                + "          \"id\": \"437eea67-84d0-40af-9a4e-7179d665930a\",\n"
                + "          \"father\": \"65837858-2bdb-4f78-957e-12c7d7a2f6c1\",\n"
                + "          \"type\": \"STATE\",\n"
                + "          \"mutable\": false\n"
                + "        },\n"
                + "        {\n"
                + "          \"label\": \"high_alcohol\",\n"
                + "          \"colName\": \"alcohol\",\n"
                + "          \"membershipFunction\": {\n"
                + "            \"center\": 0.8,\n"
                + "            \"beta\": 0.3,\n"
                + "            \"type\": \"NSIGMOID\"\n"
                + "          },\n"
                + "          \"id\": \"026e9de3-ec2a-4e81-a852-afaf8612facd\",\n"
                + "          \"father\": \"65837858-2bdb-4f78-957e-12c7d7a2f6c1\",\n"
                + "          \"type\": \"STATE\",\n"
                + "          \"mutable\": false\n"
                + "        }\n"
                + "      ],\n"
                + "      \"fitness\": 0.0,\n"
                + "      \"id\": \"65837858-2bdb-4f78-957e-12c7d7a2f6c1\",\n"
                + "      \"father\": \"cb163287-46db-4e24-8931-add89a9744bd\",\n"
                + "      \"type\": \"AND\",\n"
                + "      \"mutable\": false\n"
                + "    }\n"
                + "  },\n"
                + "  \"idFather\": \"cb163287-46db-4e24-8931-add89a9744bd\"\n"
                + "}";
        State s1 = new State("l1", "col1", new FPG());
        State s2 = new State("l3", "col3", new Sigmoid(0.4, .7));
        State ha = new State("high_alcohol", "alcohol", new NSigmoid(.8, .3));
        State s = new State("sugar", "sugar", new FPG());
        State q = new State("quality", "quality", new FPG());
        State ph = new State("ph", "ph", new FPG());
        List<State> states = new ArrayList<>();
        states.add(s1);
        states.add(s2);
        states.add(ha);
        states.add(s);
        states.add(s);
        states.add(q);
        states.add(ph);
        st = "(IMP (AND \"l1\" \"l3\" \"high_alcohol\" \"comodin\") (NOT \"quality\" ))";
        /*
        Imp imp = new Imp();
        And and = new And();
        imp.addChild(and);
        and.setFather(imp.getId());
        and.addChild(s1);
        s1.setFather(and.getId());
        and.addChild(s2);
        s2.setFather(and.getId());
        and.addChild(ha);
        ha.setFather(and.getId());
        Not not = new Not();
        imp.addChild(not);
        not.setFather(imp.getId());
        not.addChild(q);
        q.setFather(not.getId());
        Predicate p = new Predicate();
        p.addAllNode(imp);
        System.out.println(p.toJson());
        System.out.println(p);
         */
        List<String> vars = new ArrayList<>();
        vars.add(ph.getLabel());
        vars.add(s.getLabel());

        Generator g = new Generator("comodin", new NodeType[]{}, vars);
        List<Generator> gs = new ArrayList<>();
        gs.add(g);
        /*ParserPredicate p = new ParserPredicate(st, states, gs);
        Predicate pp = p.parser();
        System.out.println(pp.toJson());
        System.out.println(pp);*/
        Table t = Table.read().file("src/main/resources/datasets/tinto.csv");
        System.out.println(t);
        System.out.println(t.columnNames());
        System.out.println(t.structure());
    }
}
