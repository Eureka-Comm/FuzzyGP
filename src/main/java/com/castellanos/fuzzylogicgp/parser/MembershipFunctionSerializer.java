package com.castellanos.fuzzylogicgp.parser;

import java.lang.reflect.Type;

import com.castellanos.fuzzylogicgp.membershipfunction.FPG;
import com.castellanos.fuzzylogicgp.membershipfunction.Gamma;
import com.castellanos.fuzzylogicgp.membershipfunction.Gaussian;
import com.castellanos.fuzzylogicgp.membershipfunction.LGamma;
import com.castellanos.fuzzylogicgp.membershipfunction.LTrapezoidal;
import com.castellanos.fuzzylogicgp.membershipfunction.MapNominal;
import com.castellanos.fuzzylogicgp.membershipfunction.MembershipFunction;
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