package com.securova.server;

import com.securova.server.data.DataSource;
import com.securova.server.data.ExtraData;
import com.securova.server.data.ProcessedData;
import com.securova.server.data.SourceData;
import com.securova.server.subscriber.DataSubscriber;
import com.securova.server.transformer.DataTransformer;
import com.securova.server.writer.DataWriter;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class Pipeline {
    @Getter
    private final DataSource source;
    private final DataSubscriber subscriber;
    private final List<DataTransformer<?,?>> transformers;
    private final DataWriter writer;

    @Builder
    public Pipeline(
            @NotNull DataSource source,
            @NotNull DataSubscriber subscriber,
            @NotNull List<DataTransformer<?,?>> transformers,
            @NotNull DataWriter writer
    ) {
        this.source = source;
        this.subscriber = subscriber;
        this.transformers = transformers;
        verifyTransformers();
        this.writer = writer;

        subscriber.bind(this);
        transformers.forEach(transformer -> transformer.bind(this));
        writer.bind(this);
    }

    /**
     * 验证多个转换器是否按顺序接通
     */
    private void verifyTransformers() {
        DataTransformer<?,?> firstTransformer = null;
        DataTransformer<?,?> lastTransformer = null;
        for (DataTransformer<?,?> transformer : transformers) {
            if(firstTransformer == null) {
                firstTransformer = transformer;
                lastTransformer = transformer;
            }
            else {
                if(!transformer.getFromClass().equals(lastTransformer.getToClass())) {

                    throw new IllegalArgumentException("""
                            Transformer flows are not valid .
                            
                            lastTransformer:
                                className: %s
                                fromClassName: %s
                            nextTransformer:
                                className: %s
                                toClassName: %s
                            """.formatted(
                                    lastTransformer.getClass().getName(),
                                    lastTransformer.getToClass().getName(),
                                    transformer.getClass().getName(),
                                    transformer.getFromClass().getName()));
                }
                else lastTransformer = transformer;
            }
        }
        if(firstTransformer == null) throw new IllegalArgumentException("No transformer found");
        if(!firstTransformer.getFromClass().equals(SourceData.class)) throw new IllegalArgumentException("Transformer flows must use SourceData as input");
        if(!lastTransformer.getToClass().equals(ProcessedData.class)) throw new IllegalArgumentException("Transformer flows must use ProcessedData as output");
    }
    public void input(SourceData sourceData) {
        write(transform(sourceData));
    }

    private ProcessedData transform(@NotNull SourceData data) {
        ExtraData extraData = new ExtraData();
        Object nowData = data;
        for (DataTransformer<?,?> transformer : transformers) {
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
