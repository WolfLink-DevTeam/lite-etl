package com.securova.server.preprocessor;

import com.google.gson.JsonElement;

import java.util.Calendar;

public abstract class UpdateTimeFinder {
    public abstract Calendar find(String typeName, JsonElement jsonElement);
}
