package com.securova.server.subscriber;

import com.securova.server.Pipeline;
import com.securova.server.data.DataSource;
import com.securova.server.data.SourceData;
import com.securova.server.transformer.DataTransformer;

public abstract class DataSubscriber {

    Pipeline pipeline;
    DataTransformer transformer;


    public void initialize(Pipeline pipeline, DataTransformer transformer) {
        this.pipeline = pipeline;
        this.transformer = transformer;
    }

    boolean enable = false;

    public void setEnable(boolean enable) {
        if (this.enable == enable) return;
        this.enable = enable;
        if (enable) enable(pipeline.getSource());
        else disable();
    }

    abstract void enable(DataSource dataSource);

    abstract void disable();

    final void dataReceived(SourceData data) {
        if (!enable) return;
        transformer.transform(data);
    }
}
