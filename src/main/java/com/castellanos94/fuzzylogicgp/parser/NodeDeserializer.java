package com.castellanos94.fuzzylogicgp.parser;

import java.lang.reflect.Type;

import com.castellanos94.fuzzylogicgp.base.GeneratorNode;
import com.castellanos94.fuzzylogicgp.base.Node;
import com.castellanos94.fuzzylogicgp.base.NodeTree;
import com.castellanos94.fuzzylogicgp.base.NodeType;
import com.castellanos94.fuzzylogicgp.base.StateNode;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class NodeDeserializer implements JsonDeserializer<Node> {

    @Override
    public Node deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        String str = json.getAsJsonObject().get("type").getAsString().toUpperCase().trim();
        NodeType type = NodeType.valueOf(str);
        switch (type) {
            case STATE:
                return context.deserialize(json, StateNode.class);
            case OPERATOR:
                return context.deserialize(json, GeneratorNode.class);
            case DUMMYGENERATOR:
                return context.deserialize(json, DummyGenerator.class);
            default:
                return context.deserialize(json, NodeTree.class);

        }
    }

}
