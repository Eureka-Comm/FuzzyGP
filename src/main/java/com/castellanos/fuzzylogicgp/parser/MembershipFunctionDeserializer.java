package com.castellanos.fuzzylogicgp.parser;

import java.lang.reflect.Type;

import com.castellanos.fuzzylogicgp.membershipfunction.MembershipFunction;
import com.castellanos.fuzzylogicgp.membershipfunction.FPG_MF;
import com.castellanos.fuzzylogicgp.membershipfunction.Gaussian_MF;
import com.castellanos.fuzzylogicgp.membershipfunction.MapNominal_MF;
import com.castellanos.fuzzylogicgp.membershipfunction.MembershipFunctionType;
import com.castellanos.fuzzylogicgp.membershipfunction.NSigmoid_MF;
import com.castellanos.fuzzylogicgp.membershipfunction.Nominal_MF;
import com.castellanos.fuzzylogicgp.membershipfunction.SForm_MF;
import com.castellanos.fuzzylogicgp.membershipfunction.Sigmoid_MF;
import com.castellanos.fuzzylogicgp.membershipfunction.Singleton_MF;
import com.castellanos.fuzzylogicgp.membershipfunction.Trapezoidal_MF;
import com.castellanos.fuzzylogicgp.membershipfunction.Triangular_MF;
import com.castellanos.fuzzylogicgp.membershipfunction.ZForm_MF;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class MembershipFunctionDeserializer implements JsonDeserializer<MembershipFunction> {

    @Override
    public MembershipFunction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        MembershipFunctionType type = MembershipFunctionType.valueOf(json.getAsJsonObject().get("type").getAsString());
        switch (type) {
            case FPG:
                return context.deserialize(json, FPG_MF.class);
            case NSIGMOID:
                return context.deserialize(json, NSigmoid_MF.class);
            case SIGMOID:
                return context.deserialize(json, Sigmoid_MF.class);
            case SINGLETON:
                return context.deserialize(json, Singleton_MF.class);
            case MAPNOMIAL:
                return context.deserialize(json, MapNominal_MF.class);
            case GAUSSIAN:
                return context.deserialize(json, Gaussian_MF.class);
            case SFORM:
                return context.deserialize(json, SForm_MF.class);
            case ZFORM:
                return context.deserialize(json, ZForm_MF.class);
            case TRAPEZOIDAL:
                return context.deserialize(json, Trapezoidal_MF.class);
            case TRIANGULAR:
                return context.deserialize(json, Triangular_MF.class);
            case NOMINAL:
            return context.deserialize(json, Nominal_MF.class);
            default:
                return null;
        }
    }

}