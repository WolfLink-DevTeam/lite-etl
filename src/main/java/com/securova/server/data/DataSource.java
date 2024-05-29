package com.securova.server.data;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class DataSource {

    protected FormatDictionary formatDictionary = new FormatDictionary();
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

    protected abstract Set<SourceData> fetchRawData();
    public Set<SourceData> fetchData() {
        Set<SourceData> dataSet = fetchRawData().stream()
                .filter(sourceData -> sourceData.updateTime().after(updateTime)) // 差量更新数据;
                .collect(Collectors.toSet());
        dataSet.forEach(sourceData -> formatDictionary.format(sourceData.content(),sourceData.type())); // 数据字段规格化
        Optional<SourceData> newestData = dataSet.stream().max(Comparator.comparing(SourceData::updateTime));
        newestData.ifPresent(data -> this.updateTime = data.updateTime());// 刷新更新时间
        return dataSet;
    }
}
