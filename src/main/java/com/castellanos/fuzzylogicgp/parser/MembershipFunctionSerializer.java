package com.castellanos.fuzzylogicgp.parser;

import java.lang.reflect.Type;

import com.castellanos.fuzzylogicgp.membershipfunction.AMembershipFunction;
import com.castellanos.fuzzylogicgp.membershipfunction.FPG;
import com.castellanos.fuzzylogicgp.membershipfunction.NSigmoid;
import com.castellanos.fuzzylogicgp.membershipfunction.Sigmoid;
import com.castellanos.fuzzylogicgp.membershipfunction.Singleton;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class MembershipFunctionSerializer implements JsonSerializer<AMembershipFunction> {

    @Override
    public JsonElement serialize(AMembershipFunction src, Type typeOfSrc, JsonSerializationContext context) {
        switch (src.getType()) {
            case FPG:
                return context.serialize((FPG) src);
            case NSIGMOID:
                return context.serialize((NSigmoid) src);
            case SIGMOID:
                return context.serialize((Sigmoid) src);
            case SINGLETON:
                return context.serialize((Singleton) src);
            default:
                return null;
        }
    }

}