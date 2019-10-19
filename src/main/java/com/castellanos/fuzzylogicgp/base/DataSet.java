package com.castellanos.fuzzylogicgp.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class DataSet {
    private Set<String> colName;
    private List<List<Double>> values;

    public DataSet() {
        this.colName = new HashSet<>();
        this.values = new ArrayList<>();
    }

    public DataSet(String... names) throws DuplicateException {
        this.colName = new HashSet<>();
        for (String string : names) {
            if (!colName.add(string)) {
                throw new DuplicateException("Colname already exists: " + string);
            }
        }
        this.values = new ArrayList<>();
    }

    public void addColumn(String name) throws DuplicateException {
        if (!colName.add(name)) {
            throw new DuplicateException("Colname already exists: " + name);
        }
        this.values.add(new ArrayList<>());
    }

    public void addColumn(String name, Double... value) throws DuplicateException {
        if (!colName.add(name)) {
            throw new DuplicateException("Colname already exists: " + name);
        }
        this.values.add(new ArrayList<>(Arrays.asList(value)));
    }

    public List<Double> getValues(String name) {

        return this.values.get(retriveIndex(name));
    }

    public Double getValueAt(String name, int pos) {
        return this.values.get(retriveIndex(name)).get(pos);
    }

    private int retriveIndex(String name) {
        int index = 0;
        Iterator<String> iterator = colName.iterator();
        while (iterator.hasNext() && (name.equals(iterator.next()))) {
            index++;
        }
        return index;
    }
    public int size(){
        return this.colName.size();
    }
    public int lenght(){
        return (!this.values.isEmpty())?this.values.get(0).size():0;
    }

}