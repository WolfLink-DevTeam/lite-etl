package com.securova.server.data;

import java.util.*;
import java.util.stream.Collectors;

public abstract class DataSource {

    /**
     * 数据源上次更新时间
     */
    Calendar updateTime;

    public DataSource() {
        updateTime = Calendar.getInstance();
        updateTime.setTimeInMillis(0);
    }
    public DataSource(Calendar updateTime) {
        this.updateTime = updateTime;
    }

    protected abstract Collection<SourceData> fetchRawData();
    public Collection<SourceData> fetchData() {
        Collection<SourceData> rawData = fetchRawData();
        Collection<SourceData> dataSet = rawData.stream()
                .filter(sourceData -> sourceData.updateTime().after(updateTime)) // 差量更新数据;
                .toList();
        Optional<SourceData> newestData = dataSet.stream().max(Comparator.comparing(SourceData::updateTime));
        newestData.ifPresent(data -> this.updateTime = data.updateTime());// 刷新更新时间
        return dataSet;
    }
}
