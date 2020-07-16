package com.castellanos.fuzzylogicgp.membershipfunction;

import com.google.gson.annotations.Expose;

import tech.tablesaw.api.DoubleColumn;

public class Gaussian_MF extends MembershipFunction {
    /**
     *
     */
    private static final long serialVersionUID = -2640711699231068902L;
    @Expose
    private Double center;
    @Expose
    private Double deviation;

    public Gaussian_MF(Double center, Double deviation) {
        this.center = center;
        this.deviation = deviation;
        this.setType(MembershipFunctionType.GAUSSIAN);
    }

    public Gaussian_MF(String center, String deviation) {
        this.center = Double.parseDouble(center);
        this.deviation = Double.parseDouble(deviation);
        this.setType(MembershipFunctionType.GAUSSIAN);
    }


    @Override
    public double evaluate(double v) {
        return Math.exp(-Math.pow(v-center, 2)/(2*Math.pow(deviation, 2)));
    }
    @Override
    public DoubleColumn yPoints() {
        DoubleColumn yColumn = DoubleColumn.create("y column");
        for (double i = 0; i < center*2; i+=0.01) {
            yColumn.append(this.evaluate(i));
        }
        return yColumn;
    }

    @Override
    public DoubleColumn xPoints() {
        DoubleColumn yColumn = DoubleColumn.create("x column");
        for (double i = 0; i < center*2; i+=0.01) {
            yColumn.append(i);
        }
        return yColumn;
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
        return String.format("[%s %f, %f]", this.type.toString(), this.center, this.deviation);
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
        Gaussian_MF other = (Gaussian_MF) obj;
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
    public Object clone() throws CloneNotSupportedException {
        return new Gaussian_MF(center, deviation);
    }

}