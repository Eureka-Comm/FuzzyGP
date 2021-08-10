package com.castellanos94.fuzzylogicgp.parser;

import java.lang.reflect.Type;

import com.castellanos94.fuzzylogicgp.membershipfunction.FPG;
import com.castellanos94.fuzzylogicgp.membershipfunction.Gamma;
import com.castellanos94.fuzzylogicgp.membershipfunction.Gaussian;
import com.castellanos94.fuzzylogicgp.membershipfunction.LGamma;
import com.castellanos94.fuzzylogicgp.membershipfunction.LTrapezoidal;
import com.castellanos94.fuzzylogicgp.membershipfunction.MapNominal;
import com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunction;
import com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunctionType;
import com.castellanos94.fuzzylogicgp.membershipfunction.NSigmoid;
import com.castellanos94.fuzzylogicgp.membershipfunction.Nominal;
import com.castellanos94.fuzzylogicgp.membershipfunction.PSeudoExp;
import com.castellanos94.fuzzylogicgp.membershipfunction.RTrapezoidal;
import com.castellanos94.fuzzylogicgp.membershipfunction.SForm;
import com.castellanos94.fuzzylogicgp.membershipfunction.Sigmoid;
import com.castellanos94.fuzzylogicgp.membershipfunction.Singleton;
import com.castellanos94.fuzzylogicgp.membershipfunction.Trapezoidal;
import com.castellanos94.fuzzylogicgp.membershipfunction.Triangular;
import com.castellanos94.fuzzylogicgp.membershipfunction.ZForm;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class MembershipFunctionDeserializer implements JsonDeserializer<MembershipFunction> {

    @Override
    public MembershipFunction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        String str = json.getAsJsonObject().get("type").getAsString().toLowerCase();
        MembershipFunctionType type;
        switch (str) {
            case "fpg":
                type = MembershipFunctionType.FPG;
                break;
            case "sigmoid":
                type = MembershipFunctionType.SIGMOID;
                break;
            case "-sigmoid":
                type = MembershipFunctionType.NSIGMOID;
                break;
            case "singleton":
                type = MembershipFunctionType.SINGLETON;
                break;
            case "map-nominal":
                type = MembershipFunctionType.MAPNOMIAL;
                break;
            case "triangular":
                type = MembershipFunctionType.TRIANGULAR;
                break;
            case "trapezoidal":
                type = MembershipFunctionType.TRAPEZOIDAL;
                break;
            case "rtrapezoidal":
                type = MembershipFunctionType.RTRAPEZOIDAL;
                break;
            case "ltrapezoidal":
                type = MembershipFunctionType.LTRAPEZOIDAL;
                break;
            case "gamma":
                type = MembershipFunctionType.GAMMA;
                break;
            case "lgamma":
                type = MembershipFunctionType.LGAMMA;
                break;
            case "pseudo-exp":
                type = MembershipFunctionType.PSEUDOEXP;
                break;
            case "nominal":
                type = MembershipFunctionType.NOMINAL;
                break;
            case "zform":
                type = MembershipFunctionType.ZFORM;
                break;
            case "sform":
                type = MembershipFunctionType.SFORM;
                break;
            case "gaussian":
                type = MembershipFunctionType.GAUSSIAN;
                break;
            default:
                type = MembershipFunctionType.valueOf(str);

        }
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