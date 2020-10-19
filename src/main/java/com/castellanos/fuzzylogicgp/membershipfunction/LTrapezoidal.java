package com.castellanos.fuzzylogicgp.membershipfunction;

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
        return !(a == null || b == null);
    }

    public LTrapezoidal(double a, double b) {
        super(MembershipFunctionType.TRAPEZOIDAL);
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
        double step = Math.abs(a - b) / 50;
        double x = -a * 2 - b;
        double y;
        do {
            y = evaluate(x);
            if (y > Point.EPSILON) {
                points.add(new Point(x, y));
            }
            x += step;
        } while (y <= 0.98);

        do {
            y = evaluate(x);
            points.add(new Point(x, y));
            x += step;
        } while (y > Point.EPSILON);
        return points;
    }

    @Override
    public MembershipFunction copy() {
        return new LTrapezoidal(a, b);
    }

}