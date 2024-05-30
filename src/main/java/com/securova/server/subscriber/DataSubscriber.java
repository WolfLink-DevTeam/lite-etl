package com.securova.server.subscriber;

import com.securova.server.Pipeline;
import com.securova.server.data.SourceData;

public abstract class DataSubscriber {

    protected Pipeline pipeline;

    public void bind(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    boolean enable = false;

    public void setEnable(boolean enable) {
        if (this.enable == enable) return;
        this.enable = enable;
        if (enable) enable();
        else disable();
    }

    abstract void enable();

    abstract void disable();

    final void dataReceived(SourceData data) {
        if (!enable) return;
        pipeline.input(data);
    }
}
