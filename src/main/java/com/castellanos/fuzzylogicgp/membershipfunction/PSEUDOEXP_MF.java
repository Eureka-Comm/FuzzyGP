package com.castellanos.fuzzylogicgp.membershipfunction;

import com.google.gson.annotations.Expose;

import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.columns.Column;

public class PSEUDOEXP_MF extends MembershipFunction {
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
        return!(center == null|| deviation == null);
    }
    public PSEUDOEXP_MF(){
        this.setType(MembershipFunctionType.PSEUDOEXP);
    }
    public PSEUDOEXP_MF(Double center, Double deviation) {
        this.center = center;
        this.deviation = deviation;
        this.setType(MembershipFunctionType.PSEUDOEXP);
    }

    public PSEUDOEXP_MF(String center, String deviation) {
        this.center = Double.valueOf(center);
        this.deviation = Double.valueOf(deviation);
        this.setType(MembershipFunctionType.PSEUDOEXP);
    }

    @Override
    public double evaluate(Number value) {
        Double v = value.doubleValue();
        return 1.0 / (1.0 + deviation * Math.pow(v - center, 2));
    }
    @Override
    public Column xPoints() {
        DoubleColumn xColumn = DoubleColumn.create("x column");
        for (double i = 0; i < center*deviation; i+=0.1) {
            xColumn.append(i);
        }
        return xColumn;
    }
    @Override
    public Column yPoints() {
        DoubleColumn yColumn = DoubleColumn.create("x column");
        for (double i = 0; i < center*deviation; i+=0.1) {
            yColumn.append(this.evaluate(i));
        }
        return yColumn;
    }
    @Override
    public String toString() {
        return String.format("[%s %f, %f]", this.type, this.center, this.deviation);
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
        PSEUDOEXP_MF other = (PSEUDOEXP_MF) obj;
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

}