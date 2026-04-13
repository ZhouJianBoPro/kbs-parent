package com.kbs.file.starter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * OSS配置属性类
 */
@Data
@ConfigurationProperties(prefix = "kbs.oss")
public class OssProperties {

    /**
     * AccessKey ID
     */
    private String accessKeyId;

    /**
     * AccessKey Secret
     */
    private String accessKeySecret;

    /**
     * 地域节点
     */
    private String endpoint;

    /**
     * Bucket名称
     */
    private String bucketName;

    /**
     * 路径前缀
     */
    private String pathPrefix;
}
