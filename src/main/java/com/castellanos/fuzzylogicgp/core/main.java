/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.core;

import com.castellanos.fuzzylogicgp.base.GeneratorNode;
import com.castellanos.fuzzylogicgp.base.NodeType;
import com.castellanos.fuzzylogicgp.base.OperatorException;
import com.castellanos.fuzzylogicgp.base.Predicate;
import com.castellanos.fuzzylogicgp.base.StateNode;
import com.castellanos.fuzzylogicgp.logic.AMBC;
import com.castellanos.fuzzylogicgp.membershipfunction.FPG;
import com.castellanos.fuzzylogicgp.membershipfunction.NSigmoid;
import com.castellanos.fuzzylogicgp.membershipfunction.Sigmoid;
import com.castellanos.fuzzylogicgp.parser.ParserPredicate;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tech.tablesaw.api.Table;
/**
 *
 * @author hp
 */
public class main {

    public static void main(String[] args) throws OperatorException, IOException, CloneNotSupportedException {
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
      
        StateNode q = new StateNode("quality", "quality");
        StateNode ph = new StateNode("ph","pH");
        StateNode sugar = new StateNode("sugar","residual_sugar");
        
        
        st = "(AND \"ph\" \"quality\" )";
        List<StateNode> states = new ArrayList<>();
        states.add(q);
        states.add(ph);
        states.add(sugar);
        
        List<String> vars = new ArrayList<>();
        
        GeneratorNode g = new GeneratorNode("comodin", new NodeType[]{}, vars);
        List<GeneratorNode> gs = new ArrayList<>();
        gs.add(g);
        ParserPredicate p = new ParserPredicate(st, states, gs);
        Predicate pp = p.parser();
        System.out.println(pp.toJson());
        System.out.println(pp);
        Table t = Table.read().file("src/main/resources/datasets/tinto.csv");
        
        //(Table data, ALogic logic, float mut_percentage, int adj_num_pop, int adj_iter, float adj_truth_value) 

        GOMF gomf = new GOMF(t, new AMBC(), 0.5f , 1, 2, 0.8f);
        gomf.optimize(pp);
    }
}
