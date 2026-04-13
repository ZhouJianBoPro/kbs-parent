package com.kbs.api.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.kbs.api.controller.vo.DeliveryAddress;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * TestController 单元测试
 */
@Slf4j
@SpringBootTest
class TestControllerTest {

    @Test
    public void structuredOutput(@Autowired DashScopeChatModel dashScopeChatModel) {

        String message = "收货人：张三，电话：13809872345，地址：上海市闵行区浦江镇会诊路15弄28号103";

        ChatClient chatClient = ChatClient.builder(dashScopeChatModel)
                .build();
        ChatClient.CallResponseSpec responseSpec = chatClient.prompt()
                .system("请从下面文本中提取收货信息")
                .user(message)
                .call();

        DeliveryAddress deliveryAddress = responseSpec.entity(new BeanOutputConverter<>(DeliveryAddress.class));
        log.info("deliveryAddress: {}", deliveryAddress);
    }
}
