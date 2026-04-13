package com.kbs.rocketmq.consumer;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.fastjson.JSON;
import com.kbs.advisor.TokenUsageAdvisor;
import com.kbs.common.consts.ConsumerGroupConstant;
import com.kbs.common.consts.RedisConstant;
import com.kbs.common.consts.TopicConstant;
import com.kbs.core.entity.UserConversation;
import com.kbs.core.mapper.TokenUsageMapper;
import com.kbs.core.mapper.UserConversationMapper;
import com.kbs.redis.RedisPublisher;
import com.kbs.redis.message.LlmResult;
import com.kbs.rocketmq.message.UserChatMessage;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

/**
 * 对话处理消费者
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = TopicConstant.KBS_CHAT, consumerGroup = ConsumerGroupConstant.KBS_CHAT, maxReconsumeTimes = 2)
public class ChatConsumer implements RocketMQListener<UserChatMessage> {

    @Resource
    private UserConversationMapper userConversationMapper;

    @Resource
    private ChatClient chatClient;

    @Resource
    private ChatClient titleChatClient;

    @Resource
    private RedisPublisher redisPublisher;

    @Resource
    private ToolCallbackProvider toolCallbackProvider;

    @Resource
    private TokenUsageMapper tokenUsageMapper;

    @Resource
    private DashScopeChatModel chatModel;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void onMessage(UserChatMessage chatMessage) {

        log.info("收到对话处理消息: chatMessage= {}", JSON.toJSONString(chatMessage));

        String conversationId = chatMessage.getConversationId();
        String message = chatMessage.getMessage();

        // 1. 生成会话标题
        generateConversationTitle(conversationId, message);

        // 2. llm对话，流式输出（大模型一边思考一边返回结果）
        Flux<String> responseFlux = chatClient.prompt().toolCallbacks(toolCallbackProvider)
                .advisors(v -> v.param(MessageWindowChatMemory.CONVERSATION_ID, conversationId))
                .advisors(new TokenUsageAdvisor(tokenUsageMapper, userConversationMapper))
                .user(message)
                .stream()
                .content();

        String sessionId = chatMessage.getSessionId();

        // 3. 处理llm对话结果，广播到redis（分布式环境）
        responseFlux.subscribe(
                chunkMessage -> publish(false, sessionId, chunkMessage),
                // 流式输出处理失败
                error -> publish(true, sessionId, null),
                // 流式输出处理完成
                () -> publish(true, sessionId, null)
        );
    }

    private void publish(boolean isDone, String sessionId, String chunkMessage) {

        LlmResult llmResult = LlmResult.builder()
                .sessionId(sessionId)
                .chunkMessage(chunkMessage)
                .isDone(isDone)
                .build();
        redisPublisher.publish(RedisConstant.TOPIC_LLM_RESULT, llmResult);
    }

    private void generateConversationTitle(String conversationId, String message) {

        UserConversation userConversation = userConversationMapper.selectById(conversationId);
        if(StringUtils.isNotBlank(userConversation.getTitle())) {
            return;
        }

        String title = titleChatClient.prompt().user(message).call().content();
        userConversation.setTitle(title);
        userConversationMapper.updateById(userConversation);
    }

}
