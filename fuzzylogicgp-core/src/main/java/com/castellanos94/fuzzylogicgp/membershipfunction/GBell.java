package com.castellanos94.fuzzylogicgp.membershipfunction;

import com.google.gson.annotations.Expose;

import static java.lang.Math.*;

import java.util.ArrayList;
import java.util.List;

/**
 * The class {@code GBELL_MF} is Generalized Bell function fuzzy membership
 * generator.
 * 
 */
public class GBell extends MembershipFunction {

    /**
     *
     */
    private static final long serialVersionUID = 1421421626L;
    @Expose
    private Double width;
    @Expose
    private Double slope;
    @Expose
    private Double center;

    @Override
    public boolean isValid() {
        return !(width == null || slope == null || center == null);
    }

    /**
     * Parameters
     * 
     * @param width  Double Bell function parameter controlling width. See Note for
     *               definition.
     * @param slope  Double Bell function parameter controlling slope. See Note for
     *               definition.
     * @param center Double Bell function parameter defining the center. See Note
     *               for definition.
     */
    public GBell(Double width, Double slope, Double center) {
        this();
        this.width = width;
        this.slope = slope;
        this.center = center;
    }

    /**
     * Parameters
     * 
     * @param width  Double Bell function parameter controlling width.
     * @param slope  Double Bell function parameter controlling slope.
     * @param center Double Bell function parameter defining the center.
     */
    public GBell(String width, String slope, String center) {
        this();
        this.width = Double.parseDouble(width);
        this.slope = Double.parseDouble(slope);
        this.center = Double.parseDouble(center);
    }

    public GBell() {
        super(MembershipFunctionType.GBELL);
    }

    /**
     * Returns {@code y} : 1d array Generalized Bell fuzzy membership function.
     *
     * Definition of Generalized Bell function is:
     * 
     * @return {@code y = 1 / (1 + abs([x - c] / a) ** [2 * b])}
     */
    @Override
    public Double partialDerivate(double value, String partial_parameter) {
        if (partial_parameter.equals("width"))
            return (2. * slope * pow((center - value), 2) * pow(abs((center - value) / width), ((2 * slope) - 2)))
                    / (pow(width, 3) * pow((pow(abs((center - value) / width), (2 * slope)) + 1), 2));
        else if (partial_parameter.equals("slope"))
            return -1 * (2 * pow(abs((center - value) / width), (2 * slope)) * log(abs((center - value) / width)))
                    / (pow((pow(abs((center - value) / width), (2 * slope)) + 1), 2));
        else if (partial_parameter.equals("center"))
            return (2. * slope * (center - value) * pow(abs((center - value) / width), ((2 * slope) - 2)))
                    / (pow(width, 2) * pow((pow(abs((center - value) / width), (2 * slope)) + 1), 2));
        else
            return 0.0;
    }

    @Override
    public double evaluate(Number value) {
        Double v = value.doubleValue();
        return 1. / (1. + pow(abs((v - center) / width), (2 * slope)));
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public void setSlope(Double slope) {
        this.slope = slope;
    }

    public void setCenter(Double center) {
        this.center = center;
    }

    public Double getWidth() {
        return this.width;
    }

    public Double getSlope() {
        return this.slope;
    }

    public Double getCenter() {
        return this.center;
    }

    @Override
    public String toString() {
        return String.format("[%s %f, %f, %f]", this.type.toString(), this.width, this.slope, this.center);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((width == null) ? 0 : width.hashCode());
        result = prime * result + ((slope == null) ? 0 : slope.hashCode());
        result = prime * result + ((center == null) ? 0 : center.hashCode());
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
        GBell other = (GBell) obj;
        if (width == null) {
            if (other.width != null)
                return false;
        } else if (!width.equals(other.width))
            return false;
        if (slope == null) {
            if (other.slope != null)
                return false;
        } else if (!slope.equals(other.slope))
            return false;
        if (center == null) {
            if (other.center != null)
                return false;
        } else if (!center.equals(other.center))
            return false;
        return true;
    }

    @Override
    public MembershipFunction copy() {
        return new GBell(width, slope, center);
    }

    @Override
    public List<Point> getPoints() {
        ArrayList<Point> points = new ArrayList<>();
        final double step = Math.abs(width - center) / 100.0;
        double x = center - 3 * width / 2.0;
        while (x <= (center + 3 * width / 2.0)) {
            points.add(new Point(x, evaluate(x)));
            x += step;
        }
        return points;
    }

    @Override
    public Double[] toArray() {
        return new Double[] { width, slope, center };
    }
}