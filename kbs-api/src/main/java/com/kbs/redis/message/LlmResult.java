package com.kbs.redis.message;

import lombok.Builder;
import lombok.Data;

/**
 * @Desc:
 * @Author: zhoujianbo
 * @Version: 1.0
 * @Date: 2026/4/3 11:08
 **/
@Builder
@Data
public class LlmResult {

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 分块消息
     */
    private String chunkMessage;

    /**
     * 是否已完成
     */
    private boolean isDone;
}
