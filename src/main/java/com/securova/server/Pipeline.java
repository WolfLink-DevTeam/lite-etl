package com.securova.server;

import com.securova.server.data.DataSource;
import com.securova.server.data.ExtraData;
import com.securova.server.data.ProcessedData;
import com.securova.server.data.SourceData;
import com.securova.server.subscriber.DataSubscriber;
import com.securova.server.transformer.DataTransformer;
import com.securova.server.transformer.TransformerGroup;
import com.securova.server.writer.DataWriter;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.xml.transform.Transformer;
import java.util.ArrayList;
import java.util.List;


public class Pipeline {
    @Getter
    private final DataSource source;
    private final DataSubscriber subscriber;
    private final TransformerGroup transformerGroup;
    private final DataWriter writer;

    @Builder
    public Pipeline(
            @NotNull DataSource source,
            @NotNull DataSubscriber subscriber,
            @NotNull TransformerGroup transformerGroup,
            @NotNull DataWriter writer
    ) {
        this.source = source;
        this.subscriber = subscriber;
        this.transformerGroup = transformerGroup;
        this.writer = writer;

        subscriber.bind(this);
        transformerGroup.getTransformers().forEach(transformer -> transformer.bind(this));
        writer.bind(this);
    }

    public void input(SourceData sourceData) {
        write(transform(sourceData));
    }

    private ProcessedData transform(@NotNull SourceData data) {
        ExtraData extraData = new ExtraData();
        Object nowData = data;
        for (DataTransformer<?,?> transformer : transformerGroup.getTransformers()) {
            nowData = transformer.transform(nowData,extraData);
            if(nowData == null) return null;
        }
        if(!(nowData instanceof ProcessedData)) throw new IllegalStateException();
        return (ProcessedData) nowData;
    }
    private void write(ProcessedData data) {
        writer.write(data);
    }

    /**
     * 开启管道开始数据订阅
     */
    public void open() {
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
