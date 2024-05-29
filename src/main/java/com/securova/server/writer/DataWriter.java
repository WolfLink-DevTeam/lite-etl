package com.securova.server.writer;

import com.securova.server.Pipeline;
import com.securova.server.data.ProcessedData;
import lombok.Setter;

public abstract class DataWriter {
    @Setter
    Pipeline pipeline;

    public void initialize(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    public abstract void write(ProcessedData processedData);
}
