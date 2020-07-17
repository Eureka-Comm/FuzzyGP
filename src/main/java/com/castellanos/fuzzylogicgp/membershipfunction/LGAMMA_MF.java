package com.castellanos.fuzzylogicgp.membershipfunction;

import com.google.gson.annotations.Expose;

import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.columns.Column;

public class LGAMMA_MF extends MembershipFunction {
    /**
     *
     */
    private static final long serialVersionUID = -7381927880504229507L;
    @Expose
    private Double a;
    @Expose
    private Double b;

    public LGAMMA_MF(double a, double b) {
        this.a = a;
        this.b = b;
        this.setType(MembershipFunctionType.LGAMMA);
    }

    public LGAMMA_MF(String a, String b) {
        this.a = Double.valueOf(a);
        this.b = Double.valueOf(b);
        this.setType(MembershipFunctionType.LGAMMA);
    }

    @Override
    public double evaluate(Number value) {
        Double v = value.doubleValue();
        if( v <=a)
        return 0.0;
        return (b*Math.pow(v-a, 2)/(1+b*Math.pow(v-a, 2)));
    }

    @Override
    public Column yPoints() {
        DoubleColumn yColumn = DoubleColumn.create("y column");
        for (double i = 0; i < b * a; i += 0.01) {
            yColumn.append(this.evaluate(i));
        }
        return yColumn;
    }

    @Override
    public Column xPoints() {
        DoubleColumn xColumn = DoubleColumn.create("x column");
        for (double i = 0; i < b * a; i += 0.01) {
            xColumn.append(i);
        }
        return xColumn;
    }

    @Override
    public String toString() {
        return String.format("[%s %f, %f]", this.type.toString(), this.a, this.b);
    }

    public Double getA() {
        return a;
    }

    public void setA(Double a) {
        this.a = a;
    }

    public Double getB() {
        return b;
    }

    public void setB(Double b) {
        this.b = b;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((a == null) ? 0 : a.hashCode());
        result = prime * result + ((b == null) ? 0 : b.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LGAMMA_MF other = (LGAMMA_MF) obj;
        if (a == null) {
            if (other.a != null)
                return false;
        } else if (!a.equals(other.a))
            return false;
        if (b == null) {
            if (other.b != null)
                return false;
        } else if (!b.equals(other.b))
            return false;
        return true;
    }

}