package com.castellanos94.fuzzylogicgp.membershipfunction;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

public class PSeudoExp extends MembershipFunction {
    /**
     *
     */
    private static final long serialVersionUID = 158708124622455069L;
    @Expose
    private Double center;
    @Expose
    private Double deviation;

    @Override
    public boolean isValid() {
        return !(center == null || deviation == null);
    }

    public PSeudoExp(Double center, Double deviation) {
        super(MembershipFunctionType.PSEUDOEXP);
        this.center = center;
        this.deviation = deviation;
    }

    public PSeudoExp(String center, String deviation) {
        this(Double.parseDouble(center), Double.parseDouble(deviation));
    }

    @Override
    public double evaluate(Number value) {
        Double v = value.doubleValue();
        return 1.0 / (1.0 + deviation * Math.pow(v - center, 2));
    }

    @Override
    public String toString() {
        return String.format("[%s %f %f]", this.type, this.center, this.deviation);
    }

    public Double getCenter() {
        return center;
    }

    public void setCenter(Double center) {
        this.center = center;
    }

    public Double getDeviation() {
        return deviation;
    }

    public void setDeviation(Double deviation) {
        this.deviation = deviation;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((center == null) ? 0 : center.hashCode());
        result = prime * result + ((deviation == null) ? 0 : deviation.hashCode());
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
        PSeudoExp other = (PSeudoExp) obj;
        if (center == null) {
            if (other.center != null)
                return false;
        } else if (!center.equals(other.center))
            return false;
        if (deviation == null) {
            if (other.deviation != null)
                return false;
        } else if (!deviation.equals(other.deviation))
            return false;
        return true;
    }

    @Override
    public List<Point> getPoints() {
        ArrayList<Point> points = new ArrayList<>();
        final double step = Math.abs(deviation) / 100.0;
        double x = center - 3 * center / 2.0;
        while (x <= (center + 3 * center / 2.0)) {
            points.add(new Point(x, evaluate(x)));
            x += step;
        }
        return points;
    }

    @Override
    public MembershipFunction copy() {
        return new PSeudoExp(center, deviation);
    }

    @Override
    public Double[] toArray() {
        return new Double[] { center, deviation };
    }
}