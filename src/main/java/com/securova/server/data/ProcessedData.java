package com.securova.server.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProcessedData {
    // 外部IP(攻击者IP)
    String externalIp;
    // 主体服务器IP(受到攻击的IP)
    String serverIp;
    // 受攻击的服务器ID
    String serverId;
    // 日志类型
    String type;
    // 创建时间
    String createTime;
    // 更新时间
    String updateTime;
    // 内容(爆破端口、操作路径、异常登录用户、本地提取内容...)
    String content;
}
