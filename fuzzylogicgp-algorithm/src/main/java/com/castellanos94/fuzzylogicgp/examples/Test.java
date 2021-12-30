package com.castellanos94.fuzzylogicgp.examples;

import java.io.IOException;
import java.util.Date;

import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

public class Test {
    public static void main(String[] args) throws IOException {
        Table table = Table.read().csv("/home/thinkpad/Documents/jfuzzy-logic-studio/EurekaProjects/PRUEBA DESCUBRIMIENTOTRADING/BASE DE ACTIVOS DESCUBRIMIENTO 09121.csv");
        System.out.println(table.columnCount());
        System.out.println(table.rowCount());
        for (Column<?> column:table.columns()) {
            System.out.println(column.name() + " "+column.countMissing());
        }
        System.out.println(new Date(0));
    }
}
