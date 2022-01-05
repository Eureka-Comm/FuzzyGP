package com.castellanos94.fuzzylogicgp.logic;

import com.google.gson.annotations.Expose;

public class LogicBuilder {
    @Expose
    protected LogicType type;
    @Expose
    protected Integer exponent;
    @Expose
    protected ImplicationType implicationType;

    public static LogicBuilder newBuilder(LogicType type) {
        return new LogicBuilder(type);
    }

    private LogicBuilder(LogicType type) {
        this.type = type;
    }

    public LogicBuilder setExponent(Integer exponent) {
        this.exponent = exponent;
        return this;
    }

    public LogicBuilder setImplicationType(ImplicationType implicationType) {
        this.implicationType = implicationType;
        return this;
    }

    public LogicBuilder setType(LogicType type) {
        this.type = type;
        return this;
    }

    public Logic build() {
        if (implicationType == null) {
            implicationType = ImplicationType.Zadeh;
        }
        if (exponent == null) {
            exponent = -1;
        }
        switch (type) {
            case GMBC:
                return new GMBCLogic(implicationType);
            case ZADEH:
                return new Zadeh_Logic();
            case AMBC:
                return new AMBCLogic(implicationType);
            case GMBCV:
                return new GMBCFALogic(exponent, implicationType);
            default:
                return null;
        }
    }

    public LogicType getType() {
        return type;
    }

    public Integer getExponent() {
        return exponent;
    }

    public ImplicationType getImplicationType() {
        return implicationType;
    }

    @Override
    public String toString() {
        return "LogicBuilder [exponent=" + exponent + ", implication type=" + implicationType + ", type=" + type
                + "]";
    }
}