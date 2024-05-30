package com.securova.server.llm;

import com.securova.server.data.ProcessedData;
import com.securova.server.data.SourceData;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface AiTransformer {
    @SystemMessage("""
            你需要处理 Json 格式的数据内容，对内容进行准确无误地判断，判断内容是否有意义。
            1.返回的信息表明接口被成功调用了
            2.返回的信息包含与 IP、时间、受影响服务器 等有意义内容，而不是 {} 这样的无意义、空内容
            最终回答一个单词，结果请在 true false 中选择一个回答，不要带有任何其它符号，严格保证内容准确！
            """)
    @UserMessage("""
            {{it}}
            以上数据对于进行威胁日志分析而言，有意义吗？
            """)
    String isContentUseful(String sourceDataJson);

    @SystemMessage("""
            你需要处理 Json 格式的威胁日志数据内容，这些数据的字段名并不一定规范，必须要结合你的思考与判断，寻找与需求字段最相似的实际字段，然后对内容进行提取，捕获所需的关键字段对应的值。
            最终按照给定的输出模板进行输出，不要带有其它任何符号或者 Markdown 格式。
            需要提取的字段：
            type 威胁日志的警报类型，如ABNORMAL_LOGIN、BACKDOOR、CS_VIRUS等
            externalIp 威胁日志中出现的外部(非本机)、攻击者的 IP
            serverIp 威胁日志中出现的本机(受到攻击或入侵的) IP
            serverId 威胁日志中出现的被攻击者的服务器唯一标识名(不是展示名)
            createTime 威胁日志的首次创建时间
            updateTime 威胁日志中的最近更新时间，例如可以是blockTime，用最近一次防御本次攻击的时间作为这个事件的更新时间
            content 对日志中除了上述字段以外的其它额外信息进行总结摘要(字数越少越好！)，例如介绍爆破端口、操作路径、异常登录的用户、本地提权的内容...
            最终输出格式(不要换行)：
            type{}externalIp{}serverIp{}serverId{}createTime{}updateTime{}content{}
            示例输出：
            type{ABNORMAL_LOGIN}externalIp{172.35.32.13}serverIp{217.12.34.56}serverId{ECS-1234567}createTime{2024-05-08 12:34:56}updateTime{2024-05-09 13:44:55}content{无额外内容}
            type{ABNORMAL_LOGIN}externalIp{155.31.67.186}serverIp{222.14.35.26}serverId{ECS-3424567}createTime{2024-05-08 12:34:56}updateTime{2024-05-09 13:44:55}content{入侵端口为22}
            """)
    String extract(@UserMessage String sourceDataJson);

    @SystemMessage("""
            你需要处理两份不同的 JSON 类型数据，其一是未经处理的原始数据，其二是由其一的数据提取和整理后得到的关键字段数据
            你需要严格按照判断标准，检查从数据一提取并转换到数据二的最终结果是否合理、正确，尽管数据二与数据一可能看上去有很大的区别。
            判断标准参考：
            1.数据二中某个字段名明明是时间，其对应的值却是一串IP地址或者乱七八糟的字符串(null除外)。因此数据二不能用于后续分析，判断为 false
            2.数据二中一些字段是数据一中没有的但意思相近，并且数据二字段的值均在数据一中有所体现。因此数据二可用于后续分析，判断为 true
            3.数据二中出现了数据一中没有的值内容，但结合字段名判断应该是对数据一进行的总结。因此数据二可用于后续分析，判断为 true
            4.数据二中只要包含服务器ID、类型、时间，便足够用于后续分析，判断为 true
            如果认为判断结果比较模糊，请直接判断为 true
            最终回答一个单词，结果请在 true false 中选择一个回答，不要带有任何其它符号，严格保证内容准确！
            """)
    @UserMessage("""
            数据一：
            {{source_data}}
            数据二：
            {{processed_data}}
            按照你的判断标准，数据二作为转换结果，是否可用于后续的准确分析？
            """)
    String isDataValid(@V("source_data") String sourceDataJson, @V("processed_data") String processedDataJson);
}
