package com.kbs.mcp.config;

import com.kbs.mcp.tools.AppUserToolService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Desc:
 * @Author: zhoujianbo
 * @Version: 1.0
 * @Date: 2026/4/10 11:19
 **/
@Configuration
public class ToolsConfig {

    @Bean
    public ToolCallbackProvider toolCallbackProvider(AppUserToolService appUserToolService) {
        return MethodToolCallbackProvider.builder().toolObjects(appUserToolService).build();
    }
}
