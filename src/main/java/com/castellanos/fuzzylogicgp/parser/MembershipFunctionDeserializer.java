package com.castellanos.fuzzylogicgp.parser;

import java.lang.reflect.Type;

import com.castellanos.fuzzylogicgp.membershipfunction.FPG;
import com.castellanos.fuzzylogicgp.membershipfunction.Gamma;
import com.castellanos.fuzzylogicgp.membershipfunction.Gaussian;
import com.castellanos.fuzzylogicgp.membershipfunction.LGamma;
import com.castellanos.fuzzylogicgp.membershipfunction.LTrapezoidal;
import com.castellanos.fuzzylogicgp.membershipfunction.MapNominal;
import com.castellanos.fuzzylogicgp.membershipfunction.MembershipFunction;
import com.castellanos.fuzzylogicgp.membershipfunction.MembershipFunctionType;
import com.castellanos.fuzzylogicgp.membershipfunction.NSigmoid;
import com.castellanos.fuzzylogicgp.membershipfunction.Nominal;
import com.castellanos.fuzzylogicgp.membershipfunction.PSeudoExp;
import com.castellanos.fuzzylogicgp.membershipfunction.RTrapezoidal;
import com.castellanos.fuzzylogicgp.membershipfunction.SForm;
import com.castellanos.fuzzylogicgp.membershipfunction.Sigmoid;
import com.castellanos.fuzzylogicgp.membershipfunction.Singleton;
import com.castellanos.fuzzylogicgp.membershipfunction.Trapezoidal;
import com.castellanos.fuzzylogicgp.membershipfunction.Triangular;
import com.castellanos.fuzzylogicgp.membershipfunction.ZForm;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class MembershipFunctionDeserializer implements JsonDeserializer<MembershipFunction> {

    @Override
    public MembershipFunction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        MembershipFunctionType type = MembershipFunctionType.valueOf(json.getAsJsonObject().get("type").getAsString().toUpperCase());
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
            case GAUSSIAN:
                return context.deserialize(json, Gaussian.class);
            case SFORM:
                return context.deserialize(json, SForm.class);
            case ZFORM:
                return context.deserialize(json, ZForm.class);
            case TRAPEZOIDAL:
                return context.deserialize(json, Trapezoidal.class);
            case TRIANGULAR:
                return context.deserialize(json, Triangular.class);
            case NOMINAL:
                return context.deserialize(json, Nominal.class);
            case GAMMA:
                return context.deserialize(json, Gamma.class);
            case LGAMMA:
                return context.deserialize(json, LGamma.class);
            case LTRAPEZOIDAL:
                return context.deserialize(json, LTrapezoidal.class);
            case RTRAPEZOIDAL:
                return context.deserialize(json, RTrapezoidal.class);
            case PSEUDOEXP:
                return context.deserialize(json, PSeudoExp.class);
            default:
                return null;
        }
    }

}