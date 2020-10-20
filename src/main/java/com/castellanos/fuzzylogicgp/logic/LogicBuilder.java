package com.castellanos.fuzzylogicgp.logic;

import com.google.gson.annotations.Expose;

public class LogicBuilder {
    @Expose
    protected LogicType type;
    @Expose
    protected Integer exponent;
    @Expose
    protected Boolean natural_implication;

    public static LogicBuilder newBuilder(LogicType type) {
        return new LogicBuilder(type);
    }

    private LogicBuilder(LogicType type) {
        this.type = type;
    }

    public LogicBuilder setExponent(int exponent) {
        this.exponent = exponent;
        return this;
    }

    public LogicBuilder setNatural_implication(boolean natural_implication) {
        this.natural_implication = natural_implication;
        return this;
    }

    public LogicBuilder setType(LogicType type) {
        this.type = type;
        return this;
    }

    public Logic build() {
        if (natural_implication == null) {
            natural_implication = false;
        }
        if (exponent == null) {
            exponent = -1;
        }
        switch (type) {
            case GMBC:
                return new GMBC_Logic(natural_implication);
            case ZADEH:
                return new Zadeh_Logic();
            case AMBC:
                return new AMBC_Logic(natural_implication);
            default:
                return null;
        }
    }
}