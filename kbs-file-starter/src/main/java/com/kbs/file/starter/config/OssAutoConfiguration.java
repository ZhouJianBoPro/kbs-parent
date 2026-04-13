package com.kbs.file.starter.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OSS自动配置类
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(OssProperties.class)
public class OssAutoConfiguration {

    /**
     * 创建OSS客户端
     */
    @Bean
    public OSS ossClient(OssProperties properties) {
        log.info("初始化OSS客户端，endpoint: {}, bucket: {}", properties.getEndpoint(), properties.getBucketName());
        return new OSSClientBuilder()
                .build(properties.getEndpoint(),
                        properties.getAccessKeyId(),
                        properties.getAccessKeySecret());
    }
}
