package com.kbs.common.consts;

/**
 * Redis 前缀
 */
public class RedisConstant {

    /**
     * Access Token 存储前缀
     */
    public static final String TOKEN_PREFIX = "kbs:security:token:";

    /**
     * Refresh Token 存储前缀
     */
    public static final String REFRESH_TOKEN_PREFIX = "kbs:security:refresh_token:";

    /**
     * 方法锁
     */
    public static final String LOCK_METHOD = "lock:method:";

    /**
     * LLM对话结果topic
     */
    public static final String TOPIC_LLM_RESULT = "topic:llm_result";
}
