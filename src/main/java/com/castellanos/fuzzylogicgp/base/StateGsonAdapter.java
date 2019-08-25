/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.base;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

/**
 *
 * @author hp
 */
public class StateGsonAdapter implements JsonSerializer<State>{


    @Override
    public JsonElement serialize(State t, Type type, JsonSerializationContext jsc) {
        return getAsJsonObject(t);
    }

    private static JsonObject getAsJsonObject(State t) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("label", t.getLabel());
        jsonObject.addProperty("colname", t.getColName());
        jsonObject.addProperty("", Boolean.FALSE);
        return jsonObject;
    }
    
}
