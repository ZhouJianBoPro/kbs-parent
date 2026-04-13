package com.kbs.redis;

import com.kbs.common.consts.RedisConstant;
import com.kbs.redis.message.LlmResult;
import com.kbs.sse.SseSessionManager;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @Desc: llm对话结果订阅
 * @Author: zhoujianbo
 * @Version: 1.0
 * @Date: 2026/4/3 11:21
 **/
@Slf4j
@Component
public class LlmResultSubscribe implements ApplicationRunner {

    @Resource
    private RedissonClient redissonClient;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        RTopic topic = redissonClient.getTopic(RedisConstant.TOPIC_LLM_RESULT);

        topic.addListener(LlmResult.class, (channel, llmResult) -> {

            String sessionId = llmResult.getSessionId();
            try {
                if(llmResult.isDone()) {
                    SseSessionManager.sendDone(sessionId);
                } else {
                    SseSessionManager.sendToClient(sessionId, llmResult.getChunkMessage());
                }
            } catch (Exception e) {
                log.error("sse推送失败，sessionId = {}", sessionId, e);
            }
        });
    }
}
