package com.castellanos.fuzzylogicgp.core;

import com.castellanos.fuzzylogicgp.base.Query;
import com.castellanos.fuzzylogicgp.base.TaskType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public  class Task {
    protected TaskType job;
    protected Query query;
    /**
     * @return the job
     */
    public TaskType getJob() {
        return job;
    }
    /**
     * @param job the job to set
     */
    public void setJob(TaskType job) {
        this.job = job;
    }
    /**
     * @return the query
     */
    public Query getQuery() {
        return query;
    }
    /**
     * @param query the query to set
     */
    public void setQuery(Query query) {
        this.query = query;
    }
    public String toJSON(){
        Gson print = new GsonBuilder().setPrettyPrinting().create();
        return print.toJson(this);        
    }
    
}