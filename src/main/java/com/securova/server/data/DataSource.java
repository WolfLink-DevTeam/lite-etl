package com.securova.server.data;

import com.google.gson.JsonElement;

import java.util.*;

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
    public DataSource(DataSplitter dataSplitter,UpdateTimeFinder updateTimeFinder,Calendar updateTime) {
        this.dataSplitter = dataSplitter;
        this.updateTimeFinder = updateTimeFinder;
        this.updateTime = updateTime;
    }

    protected abstract Collection<Map.Entry<AlertType,JsonElement>> fetchJsonElement();
    public Collection<SourceData> fetchData() {
        Collection<SourceData> dataSet = fetchJsonElement()
                .stream()
                .flatMap(it -> dataPreprocess(it.getKey(),it.getValue()).stream())
                .filter(sourceData -> sourceData.updateTime().after(updateTime)) // 差量更新数据;
                .toList();
        Optional<SourceData> newestData = dataSet.stream().max(Comparator.comparing(SourceData::updateTime));
        newestData.ifPresent(data -> this.updateTime = data.updateTime());// 刷新更新时间
        return dataSet;
    }
    private Collection<SourceData> dataPreprocess(AlertType type, JsonElement jsonElement) {
        return dataSplitter
                .split(type,jsonElement)// 数据分片
                .stream()
                .map(it -> new SourceData(type,updateTimeFinder.find(type,it),it))// 类型映射
                .toList();
    }
}
