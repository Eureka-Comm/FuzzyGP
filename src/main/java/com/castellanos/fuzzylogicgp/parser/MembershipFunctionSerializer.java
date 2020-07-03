package com.castellanos.fuzzylogicgp.parser;

import java.lang.reflect.Type;

import com.castellanos.fuzzylogicgp.membershipfunction.MembershipFunction;
import com.castellanos.fuzzylogicgp.membershipfunction.FPG_MF;
import com.castellanos.fuzzylogicgp.membershipfunction.GAMMA_MF;
import com.castellanos.fuzzylogicgp.membershipfunction.Gaussian_MF;
import com.castellanos.fuzzylogicgp.membershipfunction.LGAMMA_MF;
import com.castellanos.fuzzylogicgp.membershipfunction.LTRAPEZOIDAL_MF;
import com.castellanos.fuzzylogicgp.membershipfunction.MapNominal_MF;
import com.castellanos.fuzzylogicgp.membershipfunction.NSigmoid_MF;
import com.castellanos.fuzzylogicgp.membershipfunction.Nominal_MF;
import com.castellanos.fuzzylogicgp.membershipfunction.PSEUDOEXP_MF;
import com.castellanos.fuzzylogicgp.membershipfunction.RTRAPEZOIDAL_MF;
import com.castellanos.fuzzylogicgp.membershipfunction.SForm_MF;
import com.castellanos.fuzzylogicgp.membershipfunction.Sigmoid_MF;
import com.castellanos.fuzzylogicgp.membershipfunction.Singleton_MF;
import com.castellanos.fuzzylogicgp.membershipfunction.Trapezoidal_MF;
import com.castellanos.fuzzylogicgp.membershipfunction.Triangular_MF;
import com.castellanos.fuzzylogicgp.membershipfunction.ZForm_MF;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class MembershipFunctionSerializer implements JsonSerializer<MembershipFunction> {

    @Override
    public JsonElement serialize(MembershipFunction src, Type typeOfSrc, JsonSerializationContext context) {
        switch (src.getType()) {
            case FPG:
                return context.serialize((FPG_MF) src);
            case NSIGMOID:
                return context.serialize((NSigmoid_MF) src);
            case SIGMOID:
                return context.serialize((Sigmoid_MF) src);
            case SINGLETON:
                return context.serialize((Singleton_MF) src);
            case MAPNOMIAL:
                return context.serialize((MapNominal_MF) src);
            case GAUSSIAN:
                return context.serialize((Gaussian_MF) src);
            case SFORM:
                return context.serialize((SForm_MF) src);
            case ZFORM:
                return context.serialize((ZForm_MF) src);
            case TRAPEZOIDAL:
                return context.serialize((Trapezoidal_MF) src);
            case TRIANGULAR:
                return context.serialize((Triangular_MF) src);
            case NOMINAL:
                return context.serialize((Nominal_MF) src);
            case GAMMA:
                return context.serialize((GAMMA_MF) src);
            case LGAMMA:
                return context.serialize((LGAMMA_MF) src);
            case LTRAPEZOIDAL:
                return context.serialize((LTRAPEZOIDAL_MF) src);
            case RTRAPEZOIDAL:
                return context.serialize((RTRAPEZOIDAL_MF) src);
            case PSEUDOEXP:
                return context.serialize((PSEUDOEXP_MF) src);
            default:
                return null;
        }
    }

}