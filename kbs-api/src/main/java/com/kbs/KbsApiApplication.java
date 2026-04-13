package com.kbs;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * KBS API 启动类
 */
@SpringBootApplication
@MapperScan("com.kbs.core.mapper")
public class KbsApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(KbsApiApplication.class, args);
    }
}
