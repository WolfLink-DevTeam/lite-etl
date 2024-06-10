package com.securova.server.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Calendar;


public record SourceData(String typeName, Calendar updateTime, JsonElement content) {
    public JsonObject toJson() {
        JsonObject jo = new JsonObject();
        jo.addProperty("type", typeName);
        jo.add("body", content);
        return jo;
    }
}
