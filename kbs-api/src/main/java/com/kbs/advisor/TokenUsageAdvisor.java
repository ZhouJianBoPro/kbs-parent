package com.kbs.advisor;

import com.kbs.core.entity.TokenUsage;
import com.kbs.core.entity.UserConversation;
import com.kbs.core.mapper.TokenUsageMapper;
import com.kbs.core.mapper.UserConversationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.metadata.Usage;

/**
 * @Desc: token使用记录
 * @Author: zhoujianbo
 * @Version: 1.0
 * @Date: 2026/4/10 16:48
 **/
@Slf4j
public class TokenUsageAdvisor implements BaseAdvisor {

    private final TokenUsageMapper tokenUsageMapper;

    private final UserConversationMapper userConversationMapper;

    public TokenUsageAdvisor(TokenUsageMapper tokenUsageMapper, UserConversationMapper userConversationMapper) {
        this.tokenUsageMapper = tokenUsageMapper;
        this.userConversationMapper = userConversationMapper;
    }

    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        return chatClientRequest;
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {

        try {
            Usage usage = chatClientResponse.chatResponse().getMetadata().getUsage();
            TokenUsage tokenUsage = new TokenUsage();
            tokenUsage.setInputToken(usage.getPromptTokens());
            tokenUsage.setOutputToken(usage.getCompletionTokens());
            tokenUsage.setTotalToken(usage.getTotalTokens());

            String conversationId = chatClientResponse.context().get("chat_memory_conversation_id").toString();
            UserConversation userConversation = userConversationMapper.selectById(conversationId);

            tokenUsage.setConversationId(conversationId);
            tokenUsage.setUserId(userConversation.getUserId());

            tokenUsageMapper.insert(tokenUsage);
        } catch (Exception e) {
            log.error("token usage record error", e);
        }

        return chatClientResponse;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
