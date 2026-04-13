package com.kbs.core.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 对话响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {

    /**
     * 会话ID
     */
    private String conversationId;

    /**
     * 会话标题
     */
    private String conversationTitle;

    /**
     * AI回复内容
     */
    private String response;

    /**
     * 引用文档列表
     */
    private java.util.List<String> references;
}
