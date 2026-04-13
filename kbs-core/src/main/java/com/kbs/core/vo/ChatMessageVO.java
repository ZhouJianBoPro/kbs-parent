package com.kbs.core.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天消息视图对象
 */
@Data
public class ChatMessageVO {

    /**
     * 会话ID
     */
    private String conversationId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息类型（USER/ASSISTANT）
     */
    private String type;

    /**
     * 时间戳
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime timestamp;
}
