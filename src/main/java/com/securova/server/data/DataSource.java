package com.securova.server.data;

import com.google.gson.JsonElement;
import com.securova.server.preprocessor.DataSplitter;
import com.securova.server.preprocessor.UpdateTimeFinder;

import java.util.*;
import java.util.stream.Stream;

public abstract class DataSource {

    /**
     * 数据源上次更新时间
     */
    Calendar updateTime;
    DataSplitter dataSplitter;
    UpdateTimeFinder updateTimeFinder;

    public DataSource(DataSplitter dataSplitter, UpdateTimeFinder updateTimeFinder) {
        this.dataSplitter = dataSplitter;
        this.updateTimeFinder = updateTimeFinder;
        updateTime = Calendar.getInstance();
        updateTime.setTimeInMillis(0);
    }

    public DataSource(DataSplitter dataSplitter, UpdateTimeFinder updateTimeFinder, Calendar updateTime) {
        this.dataSplitter = dataSplitter;
        this.updateTimeFinder = updateTimeFinder;
        this.updateTime = updateTime;
    }

    protected abstract Collection<Map.Entry<String, JsonElement>> fetchJsonElement();

    public Collection<SourceData> fetchData() {
        Collection<SourceData> dataSet = fetchJsonElement()
                .stream()
                .flatMap(it -> dataPreprocess(it.getKey(), it.getValue()).stream())
                .filter(sourceData -> sourceData.updateTime() != null && sourceData.updateTime().after(updateTime)) // 差量更新数据;
                .toList();
        Optional<SourceData> newestData = dataSet.stream().max(Comparator.comparing(SourceData::updateTime));
        newestData.ifPresent(data -> this.updateTime = data.updateTime());// 刷新更新时间
        return dataSet;
    }

    private Collection<SourceData> dataPreprocess(String typeName, JsonElement jsonElement) {
        try {
            return dataSplitter
                    .split(typeName, jsonElement)// 数据分片
                    .stream()
                    .map(it -> new SourceData(typeName, updateTimeFinder.find(typeName, it), it))// 类型映射
                    .toList();
        } catch (Exception e) {
            return Stream.of(new SourceData(typeName, null, jsonElement)).toList();
        }
    }
}
