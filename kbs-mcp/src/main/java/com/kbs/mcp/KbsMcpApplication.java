package com.kbs.mcp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * MCP 服务启动类
 * 使用 streamable http 传输方式
 */
@MapperScan("com.kbs.core.mapper")
@SpringBootApplication
public class KbsMcpApplication {

    public static void main(String[] args) {
        SpringApplication.run(KbsMcpApplication.class, args);
    }
}
