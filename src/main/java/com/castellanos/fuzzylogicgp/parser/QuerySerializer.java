package com.castellanos.fuzzylogicgp.parser;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class QuerySerializer implements JsonSerializer<Query> {

    @Override
    public JsonElement serialize(Query src, Type typeOfSrc, JsonSerializationContext context) {
        switch (src.getType()) {
            case DISCOVERY:
                return context.serialize((DiscoveryQuery) src);
            case EVALUATION:
                return context.serialize((EvaluationQuery) src);
            default:
                return null;
        }
    }

}