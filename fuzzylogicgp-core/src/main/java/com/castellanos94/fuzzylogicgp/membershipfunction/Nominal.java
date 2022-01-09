package com.castellanos94.fuzzylogicgp.membershipfunction;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

public class Nominal extends MembershipFunction {
    /**
     *
     */
    private static final long serialVersionUID = -450695990777374893L;
    @Expose
    private String key;
    @Expose
    private Double value;
    @Expose
    private Double notFoundValue = 0.0;

    public Nominal(String key, Double value) {
        this();
        if (value > 1.0 || value < 0.0) {
            throw new IllegalArgumentException("Value must be in [0,1]");
        }
        this.key = key;
        this.value = value;
    }

    public Nominal() {
        super(MembershipFunctionType.NOMINAL);
    }

    @Override
    public boolean isValid() {
        return !(value == null || key == null)
                || !(value > 1.0 || value < 0.0) && !(notFoundValue > 1.0 || notFoundValue < 0.0);
    }

    @Override
    public double evaluate(String key) {
        if (key.equals(this.key)) {
            return value;
        }
        return notFoundValue;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        if (value > 1.0 || value < 0.0) {
            throw new IllegalArgumentException("Value must be in [0,1]");
        }
        this.value = value;
    }

    public Double getNotFoundValue() {
        return notFoundValue;
    }

    public void setNotFoundValue(Double notFoundValue) {
        if (notFoundValue > 1.0 || notFoundValue < 0.0) {
            throw new IllegalArgumentException("notFoundValue must be in [0,1]");
        }
        this.notFoundValue = notFoundValue;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        result = prime * result + ((notFoundValue == null) ? 0 : notFoundValue.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
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
        Nominal other = (Nominal) obj;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        if (notFoundValue == null) {
            if (other.notFoundValue != null)
                return false;
        } else if (!notFoundValue.equals(other.notFoundValue))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("[%s %s %f %f]", this.type, key, value, notFoundValue);
    }

    @Override
    public MembershipFunction copy() {
        Nominal n = new Nominal(key, value);
        n.setNotFoundValue(notFoundValue);
        return n;
    }

    @Override
    public List<Point> getPoints() {
        ArrayList<Point> point = new ArrayList<>();
        point.add(new Point(1, value));
        point.add(new Point(0.5, notFoundValue));
        return point;
    }
}