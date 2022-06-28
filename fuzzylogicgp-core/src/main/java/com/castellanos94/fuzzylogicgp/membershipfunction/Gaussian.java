package com.castellanos94.fuzzylogicgp.membershipfunction;

import com.google.gson.annotations.Expose;

import static java.lang.Math.*;

import java.util.ArrayList;
import java.util.List;

/**
 * The class {@code GAUSSIAN_MF} is Generalized Gaussian function fuzzy
 * membership generator.
 * 
 */
public class Gaussian extends MembershipFunction {
    /**
     *
     */
    private static final long serialVersionUID = -2640711699231068902L;
    @Expose
    private Double center;
    @Expose
    private Double deviation;

    public Gaussian() {
        super(MembershipFunctionType.GAUSSIAN);
    }

    /**
     * 
     * @param center
     * @param deviation
     */
    public Gaussian(Double center, Double deviation) {
        this();
        this.center = center;
        this.deviation = deviation;
    }

    public Gaussian(String center, String deviation) {
        this();
        this.center = Double.parseDouble(center);
        this.deviation = Double.parseDouble(deviation);
    }

    @Override
    public Double partialDerivate(double value, String partial_parameter) {
        if (partial_parameter.equals("deviation"))
            return (2. / pow(center, 3)) * exp(-((pow(value - center, 2)) / pow(deviation, 2)))
                    * pow(value - deviation, 2);
        else if (partial_parameter.equals("center"))
            return (2. / pow(deviation, 2)) * exp(-((pow(value - center, 2)) / pow(deviation, 2))) * (value - center);
        else
            return 0.0;
    }

    @Override
    public double evaluate(Number value) {
        Double v = value.doubleValue();
        return Math.exp(-Math.pow(v - center, 2) / (2 * Math.pow(deviation, 2)));
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
    public String toString() {
        return String.format("[%s %f %f]", this.type.toString(), this.center, this.deviation);
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
        Gaussian other = (Gaussian) obj;
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
    public MembershipFunction copy() {
        return new Gaussian(center, deviation);
    }

    @Override
    public boolean isValid() {
        return (center != null && deviation != null);
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
    public Double[] toArray() {
        return new Double[] { center, deviation };
    }
}