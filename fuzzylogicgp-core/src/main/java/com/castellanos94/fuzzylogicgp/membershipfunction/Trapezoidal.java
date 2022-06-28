package com.castellanos94.fuzzylogicgp.membershipfunction;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

public class Trapezoidal extends LTrapezoidal {
    /**
     *
     */
    private static final long serialVersionUID = 5895372936423063836L;

    @Expose
    private Double c;
    @Expose
    private Double d;

    @Override
    public boolean isValid() {
        return !(a == null || b == null || c == null || d == null) && (a <= b && b <= c && c <= d);
    }

    public Trapezoidal(Double a, Double b, Double c, Double d) {
        super(a, b);
        this.c = c;
        this.d = d;
        this.type = MembershipFunctionType.TRAPEZOIDAL;
    }

    public Trapezoidal(String a, String b, String c, String d) {
        this(Double.parseDouble(a), Double.parseDouble(b), Double.parseDouble(c), Double.parseDouble(d));
    }

    @Override

    public double evaluate(Number value) {
        Double v = value.doubleValue();
        if (v < a)
            return 0;
        if (a <= v && v <= b) {
            double lw = (a == b) ? Double.NaN : b - a;
            return (v - a) / lw;
        }
        if (b <= v && v < c)
            return 1;
        if (c <= v && v < d)
            return 1 - (v - c) / (d - c);
        return 0;

    }

    @Override
    public String toString() {
        return String.format("[%s %f %f %f %f]", this.type.toString(), this.a, this.b, this.c, this.d);
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
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Trapezoidal other = (Trapezoidal) obj;
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
    public List<Point> getPoints() {
        ArrayList<Point> points = new ArrayList<>();
        final double step = Math.abs(a - d) / 100.0;
        double x = a - step * 5;
        while (x <= (d + step * 5)) {
            points.add(new Point(x, evaluate(x)));
            x += step;
        }
        return points;
    }

    @Override
    public MembershipFunction copy() {
        return new Trapezoidal(a, b, c, d);
    }

    @Override
    public Double[] toArray() {
        return new Double[] { a, b, c, d };
    }
}