package com.castellanos94.fuzzylogicgp.parser;

import java.lang.reflect.Type;

import com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunctionType;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class MembershipTypeDeserializer implements JsonDeserializer<MembershipFunctionType> {

    @Override
    public MembershipFunctionType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
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
        return type;
    }

}
