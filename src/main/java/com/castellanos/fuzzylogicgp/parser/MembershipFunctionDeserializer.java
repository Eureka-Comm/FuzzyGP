package com.castellanos.fuzzylogicgp.parser;

import java.lang.reflect.Type;

import com.castellanos.fuzzylogicgp.membershipfunction.AMembershipFunction;
import com.castellanos.fuzzylogicgp.membershipfunction.FPG;
import com.castellanos.fuzzylogicgp.membershipfunction.MapNominal;
import com.castellanos.fuzzylogicgp.membershipfunction.MembershipFunctionType;
import com.castellanos.fuzzylogicgp.membershipfunction.NSigmoid;
import com.castellanos.fuzzylogicgp.membershipfunction.Sigmoid;
import com.castellanos.fuzzylogicgp.membershipfunction.Singleton;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class MembershipFunctionDeserializer implements JsonDeserializer<AMembershipFunction> {

    @Override
    public AMembershipFunction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        MembershipFunctionType type = MembershipFunctionType.valueOf(json.getAsJsonObject().get("type").getAsString());
        switch (type) {
            case FPG:
                return context.deserialize(json, FPG.class);
            case NSIGMOID:
                return context.deserialize(json, NSigmoid.class);
            case SIGMOID:
                return context.deserialize(json, Sigmoid.class);
            case SINGLETON:
                return context.deserialize(json, Singleton.class);
            case MAPNOMIAL:
                return context.deserialize(json, MapNominal.class);
            default:
                return null;
        }
    }

}