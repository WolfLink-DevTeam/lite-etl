package com.securova.server.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Calendar;


public record SourceData(AlertType type, Calendar updateTime, JsonElement content) {
    public JsonObject toJson() {
        JsonObject jo = new JsonObject();
        jo.addProperty("alert_type",type.name());
        jo.add("body",content);
        return jo;
    }
}
