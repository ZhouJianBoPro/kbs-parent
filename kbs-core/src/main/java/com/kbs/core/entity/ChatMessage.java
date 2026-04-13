package com.kbs.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 对话消息实体类（对应 SPRING_AI_CHAT_MEMORY 表）
 * 注意：该表无主键，使用复合索引
 */
@Data
@TableName("spring_ai_chat_memory")
public class ChatMessage {

    /**
     * 会话ID
     */
    @TableField("conversation_id")
    private String conversationId;

    /**
     * 消息内容
     */
    @TableField("content")
    private String content;

    /**
     * 消息类型（USER/ASSISTANT/SYSTEM/TOOL）
     */
    @TableField("type")
    private String type;

    /**
     * 时间戳
     */
    @TableField("timestamp")
    private java.time.LocalDateTime timestamp;
}
