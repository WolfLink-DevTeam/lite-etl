package com.securova.server.data;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public abstract class DataSplitter {
    @NotNull
    public abstract Collection<JsonElement> split(AlertType type, JsonElement jsonElement);
}
