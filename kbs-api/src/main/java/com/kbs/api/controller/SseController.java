package com.kbs.api.controller;

import com.kbs.sse.SseSessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @Desc:
 * @Author: zhoujianbo
 * @Version: 1.0
 * @Date: 2026/4/1 16:08
 **/
@Slf4j
@PreAuthorize("hasRole('NORMAL')")
@RestController
@RequestMapping("/sse")
public class SseController {

    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(@RequestParam("sessionId") String sessionId) {
        return SseSessionManager.createConnection(sessionId);
    }
}
