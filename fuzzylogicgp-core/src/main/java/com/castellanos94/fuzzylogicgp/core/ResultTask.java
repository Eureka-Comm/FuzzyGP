package com.castellanos94.fuzzylogicgp.core;

import java.io.File;
import java.io.IOException;

/**
 * Abstract result query
 * 
 * @version 0.0.1
 */
public abstract class ResultTask {
    protected Query query;

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }
    
}
