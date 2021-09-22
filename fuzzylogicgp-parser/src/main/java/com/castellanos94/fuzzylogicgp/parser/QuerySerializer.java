package com.castellanos94.fuzzylogicgp.parser;

import com.castellanos94.fuzzylogicgp.core.DiscoveryQuery;
import com.castellanos94.fuzzylogicgp.core.EvaluationQuery;
import com.castellanos94.fuzzylogicgp.core.Query;
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