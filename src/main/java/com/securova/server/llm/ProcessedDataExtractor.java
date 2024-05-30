package com.securova.server.llm;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcessedDataExtractor {
    public static Map<String, String> extract(String input) {
        Map<String, String> result = new HashMap<>();

        // 定义正则表达式
        String regex = "type\\{(?<type>.*?)\\}" +
                "externalIp\\{(?<externalIp>.*?)\\}" +
                "serverIp\\{(?<serverIp>.*?)\\}" +
                "serverId\\{(?<serverId>.*?)\\}" +
                "createTime\\{(?<createTime>.*?)\\}" +
                "updateTime\\{(?<updateTime>.*?)\\}" +
                "content\\{(?<content>.*?)\\}";

        // 编译正则表达式
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        // 查找匹配项
        if (matcher.find()) {
            result.put("type", matcher.group("type"));
            result.put("externalIp", matcher.group("externalIp"));
            result.put("serverIp", matcher.group("serverIp"));
            result.put("serverId", matcher.group("serverId"));
            result.put("createTime", matcher.group("createTime"));
            result.put("updateTime", matcher.group("updateTime"));
            result.put("content", matcher.group("content"));
        }

        return result;
    }

}
