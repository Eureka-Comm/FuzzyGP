package com.castellanos94.fuzzylogicgp.membershipfunction;

import java.util.HashMap;
import java.util.List;

import com.google.gson.annotations.Expose;

public class MapNominal extends MembershipFunction {
    /**
     *
     */
    private static final long serialVersionUID = -4723262598446917350L;
    @Expose
    private HashMap<String, Double> values;
    @Expose
    private Double notFoundValue = 0.0;

    public MapNominal() {
        super(MembershipFunctionType.MAPNOMIAL);
        values = new HashMap<>();
    }

    public void setNotFoundValue(Double notFoundValue) {
        this.notFoundValue = notFoundValue;
    }

    public Double getNotFoundValue() {
        return notFoundValue;
    }

    public Double addParameter(String key, Double value) {
        if (value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException("Value must be in [0,1].");
        }
        return this.values.put(key, value);
    }

    public Double remove(String key) {
        return this.values.remove(key);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((notFoundValue == null) ? 0 : notFoundValue.hashCode());
        result = prime * result + ((values == null) ? 0 : values.hashCode());
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
        MapNominal other = (MapNominal) obj;
        if (notFoundValue == null) {
            if (other.notFoundValue != null)
                return false;
        } else if (!notFoundValue.equals(other.notFoundValue))
            return false;
        if (values == null) {
            if (other.values != null)
                return false;
        } else if (!values.equals(other.values))
            return false;
        return true;
    }

    @Override
    public String toString() {
        String st = "";
        for (String kString : values.keySet()) {
            st += "\"" + kString + "\" " + values.get(kString) + ",";
        }
        st = st.trim().substring(0, st.length() - 1);
        return "[map-nominal {" + values + "} " + notFoundValue + "]";
    }

    public HashMap<String, Double> getValues() {
        return values;
    }

    public void setValues(HashMap<String, Double> values) {
        this.values = values;
    }

    @Override
    public double evaluate(Number v) {
        throw new NullPointerException("Key is required.");
    }

    @Override
    public double evaluate(String key) {
        return values.getOrDefault(key, notFoundValue);
    }

    @Override
    public MembershipFunction copy() {
        MapNominal mn = new MapNominal();
        mn.setNotFoundValue(notFoundValue);
        values.forEach((k, v) -> mn.addParameter(k, v));
        return mn;
    }

    @Override
    public List<Point> getPoints() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public boolean isValid() {
        return true;
    }

}