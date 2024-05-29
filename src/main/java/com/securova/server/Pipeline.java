package com.securova.server;

import com.securova.server.data.DataSource;
import com.securova.server.subscriber.DataSubscriber;
import com.securova.server.transformer.DataTransformer;
import com.securova.server.writer.DataWriter;
import lombok.Builder;
import lombok.Getter;

public class Pipeline {
    @Getter
    private final DataSource source;
    private final DataSubscriber subscriber;
    private final DataTransformer transformer;
    private final DataWriter writer;

    @Builder
    public Pipeline(DataSource source, DataSubscriber subscriber, DataTransformer transformer, DataWriter writer) {
        this.source = source;
        this.subscriber = subscriber;
        this.transformer = transformer;
        this.writer = writer;

        subscriber.initialize(this,transformer);
        transformer.initialize(this,writer);
        writer.initialize(this);
    }

    /**
     * 开启管道开始数据订阅
     */
    public void open() {
        if(source == null || subscriber == null || transformer == null || writer == null) {
            throw new NullPointerException("Pipeline are not initialized");
        }
        subscriber.setEnable(true);
    }

    /**
     * 关闭管道停止订阅
     */
    public void close() {
        subscriber.setEnable(false);
        Scheduler.getInstance().shutdownNow();
    }
}
