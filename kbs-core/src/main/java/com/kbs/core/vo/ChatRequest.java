package com.kbs.core.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 对话请求
 */
@Data
public class ChatRequest {

    /**
     * 会话ID
     */
    @NotBlank(message = "会话ID不能为空")
    private String conversationId;

    /**
     * 用户消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    private String message;

    /**
     * sse连接sessionId
     */
    @NotBlank(message = "sessionId不能为空")
    private String sessionId;
}
