package com.kbs.redis;

import jakarta.annotation.Resource;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

/**
 * @Desc:
 * @Author: zhoujianbo
 * @Version: 1.0
 * @Date: 2026/4/3 11:05
 **/
@Component
public class RedisPublisher {

    @Resource
    private RedissonClient redissonClient;

    /**
     * 发布消息
     * @param topic 主题
     * @param message 消息
     */
    public <T> void publish(String topic, T message) {
        RTopic rTopic = redissonClient.getTopic(topic);
        rTopic.publish(message);
    }
}
