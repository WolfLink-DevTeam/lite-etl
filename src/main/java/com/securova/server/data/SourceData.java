package com.securova.server.data;

import com.google.gson.JsonElement;

import java.util.Calendar;


public record SourceData(AlertType type, Calendar updateTime, JsonElement content) {
    @Override
    public String toString() {
        return content.toString();
    }
}
