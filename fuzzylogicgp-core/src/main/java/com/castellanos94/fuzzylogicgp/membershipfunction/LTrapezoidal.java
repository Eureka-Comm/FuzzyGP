package com.castellanos94.fuzzylogicgp.membershipfunction;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

public class LTrapezoidal extends MembershipFunction {
    /**
     *
     */
    private static final long serialVersionUID = -8306412291609590562L;
    @Expose
    protected Double a;
    @Expose
    protected Double b;

    @Override
    public boolean isValid() {
        return (a != null && b != null) && a <= b;
    }

    public LTrapezoidal(double a, double b) {
        super(MembershipFunctionType.LTRAPEZOIDAL);
        this.a = a;
        this.b = b;
    }

    public LTrapezoidal(String a, String b) {
        this(Double.parseDouble(a), Double.parseDouble(b));
    }

    @Override
    public double evaluate(Number value) {
        Double v = value.doubleValue();
        if (v < a)
            return 0;
        if (a <= v && v <= b) {
            if (b == a)
                return Double.NaN;
            return (v - a) / (b - a);
        }
        return 1.0;
    }

    @Override
    public String toString() {
        return String.format("[%s %f %f]", this.type.toString(), this.a, this.b);
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
        LTrapezoidal other = (LTrapezoidal) obj;
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

    @Override
    public List<Point> getPoints() {
        ArrayList<Point> points = new ArrayList<>();
        final double step = Math.abs(a - b) / 100.0;
        double x = a - step * 5;
        while (x <= (b + step * 5)) {
            points.add(new Point(x, evaluate(x)));
            x += step;
        }
        return points;
    }

    @Override
    public MembershipFunction copy() {
        return new LTrapezoidal(a, b);
    }

    @Override
    public Double[] toArray() {
        return new Double[] { a, b };
    }

}