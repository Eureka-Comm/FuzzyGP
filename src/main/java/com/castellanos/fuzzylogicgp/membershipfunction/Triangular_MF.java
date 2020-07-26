package com.castellanos.fuzzylogicgp.membershipfunction;

import com.google.gson.annotations.Expose;

import tech.tablesaw.api.DoubleColumn;

public class Triangular_MF extends MembershipFunction {
    /**
     *
     */
    private static final long serialVersionUID = -2498385841010998391L;
    @Expose
    private Double a;
    @Expose
    private Double b;
    @Expose
    private Double c;
    @Override
    public boolean isValid() {
        return!(a==null || b == null||c == null);
    }

    public Triangular_MF(String a, String b, String c) {
        this.a = Double.parseDouble(a);
        this.b = Double.parseDouble(b);
        this.c = Double.parseDouble(c);
        this.setType(MembershipFunctionType.TRIANGULAR);
    }

    public Triangular_MF(Double a, Double b, Double c) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.setType(MembershipFunctionType.TRIANGULAR);
    }

    @Override
    public double evaluate(Number value) {
        Double v = value.doubleValue();
        return Math.max(Math.min((v - a) / (b - a), (c - v) / (c - b)), 0);
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

    public Double getC() {
        return c;
    }

    public void setC(Double c) {
        this.c = c;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((a == null) ? 0 : a.hashCode());
        result = prime * result + ((b == null) ? 0 : b.hashCode());
        result = prime * result + ((c == null) ? 0 : c.hashCode());
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
        Triangular_MF other = (Triangular_MF) obj;
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
        if (c == null) {
            if (other.c != null)
                return false;
        } else if (!c.equals(other.c))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("[%s %.3f, %.3f, %.3f]", this.type.toString(), this.a, this.b, this.c);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new Triangular_MF(a, b, c);
    }

    @Override
    public DoubleColumn xPoints() {
        DoubleColumn xColumn = DoubleColumn.create("x column");
        for (double i = 0; i < b + c; i += 0.01) {
            xColumn.append(i);
        }
        return xColumn;
    }

    @Override
    public DoubleColumn yPoints() {
        DoubleColumn yColumn = DoubleColumn.create("y column");
        for (double i = 0; i < b + c; i += 0.01) {
            yColumn.append(this.evaluate(i));
        }
        return yColumn;
    }
}