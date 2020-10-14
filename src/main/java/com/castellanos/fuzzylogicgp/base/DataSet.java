package com.castellanos.fuzzylogicgp.base;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.api.ColumnType;

/**
 * @author Pedraza, Edgar
 */

public class DataSet {
    private String path;
    private Table data;
    private List<List<Double>> data_values_list;
    private List<Integer> shapes;
    private List<Double> data_values_class_list;
    /**
    * Iniatialize with a name file of Data Set. This would be {@code csv} file.
    *
    * @param fileName is Value String
    */
    public DataSet(String fileName) {
        this.path = path() + "/" + fileName;
        try {
            this.data = Table.read().file(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setShapes();
        setDataWithOutClass();
        setDataClass();
        
    }

    /**
     * Get absolute {@code PATH} of file Data Set.
     *
     * @return string value of path.
     */
    public String getPath() {
        return path;
    }

    public void setPath(String fileName) {
        this.path = path() + "/" + fileName;
    }

    /**
    * Return List of string that contains the column's names of data set.
    * 
    * @return List of names of data columns.
    */
    public List<String> getNames() {
        return data.columnNames();
    }

    private List<Double> TypeData(String name) {
        ColumnType type = data.column(name).type();
        List<Double> data_cells = new ArrayList<Double>();
        if (type == ColumnType.DOUBLE) {
            Column<Double> column = (Column<Double>) data.column(name);
            for (Double cell : column) {
                data_cells.add(cell);
            }

        } else if (type == ColumnType.FLOAT) {
            Column<Float> column = (Column<Float>) data.column(name);
            for (Float cell : column) {
                data_cells.add((double) cell);
            }

        } else if (type == ColumnType.INTEGER) {
            Column<Integer> column = (Column<Integer>) data.column(name);
            for (Integer cell : column) {
                data_cells.add((double) cell);
            }

        } else if (type == ColumnType.LONG) {
            Column<Long> column = (Column<Long>) data.column(name);
            for (Long cell : column) {
                data_cells.add((double) cell);
            }
        } else if (type == ColumnType.STRING) {
            Column<String> column = (Column<String>) data.column(name);
            try {
                for (String valueString : column) {
                    data_cells.add(Double.parseDouble(valueString));
                }
            } catch (Exception e) {
               e.printStackTrace();
            }

        } else {
            System.out.println("ColumnType: " + type);
        }
        return data_cells;
    }

    private void setDataClass(){
        String class_name = getNames().get(getShapes().get(1) - 1);
        this.data_values_class_list = TypeData(class_name);
    }

    /**
     * Return List Double that contains values of column class.
     * 
     * @return List of {@code values} from class column.
     */
    public List<Double> getDataClass() {
        return this.data_values_class_list;
    }

    private void setDataWithOutClass(){
        List<String> a = getNames();
        this.data_values_list = new ArrayList<>();
        a.remove(a.size()-1);
        for (String name : a) {
            this.data_values_list.add(TypeData(name));
        }
    }

    /**
    * Return List of list's {@code Double} that contains values of columns with out class column.
    * 
    * @return List of {@code values} from Data Set exclude class column.
    */
    public List<List<Double>> getDataWithOutClass(){
        return this.data_values_list;
    }
    /**
     * Get shapes of Data set.
     * 
     * @return List of Integers with numbers of {@code Row,Column}
     */
    private void setShapes() {
        this.shapes = new ArrayList<Integer>();
        this.shapes.add(data.rowCount());
        this.shapes.add(data.columnCount());   
    }

    /**
    * Get shapes of Data set.
    * 
    * @return List of Integers with numbers of {@code Row,Column}
    */
    public List<Integer> getShapes(){
        return this.shapes;
    }

    static private String path() {
        File resourcesDirectory = new File("src/main/resources/datasets");
        return resourcesDirectory.getAbsolutePath();
    }

}