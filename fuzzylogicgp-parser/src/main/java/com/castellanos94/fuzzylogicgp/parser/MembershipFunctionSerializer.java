package com.castellanos94.fuzzylogicgp.parser;

import java.lang.reflect.Type;

import com.castellanos94.fuzzylogicgp.membershipfunction.FPG;
import com.castellanos94.fuzzylogicgp.membershipfunction.Gamma;
import com.castellanos94.fuzzylogicgp.membershipfunction.Gaussian;
import com.castellanos94.fuzzylogicgp.membershipfunction.LGamma;
import com.castellanos94.fuzzylogicgp.membershipfunction.LTrapezoidal;
import com.castellanos94.fuzzylogicgp.membershipfunction.MapNominal;
import com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunction;
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
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class MembershipFunctionSerializer implements JsonSerializer<MembershipFunction> {

    @Override
    public JsonElement serialize(MembershipFunction src, Type typeOfSrc, JsonSerializationContext context) {
        switch (src.type) {
            case FPG:
                return context.serialize((FPG) src);
            case NSIGMOID:
                return context.serialize((NSigmoid) src);
            case SIGMOID:
                return context.serialize((Sigmoid) src);
            case SINGLETON:
                return context.serialize((Singleton) src);
            case MAPNOMIAL:
                return context.serialize((MapNominal) src);
            case GAUSSIAN:
                return context.serialize((Gaussian) src);
            case SFORM:
                return context.serialize((SForm) src);
            case ZFORM:
                return context.serialize((ZForm) src);
            case TRAPEZOIDAL:
                return context.serialize((Trapezoidal) src);
            case TRIANGULAR:
                return context.serialize((Triangular) src);
            case NOMINAL:
                return context.serialize((Nominal) src);
            case GAMMA:
                return context.serialize((Gamma) src);
            case LGAMMA:
                return context.serialize((LGamma) src);
            case LTRAPEZOIDAL:
                return context.serialize((LTrapezoidal) src);
            case RTRAPEZOIDAL:
                return context.serialize((RTrapezoidal) src);
            case PSEUDOEXP:
                return context.serialize((PSeudoExp) src);
            default:
                return null;
        }
    }

}