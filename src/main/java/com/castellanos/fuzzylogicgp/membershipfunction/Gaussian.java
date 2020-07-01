package com.castellanos.fuzzylogicgp.membershipfunction;

import com.google.gson.annotations.Expose;

public class Gaussian extends AMembershipFunction {
    /**
     *
     */
    private static final long serialVersionUID = -2640711699231068902L;
    @Expose
    private Double center;
    @Expose
    private Double deviation;

    public Gaussian(Double center, Double deviation) {
        this.center = center;
        this.deviation = deviation;
        this.setType(MembershipFunctionType.GAUSSIAN);
    }

    public Gaussian(String center, String deviation) {
        this.center = Double.parseDouble(center);
        this.deviation = Double.parseDouble(deviation);
        this.setType(MembershipFunctionType.GAUSSIAN);
    }

    @Override
    public boolean isValid() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public double evaluate(double v) {
        return Math.exp(-Math.pow(v-center, 2)/(2*Math.pow(deviation, 2)));
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
    public Object clone() throws CloneNotSupportedException {
        return new Gaussian(center, deviation);
    }

}