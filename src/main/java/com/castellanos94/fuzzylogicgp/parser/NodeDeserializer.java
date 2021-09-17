package com.castellanos94.fuzzylogicgp.parser;

import java.lang.reflect.Type;

import com.castellanos94.fuzzylogicgp.base.Node;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class NodeDeserializer implements JsonDeserializer<Node> {

    @Override
    public Node deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
                String str = json.getAsJsonObject().get("type").getAsString().toLowerCase();

        return null;
    }
    
    
}
