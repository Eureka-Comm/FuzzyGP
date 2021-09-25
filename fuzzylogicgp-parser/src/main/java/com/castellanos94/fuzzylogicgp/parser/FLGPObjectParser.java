/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos94.fuzzylogicgp.parser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;

import com.castellanos94.fuzzylogicgp.core.GeneratorNode;
import com.castellanos94.fuzzylogicgp.core.Node;
import com.castellanos94.fuzzylogicgp.core.NodeTree;
import com.castellanos94.fuzzylogicgp.core.Query;
import com.castellanos94.fuzzylogicgp.core.StateNode;
import com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *
 * @author thinkpad
 */
public class FLGPObjectParser {
    private final Gson gson;

    public FLGPObjectParser() {

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Query.class, new QuerySerializer());

        builder.registerTypeAdapter(MembershipFunction.class, new MembershipFunctionSerializer());

        builder.excludeFieldsWithoutExposeAnnotation();
        builder.setPrettyPrinting();

        gson = builder.create();
    }

    public String toJson(Query query) {
        return toJSON(query);
    }

    public String toJson(StateNode state) {
        return toJSON(state);
    }

    public String toJson(NodeTree predicate) {
        return toJSON(predicate);
    }

    public String toJson(GeneratorNode generator) {
        return toJSON(generator);
    }

    protected String toJSON(Object object) {
        if (object == null) {
            return null;
        }
        return gson.toJson(object);
    }

    public Object fromJson(Path path, Class<?> clazz) throws FileNotFoundException {
        if (path == null) {
            return null;
        }
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Query.class, new QueryDeserializer());

        builder.registerTypeAdapter(MembershipFunction.class, new MembershipFunctionDeserializer());
        builder.registerTypeAdapter(Node.class, new NodeDeserializer());
        Gson read = builder.create();
        FileReader fileReader = new FileReader(path.toFile());
        return read.fromJson(fileReader, clazz);
    }

    public Gson getGsonWriter() {
        return gson;
    }

}
