package com.castellanos.fuzzylogicgp.membershipfunction;

import java.util.HashMap;

public class MembershipFunctionFactory {
    private Double gamma = 0.;
    private double center = 0;
    private double deviation = 0;
    private Double beta;
    private Double m;
    private double a = 0;
    private double b = 0;
    private double c = 0;
    private double d = 0;
    private double width = 0;
    private double slope = 0;
    private double L = 0;
    private MembershipFunctionType type;
    private HashMap<String, Double> map;
    private double notFoundValue = 0;

    public MembershipFunctionFactory(MembershipFunctionType type) {
        this.type = type;
        this.map = new HashMap<>();
    }

    public MembershipFunction build() {
        switch (type) {
            case FPG:
                if (gamma == null || beta == null || m == null) {
                    return new FPG();
                }
                return new FPG(beta, gamma, m);
            case GAMMA:
                return new Gamma(a, b);
            case GAUSSIAN:
                return new Gaussian(center, deviation);
            case GBELL:
                return new GBell(width, slope, center);
            case LGAMMA:
                return new LGamma(a, b);
            case GCLV:
                return new GCLV(L, gamma, beta, m);
            case LTRAPEZOIDAL:
                return new LTrapezoidal(a, b);
            case RTRAPEZOIDAL:
                return new RTrapezoidal(a, b);
            case NSIGMOID:
                return new NSigmoid(center, beta);
            case SIGMOID:
                return new Sigmoid(center, beta);
            case SFORM:
                return new SForm(a, b);
            case PSEUDOEXP:
                return new PSeudoExp(center, deviation);
            case SINGLETON:
                return new Singleton(a);
            case TRIANGULAR:
                return new Trapezoidal(a, b, c, d);
            case TRAPEZOIDAL:
                return new Trapezoidal(a, b, c, d);
            case ZFORM:
                return new ZForm(a, b);
            case MAPNOMIAL:
                MapNominal mapN = new MapNominal();
                mapN.setNotFoundValue(notFoundValue);
                map.forEach((k, v) -> mapN.addParameter(k, v));
                return mapN;
            case NOMINAL:
                String key = map.keySet().iterator().next();
                Nominal m = new Nominal(key, map.get(key));
                m.setNotFoundValue(notFoundValue);
                return m;
            default:
                return null;
        }
    }

    public void clearMap() {
        this.map.clear();
    }

    public MembershipFunctionFactory addParameter(String key, double value) {
        this.map.put(key, value);
        return this;
    }

    public MembershipFunctionFactory setNotFoundValue(double notFoundValue) {
        this.notFoundValue = notFoundValue;
        return this;
    }

    public MembershipFunctionFactory setA(double a) {
        this.a = a;
        return this;
    }

    public MembershipFunctionFactory setL(double L) {
        this.L = L;
        return this;
    }

    public MembershipFunctionFactory setB(double b) {
        this.b = b;
        return this;
    }

    public MembershipFunctionFactory setBeta(double beta) {
        this.beta = beta;
        return this;
    }

    public MembershipFunctionFactory setC(double c) {
        this.c = c;
        return this;
    }

    public MembershipFunctionFactory setCenter(double center) {
        this.center = center;
        return this;
    }

    public MembershipFunctionFactory setD(double d) {
        this.d = d;
        return this;
    }

    public MembershipFunctionFactory setDeviation(double deviation) {
        this.deviation = deviation;
        return this;
    }

    public MembershipFunctionFactory setGamma(double gamma) {
        this.gamma = gamma;
        return this;
    }

    public MembershipFunctionFactory setM(double m) {
        this.m = m;
        return this;
    }

    public MembershipFunctionFactory setSlope(double slope) {
        this.slope = slope;
        return this;
    }

    public MembershipFunctionFactory setType(MembershipFunctionType type) {
        this.type = type;
        return this;
    }

    public MembershipFunctionFactory setWidth(double width) {
        this.width = width;
        return this;
    }

}
