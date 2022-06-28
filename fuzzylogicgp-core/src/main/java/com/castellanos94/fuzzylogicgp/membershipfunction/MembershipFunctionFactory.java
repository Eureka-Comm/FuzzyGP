package com.castellanos94.fuzzylogicgp.membershipfunction;

import java.util.HashMap;

public class MembershipFunctionFactory {
    private Double gamma = 0.;
    private double center = 0;
    private double deviation = 0;
    private Double beta = 0.;
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

    private MembershipFunctionFactory(MembershipFunctionType type) {
        this.type = type;
        this.map = new HashMap<>();
    }

    public static MembershipFunctionFactory builder(MembershipFunctionType type) {
        return new MembershipFunctionFactory(type);
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
                return new Triangular(a, b, c);
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
                Nominal m;
                if (map.keySet().size() > 0) {
                    String key = map.keySet().iterator().next();
                    m = new Nominal(key, map.get(key));
                } else {
                    m = new Nominal();
                }
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

    /**
     * method for transforming a sorted array into properties
     * 
     * @param type - membership to instance
     * @param vars - sorted values
     */
    public static MembershipFunction fromArray(MembershipFunctionType type, Double[] vars) {
        if (vars == null) {
            throw new IllegalArgumentException("values was null");
        }
        switch (type) {
            case FPG:
                if (vars.length < 3) {
                    throw new IllegalArgumentException("at least 3 values are required [beta, gamma, m]");
                }
                return new FPG(vars[0], vars[1], vars[2]);
            case GAMMA:
                if (vars.length < 2) {
                    throw new IllegalArgumentException("at least 2 values are required [a, b]");
                }
                return new Gamma(vars[0], vars[1]);
            case GAUSSIAN:
                if (vars.length < 2) {
                    throw new IllegalArgumentException("at least 2 values are required [center, deviation]");
                }
                return new Gaussian(vars[0], vars[1]);
            case GBELL:
                if (vars.length < 3) {
                    throw new IllegalArgumentException("at least 3 values are required [width, slope, center]");
                }
                return new GBell(vars[0], vars[1], vars[2]);
            case LGAMMA:
                if (vars.length < 2) {
                    throw new IllegalArgumentException("at least 2 values are required [a, b]");
                }
                return new LGamma(vars[0], vars[1]);
            case GCLV:
                if (vars.length < 4) {
                    throw new IllegalArgumentException("at least 4 values are required [L, gamma, beta, m]");
                }
                return new GCLV(vars[0], vars[1], vars[2], vars[3]);
            case LTRAPEZOIDAL:
                if (vars.length < 2) {
                    throw new IllegalArgumentException("at least 2 values are required [a, b]");
                }
                return new LTrapezoidal(vars[0], vars[1]);
            case RTRAPEZOIDAL:
                if (vars.length < 2) {
                    throw new IllegalArgumentException("at least 2 values are required [a, b]");
                }
                return new RTrapezoidal(vars[0], vars[1]);
            case NSIGMOID:
                if (vars.length < 2) {
                    throw new IllegalArgumentException("at least 2 values are required [center, beta]");
                }
                return new NSigmoid(vars[0], vars[1]);
            case SIGMOID:
                if (vars.length < 2) {
                    throw new IllegalArgumentException("at least 2 values are required [center, beta]");
                }
                return new Sigmoid(vars[0], vars[1]);
            case SFORM:
                if (vars.length < 2) {
                    throw new IllegalArgumentException("at least 2 values are required [a, b]");
                }
                return new SForm(vars[0], vars[1]);
            case PSEUDOEXP:
                if (vars.length < 2) {
                    throw new IllegalArgumentException("at least 1 values are required [center, deviation]");
                }
                return new PSeudoExp(vars[0], vars[1]);
            case SINGLETON:
                if (vars.length < 1) {
                    throw new IllegalArgumentException("at least 1 values are required [a]");
                }
                return new Singleton(vars[0]);
            case TRIANGULAR:
                if (vars.length < 3) {
                    throw new IllegalArgumentException("at least 3 values are required [a, b, c]");
                }
                return new Triangular(vars[0], vars[1], vars[2]);
            case TRAPEZOIDAL:
                if (vars.length < 4) {
                    throw new IllegalArgumentException("at least 4 values are required [a, b, c, d]");
                }
                return new Trapezoidal(vars[0], vars[1], vars[2], vars[3]);
            case ZFORM:
                if (vars.length < 2) {
                    throw new IllegalArgumentException("at least 3 values are required [a, b]");
                }
                return new ZForm(vars[0], vars[1]);
            default:
                throw new IllegalArgumentException("Not supported for " + type);
        }
    }

}
