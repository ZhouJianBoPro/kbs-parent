package com.kbs.chatai.starter.advisor;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import reactor.core.publisher.Flux;

/**
 * @Desc:
 * @Author: zhoujianbo
 * @Version: 1.0
 * @Date: 2026/4/10 15:03
 **/
@Slf4j
public class LoggingAdvisor implements BaseAdvisor {
    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        log.info("chatClientRequest: {}", JSON.toJSONString(chatClientRequest));
        return chatClientRequest;
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        log.info("chatClientResponse: {}", JSON.toJSONString(chatClientResponse));
        return chatClientResponse;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
