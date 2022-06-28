package com.castellanos94.fuzzylogicgp.membershipfunction;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

public class Triangular extends MembershipFunction {
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

    public Triangular(String a, String b, String c) {
        this.a = Double.parseDouble(a);
        this.b = Double.parseDouble(b);
        this.c = Double.parseDouble(c);
        this.setType(MembershipFunctionType.TRIANGULAR);
    }

    public Triangular(double a, double b, double c) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.setType(MembershipFunctionType.TRIANGULAR);
    }

    @Override
    public double evaluate(Number value) {
        Double v = value.doubleValue();
        double la = b - a;
        double lb = c - b;
        if (v <= a)
            return 0;
        if (a <= v && v <= b) {
            if (la == 0)
                return Float.NaN;
            return (v - a) / la;
        }
        if (b <= v && v <= c) {
            if (lb == 0)
                return Float.NaN;
            return (c - v) / lb;
        }
        return 0;

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
        Triangular other = (Triangular) obj;
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
        return String.format("[%s %f %f %f]", this.type.toString(), this.a, this.b, this.c);
    }

    @Override
    public MembershipFunction copy() {
        return new Triangular(a, b, c);
    }

    @Override
    public List<Point> getPoints() {
        ArrayList<Point> points = new ArrayList<>();
        final double step = Math.abs(a - c) / 100.0;
        double x = a - step * 5;
        while (x <= (c + step * 5)) {
            points.add(new Point(x, evaluate(x)));
            x += step;
        }
        return points;
    }

    @Override
    public boolean isValid() {
        return (a != null && b != null && c != null) && (a <= b && b <= c);
    }

    @Override
    public Double[] toArray() {
        return new Double[] { a, b, c };
    }
}