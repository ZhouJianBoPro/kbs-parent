-- ----------------------------
-- Spring AI Chat Memory 表
-- ----------------------------
DROP TABLE IF EXISTS `spring_ai_chat_memory`;
CREATE TABLE IF NOT EXISTS `spring_ai_chat_memory` (
    `conversation_id` VARCHAR(36) NOT NULL COMMENT '会话ID',
    `content` TEXT NOT NULL COMMENT '消息内容',
    `type` ENUM('USER', 'ASSISTANT', 'SYSTEM', 'TOOL') NOT NULL COMMENT '消息类型',
    `timestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '时间戳',
    INDEX `spring_ai_chat_memory_conversation_id_timestamp_idx` (`conversation_id`, `timestamp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Spring AI 对话历史表';
