package com.castellanos.fuzzylogicgp.membershipfunction;

import com.google.gson.annotations.Expose;

import tech.tablesaw.api.DoubleColumn;

public class Trapezoidal_MF extends MembershipFunction {
    /**
     *
     */
    private static final long serialVersionUID = 5895372936423063836L;
    @Expose
    private Double a;
    @Expose
    private Double b;
    @Expose
    private Double c;
    @Expose
    private Double d;

    public Trapezoidal_MF(Double a, Double b, Double c, Double d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.setType(MembershipFunctionType.TRAPEZOIDAL);
    }

    public Trapezoidal_MF(String a, String b, String c, String d) {
        this.a = Double.parseDouble(a);
        this.b = Double.parseDouble(b);
        this.c = Double.parseDouble(c);
        this.d = Double.parseDouble(d);
        this.setType(MembershipFunctionType.TRAPEZOIDAL);
    }

    @Override
    public double evaluate(double v) {
        return Math.max(Math.min(Math.min((v - a) / (b - a), (d - v) / (d - c)), 1), 0);
    }

    @Override
    public String toString() {
        return String.format("[%s %f, %f, %f, %f]", this.type.toString(), this.a, this.b, this.c, this.d);
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

    public Double getD() {
        return d;
    }

    public void setD(Double d) {
        this.d = d;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((a == null) ? 0 : a.hashCode());
        result = prime * result + ((b == null) ? 0 : b.hashCode());
        result = prime * result + ((c == null) ? 0 : c.hashCode());
        result = prime * result + ((d == null) ? 0 : d.hashCode());
        return result;
    }

    @Override
    public DoubleColumn xPoints() {
        DoubleColumn xColumn = DoubleColumn.create("x column");
        for (double i = 0; i < b + d; i += 0.01) {
            xColumn.append(i);
        }
        return xColumn;
    }

    @Override
    public DoubleColumn yPoints() {
        DoubleColumn yColumn = DoubleColumn.create("y column");
        for (double i = 0; i < b + d; i += 0.01) {
            yColumn.append(this.evaluate(i));
        }
        return yColumn;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Trapezoidal_MF other = (Trapezoidal_MF) obj;
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
        if (d == null) {
            if (other.d != null)
                return false;
        } else if (!d.equals(other.d))
            return false;
        return true;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new Trapezoidal_MF(a, b, c, d);
    }
}