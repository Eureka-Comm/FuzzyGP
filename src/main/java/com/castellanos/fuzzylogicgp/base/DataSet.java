package com.castellanos.fuzzylogicgp.base;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tech.tablesaw.api.Table;

/**
 * @author Pedraza, Edgar
 */

public class DataSet {
    private String path;
    private Table data;

    /**
     * Iniatialize with a name file of Data Set.
     * This would be {@code csv} file.
     *
     * @param   fileName is Value String
     */
    public DataSet(String fileName){
        this.path = path() + "/" +fileName;
        try {
            this.data = Table.read().file(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }      
    }

    /**
     * Get absolute {@code PATH} of file Data Set.
     *
     * @return string value of path.
     */
    public String getPath(){
        return path;
    }

    public void setPath(String fileName){
        this.path =  path() + "/" +fileName;
    }

    /**
     * Return List of string that contains the column's names of data set.
     * 
     * @return  List of names of data columns.
     */
    public List<String> getNames(){
        return data.columnNames();
    }

    /**
     * Get shapes of Data set.
     * @return List of Integers with numbers of {@code Row,Column}
     */
    public List<Integer> getShapes(){
        List<Integer> shapes = new ArrayList<Integer>();
        shapes.add(data.rowCount());
        shapes.add(data.columnCount());
        return shapes;
    }

    static private String path(){
        File resourcesDirectory = new File("src/main/resources/datasets");
        return resourcesDirectory.getAbsolutePath();
    }

}