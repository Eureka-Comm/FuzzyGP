package com.castellanos.fuzzylogicgp.parser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Serializable;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Task implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -2401170730627433963L;
    protected TaskType job;
    protected Query query;

    public TaskType getJob() {
        return job;
    }

    public Query getQuery() {
        return query;
    }

    public void setJob(TaskType job) {
        this.job = job;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public String toJSON() {
        Gson print = new GsonBuilder().setPrettyPrinting().create();
        return print.toJson(this);
    }

    public static Task fromJson(Path path) throws FileNotFoundException {
        if(path ==null) 
        return null;
        Gson read = new GsonBuilder().create();
        FileReader fileReader = new FileReader(path.toFile());
        return read.fromJson(fileReader, Task.class);

    }

}