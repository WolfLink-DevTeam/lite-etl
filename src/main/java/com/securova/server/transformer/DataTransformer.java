package com.securova.server.transformer;

import com.securova.server.Pipeline;
import com.securova.server.data.ProcessedData;
import com.securova.server.data.SourceData;
import com.securova.server.writer.DataWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class DataTransformer {

    Pipeline pipeline;
    DataWriter writer;

    public void initialize(Pipeline pipeline, DataWriter writer) {
        this.pipeline = pipeline;
        this.writer = writer;
    }

    public void transform(SourceData sourceData) {
        writer.write(process(sourceData));
    }

    protected abstract ProcessedData process(SourceData sourceData);
}
