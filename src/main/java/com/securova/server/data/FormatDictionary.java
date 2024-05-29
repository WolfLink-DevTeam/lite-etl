package com.securova.server.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class FormatDictionary {
    /**
     * 规范化字典，不同的数据源接口字段命名存在较大的差异
     * 需要定义统一的字典进行映射，帮助大模型理解
     */
    private final Map<String,String> sourceDictionary = new HashMap<>();
    private final Map<AlertType, Map<String,String>> typeDictionaries = new HashMap<>();

    /**
     * 注册字典映射
     * @param type  影响的警告数据类型
     * @param fromName  原始字段名称
     * @param toName    目标字段名称(如果留空则删除这个原始字段，不再添加)
     */
    public void register(AlertType type, String fromName, String toName) {
        if(!typeDictionaries.containsKey(type)) {
            typeDictionaries.put(type, new HashMap<>());
        }
        typeDictionaries.get(type).put(fromName, toName);
    }
    public void register(String fromName, String toName) {
        sourceDictionary.put(fromName, toName);
    }
    public void format(JsonElement data,AlertType type) {
        if(data.isJsonArray()) {
            data.getAsJsonArray().forEach((element)->format(element,type));
        } else if(data.isJsonObject()) {
            JsonObject rawData = data.getAsJsonObject();
            // 数据源级别字典替换
            for(Map.Entry<String,String> entry : sourceDictionary.entrySet()) {
                if(rawData.has(entry.getKey())) {
                    JsonElement value = rawData.get(entry.getKey());
                    rawData.remove(entry.getKey());
                    if(entry.getValue().isEmpty()) continue;
                    rawData.add(entry.getValue(),value);
                }
            }
            // 警告类型级别字典替换
            if(!typeDictionaries.containsKey(type)) return;
            for(Map.Entry<String,String> entry : typeDictionaries.get(type).entrySet()) {
                if(rawData.has(entry.getKey())) {
                    JsonElement value = rawData.get(entry.getKey());
                    rawData.remove(entry.getKey());
                    if(entry.getValue().isEmpty()) continue;
                    rawData.add(entry.getValue(),value);
                }
            }
        }
    }
}
