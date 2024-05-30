package com.securova.server.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FormatDictionary {
    /**
     * 规范化字典，不同的数据源接口字段命名存在较大的差异
     * 需要定义统一的字典进行映射，帮助大模型理解
     */
    private final Map<String, String> sourceDictionary = new HashMap<>();
    private final Map<AlertType, Map<String, String>> typeDictionaries = new HashMap<>();

    /**
     * 注册字典映射
     *
     * @param type     影响的警告数据类型
     * @param fromName 原始字段名称
     * @param toName   目标字段名称(如果留空则删除这个原始字段，不再添加)
     */
    public void register(AlertType type, String fromName, String toName) {
        if (!typeDictionaries.containsKey(type)) {
            typeDictionaries.put(type, new HashMap<>());
        }
        typeDictionaries.get(type).put(fromName, toName);
    }

    public void register(String fromName, String toName) {
        sourceDictionary.put(fromName, toName);
    }

    public void format(JsonElement data, AlertType type) {
        // 数据源级别字典替换
        for (Map.Entry<String, String> entry : sourceDictionary.entrySet()) {
            jsonKeyReplacer(data, entry.getKey(), entry.getValue());
        }
        // 警告类型级别字典替换
        if (!typeDictionaries.containsKey(type)) return;
        for (Map.Entry<String, String> entry : typeDictionaries.get(type).entrySet()) {
            jsonKeyReplacer(data, entry.getKey(), entry.getValue());
        }
    }

    private void jsonKeyReplacer(JsonElement data, String from, String to) {
        if (data.isJsonArray()) {
            data.getAsJsonArray().forEach((element) -> jsonKeyReplacer(element, from, to));
        } else if (data.isJsonObject()) {
            JsonObject jo = data.getAsJsonObject();
            Set<String> keys = new HashSet<>(jo.keySet());
            for (String key : keys) {
                JsonElement value = jo.get(key);
                if (value.isJsonPrimitive()) {
                    if (key.equals(from)) {
                        jo.remove(from);
                        if (to != null && !to.isEmpty()) jo.add(to, value);
                        break;
                    }
                } else if (!value.isJsonNull()) jsonKeyReplacer(jo.get(key), from, to);
            }
        }
    }
}
