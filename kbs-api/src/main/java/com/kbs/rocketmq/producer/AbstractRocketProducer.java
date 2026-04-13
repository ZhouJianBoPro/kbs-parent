package com.kbs.rocketmq.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Component;

/**
 * 通用的RocketMQ消息生产者
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AbstractRocketProducer {

    private final RocketMQTemplate rocketMQTemplate;

    /**
     * 发送同步消息
     *
     * @param topic   主题
     * @param message 消息内容
     * @param <T>     消息类型
     * @return 发送结果
     */
    public <T> SendResult send(String topic, T message) {
        log.info("发送RocketMQ消息, topic={}", topic);
        SendResult result = rocketMQTemplate.syncSend(topic, message);
        log.info("消息发送成功, topic={}, msgId={}", topic, result.getMsgId());
        return result;
    }

    /**
     * 发送同步消息（带标签）
     *
     * @param topic  主题
     * @param tag    标签
     * @param message 消息内容
     * @param <T>   消息类型
     * @return 发送结果
     */
    public <T> SendResult send(String topic, String tag, T message) {
        String fullTopic = topic + ":" + tag;
        log.info("发送RocketMQ消息, topic={}, tag={}", topic, tag);
        SendResult result = rocketMQTemplate.syncSend(fullTopic, message);
        log.info("消息发送成功, topic={}, tag={}, msgId={}", topic, tag, result.getMsgId());
        return result;
    }
}
