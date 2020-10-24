package com.castellanos.fuzzylogicgp.parser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;

import com.castellanos.fuzzylogicgp.base.StateNode;
import com.castellanos.fuzzylogicgp.logic.LogicBuilder;
import com.castellanos.fuzzylogicgp.membershipfunction.MembershipFunction;
import com.castellanos.fuzzylogicgp.membershipfunction.MembershipFunctionType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.Expose;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public abstract class Query implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 775428116619993743L;
    @Expose

    protected TaskType type;
    @Expose
    protected String db_uri;
    @Expose
    protected String out_file;
    @Expose
    protected ArrayList<StateNode> states;
    @Expose
    protected LogicBuilder logic;
    @Expose
    protected String predicate;
    @Expose
    protected String desciption;

    public String getDesciption() {
        return desciption;
    }

    public void setDesciption(String desciption) {
        this.desciption = desciption;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public String getDb_uri() {
        return db_uri;
    }

    public void setDb_uri(String db_uri) {
        this.db_uri = db_uri;
    }

    public String getOut_file() {
        return out_file;
    }

    public void setOut_file(String out_file) {
        this.out_file = out_file;
    }

    public ArrayList<StateNode> getStates() {
        return states;
    }

    public void setStates(ArrayList<StateNode> states) {
        this.states = states;
    }

    public LogicBuilder getLogic() {
        return logic;
    }

    public void setLogic(LogicBuilder logic) {
        this.logic = logic;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((db_uri == null) ? 0 : db_uri.hashCode());
        result = prime * result + ((logic == null) ? 0 : logic.hashCode());
        result = prime * result + ((out_file == null) ? 0 : out_file.hashCode());
        result = prime * result + ((predicate == null) ? 0 : predicate.hashCode());
        result = prime * result + ((states == null) ? 0 : states.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        Query other = (Query) obj;
        if (db_uri == null) {
            if (other.db_uri != null)
                return false;
        } else if (!db_uri.equals(other.db_uri))
            return false;
        if (logic != other.logic)
            return false;
        if (out_file == null) {
            if (other.out_file != null)
                return false;
        } else if (!out_file.equals(other.out_file))
            return false;
        if (predicate == null) {
            if (other.predicate != null)
                return false;
        } else if (!predicate.equals(other.predicate))
            return false;
        if (states == null) {
            if (other.states != null)
                return false;
        } else if (!states.equals(other.states))
            return false;
        if (type != other.type)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "[db_uri=" + db_uri + ", logic=" + logic + ", out_file=" + out_file + ", predicate=" + predicate
                + ", states=" + states + ", type=" + type + "]";
    }

    public String toJSON() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Query.class, new QuerySerializer());
        
        builder.registerTypeAdapter(MembershipFunction.class, new MembershipFunctionSerializer());

        builder.excludeFieldsWithoutExposeAnnotation();
        builder.setPrettyPrinting();

        Gson print = builder.create();
        return print.toJson(this);
    }

    public static Query fromJson(Path path) throws FileNotFoundException {
        if (path == null)
            return null;
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Query.class, new QueryDeserializer());
        
        builder.registerTypeAdapter(MembershipFunction.class, new MembershipFunctionDeserializer());

        Gson read = builder.create();
        FileReader fileReader = new FileReader(path.toFile());
        return read.fromJson(fileReader, Query.class);

    }

}