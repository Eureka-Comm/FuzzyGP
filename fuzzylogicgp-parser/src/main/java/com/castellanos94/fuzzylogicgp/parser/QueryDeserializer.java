package com.castellanos94.fuzzylogicgp.parser;

import com.castellanos94.fuzzylogicgp.core.DiscoveryQuery;
import com.castellanos94.fuzzylogicgp.core.EvaluationQuery;
import com.castellanos94.fuzzylogicgp.core.Query;
import com.castellanos94.fuzzylogicgp.core.TaskType;
import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class QueryDeserializer implements JsonDeserializer<Query> {

    @Override
    public Query deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        TaskType type = TaskType.valueOf(json.getAsJsonObject().get("type").getAsString());
        switch (type) {
            case EVALUATION:
                return context.deserialize(json, EvaluationQuery.class);
            case DISCOVERY:
                return context.deserialize(json, DiscoveryQuery.class);
            default:
                return null;
        }
    }

}