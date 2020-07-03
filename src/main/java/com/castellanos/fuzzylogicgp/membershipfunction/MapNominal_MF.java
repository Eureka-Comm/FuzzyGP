package com.castellanos.fuzzylogicgp.membershipfunction;

import java.util.HashMap;

import com.google.gson.annotations.Expose;

import tech.tablesaw.columns.Column;

public class MapNominal_MF extends MembershipFunction {
    /**
     *
     */
    private static final long serialVersionUID = -4723262598446917350L;
    @Expose
    private HashMap<String, Double> values;
    @Expose
    private Double notFoundValue = 0.0;

    public MapNominal_MF() {
        values = new HashMap<>();
        setType(MembershipFunctionType.MAPNOMIAL);
    }
    

    public void setNotFoundValue(Double notFoundValue) {
        this.notFoundValue = notFoundValue;
    }

    public Double getNotFoundValue() {
        return notFoundValue;
    }

    public Double addParameter(String key, Double value) {
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
        MapNominal_MF other = (MapNominal_MF) obj;
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
    public double evaluate(double v) {
        throw new NullPointerException("Key is required.");
    }

    @Override
    public double evaluate(String key) {
        // return (values.getOrDefault(key, notFoundValue) == notFoundValue) ?
        // notFoundValue : 1.0;
        return values.getOrDefault(key, notFoundValue);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        MapNominal_MF map = new MapNominal_MF();
        if (notFoundValue != null)
            map.setNotFoundValue(notFoundValue);
        if (getValues() != null)
            map.setValues(getValues());
        return map;
    }

    @Override
    public Column yPoints() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Column xPoints() {
        // TODO Auto-generated method stub
        return null;
    }
}