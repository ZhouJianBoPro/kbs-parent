package com.kbs.chatai.starter.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * AI大模型配置属性类
 */
@Data
@ConfigurationProperties(prefix = "kbs.chatai")
public class ChatAiProperties {

    /**
     * 温度参数
     */
    private Double temperature;

    /**
     * 对话历史最大消息数
     */
    private Integer maxMessages;

    /**
     * 向量检索相似度阈值
     */
    private Double similarityThreshold;

    /**
     * 向量检索返回结果数
     */
    private Integer topK;
}
