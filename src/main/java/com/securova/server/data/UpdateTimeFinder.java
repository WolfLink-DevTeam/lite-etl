package com.securova.server.data;

import com.google.gson.JsonElement;

import java.util.Calendar;

public abstract class UpdateTimeFinder {
    public abstract Calendar find(AlertType type, JsonElement jsonElement);
}
