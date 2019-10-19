package com.castellanos.fuzzylogicgp.base;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class DataSet {
    private HashMap<String, List<Double>> data;

    public DataSet() {
        this.data = new HashMap<>();
    }

    public DataSet(String... names) throws DuplicateException {
        this.data = new HashMap<>();
        for (String string : names) {
            if (!data.containsKey(string)) {
                data.put(string, new ArrayList<>());
            } else {
                throw new DuplicateException("Colname already exists: " + string);
            }
        }
    }

    public void addColumn(String name) throws DuplicateException {
        if (!data.containsKey(name)) {
            data.put(name, new ArrayList<>());
        } else {
            throw new DuplicateException("Colname already exists: " + name);
        }
    }

    public void addColumns(String... names) throws DuplicateException {
        this.data = new HashMap<>();
        for (String string : names) {
            if (!data.containsKey(string)) {
                data.put(string, new ArrayList<>());
            } else {
                throw new DuplicateException("Colname already exists: " + string);
            }
        }
    }

    public void addColumn(String name, Double... value) throws DuplicateException {
        if (!data.containsKey(name)) {
            data.put(name, new ArrayList<>());
        } else {
            throw new DuplicateException("Colname already exists: " + name);
        }
    }

    public void addValues(String name, Double... values) {
        data.get(name).addAll(Arrays.asList(values));
    }

    public void addValue(String name, Double value) {
        data.get(name).add(value);
    }

    public List<Double> getValues(String name) {

        return this.data.get(name);
    }

    public Double getValueAt(String name, int pos) {
        return this.data.get(name).get(pos);
    }

    public int size() {
        return this.data.size();
    }

    @Override
    public String toString() {
        StringBuilder st = new StringBuilder();
        Set<String> keySet = data.keySet();
        if (keySet.iterator().hasNext()) {
            int rows = 0;
            Iterator<String> keyIt = keySet.iterator();
           
            while (keyIt.hasNext()) {
                String n = keyIt.next();
                if (keyIt.hasNext())
                    st.append(String.format(" %s,", n));
                else {
                    st.append(String.format(" %s\n", n));
                }

                List d = data.get(n);
                if (d.size() > rows) {
                    rows = d.size();
                }
            }
            for (int i = 0; i < rows; i++) {
                Iterator<String> iterator = keySet.iterator();
                while (iterator.hasNext()) {
                    List dd = data.get(iterator.next());
                    st.append(String.format(" %s%c", (dd.size() > i) ? dd.get(i) : Double.NaN, (iterator.hasNext())?',':'\n' ));
                }
            }
        }
        return st.toString();
    }

    public static void main(String[] args) throws DuplicateException {
        DataSet data = new DataSet();
        data.addColumns("Col 1", "Col 2", "Col 3");
        data.addValue("Col 1", 0.4);
        data.addValue("Col 3", 10.9);
        data.addValues("Col 2", 5.6, 1.3, -392.4123219, 1.0, -1.0, 0.0);
        data.addValues("Col 1", 1293121312941.24812401201,9.230120312903129921389);
        System.out.println(data.data);
        System.out.println(data);
    }
}