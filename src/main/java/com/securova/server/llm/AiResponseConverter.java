package com.securova.server.llm;

import com.securova.server.data.AlertType;
import com.securova.server.data.ProcessedData;

import java.util.Map;

public class AiResponseConverter {
    public static boolean toBoolean(String msg) {
        if (msg.contains("true")) return true;
        if (msg.contains("false")) return false;
        throw new IllegalArgumentException("AiResponseConverter toBoolean() failed, msg: " + msg);
    }

    public static ProcessedData toProcessedData(String msg) {
        Map<String, String> map = ProcessedDataExtractor.extract(msg);
        ProcessedData.ProcessedDataBuilder builder = ProcessedData.builder();
        builder.type(map.getOrDefault("type", AlertType.UNKNOWN.name()));
        builder.externalIp(map.getOrDefault("externalIp", ""));
        builder.serverIp(map.getOrDefault("serverIp", ""));
        builder.serverId(map.getOrDefault("serverId", ""));
        builder.createTime(map.getOrDefault("createTime", ""));
        builder.updateTime(map.getOrDefault("updateTime", ""));
        builder.content(map.getOrDefault("content", ""));
        return builder.build();

    }
}
