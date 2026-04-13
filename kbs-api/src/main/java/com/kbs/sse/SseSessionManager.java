package com.kbs.sse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 1. 前端主动发起创建连接
 * 2. 后端定时发送心跳给前端，心跳发送失败说明连接断开，移除该连接
 * 3. 前端通过 sse自动重连 + 收到心跳超时检测（服务端假死） 来保持连接正常
 * 4. sessionId由前端创建
 *
 * 复用连接优势：
 * 1. 浏览器限制：同一个目标URL下，同一个浏览器最多只能保持6个长连接
 * 2. 减少服务器压力：频繁创建/销毁连接会给服务器造成压力
 **/
@Slf4j
public class SseSessionManager {

    // 维护SSE连接
    private static final Map<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    /**
     * 获取SSE连接
     * @param sessionId
     * @return
     */
    public static SseEmitter getConnection(String sessionId) {
        return emitterMap.get(sessionId);
    }

    /**
     * 创建SSE连接
     *
     * @param sessionId
     * @return
     */
    public static SseEmitter createConnection(String sessionId) {

        // 创建用不超时的连接
        SseEmitter emitter = new SseEmitter(0L);

        emitter.onCompletion(() -> {
            log.info("SSE连接完成, sessionId = {}", sessionId);
            emitterMap.remove(sessionId);
        });

        emitter.onTimeout(() -> {
            log.warn("SSE连接超时, sessionId = {}", sessionId);
            emitterMap.remove(sessionId);
        });

        emitter.onError(throwable -> {
            log.error("SSE连接异常, sessionId = {}", sessionId, throwable);
            emitterMap.remove(sessionId);
        });

        emitterMap.put(sessionId, emitter);
        log.info("创建SSE连接, sessionId={}, 当前连接数={}", sessionId, emitterMap.size());

        return emitter;
    }

    public static void sendToClient(String sessionId, Object data) {

        SseEmitter emitter = emitterMap.get(sessionId);
        if (emitter == null) {
            return;
        }

        try {
            emitter.send(SseEmitter.event().name("message").data(data));
        } catch (IOException e) {
            log.error("SSE推送失败, sessionId={}", sessionId, e);
            emitterMap.remove(sessionId);
        }
    }

    /**
     * 每隔60s定时发送心跳，移除无效链接
     */
    @Scheduled(fixedRate = 60000)
    public void sendHeartbeatScheduled() {

        for (Map.Entry<String, SseEmitter> entry : emitterMap.entrySet()) {
            SseEmitter emitter = entry.getValue();
            boolean isAlive = sendHeartbeat(emitter);
            if(!isAlive) {
                emitterMap.remove(entry.getKey());
            }
        }
    }

    /**
     * 发送心跳
     * @param emitter
     * @return
     */
    public static boolean sendHeartbeat(SseEmitter emitter) {

        try {
            emitter.send(SseEmitter.event().name("heartbeat").data("ping"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void sendDone(String sessionId) {
        SseEmitter emitter = emitterMap.get(sessionId);
        try {
            emitter.send(SseEmitter.event().name("done").data("done"));
        } catch (IOException e) {
            log.error("SSE推送完成异常, sessionId={}", sessionId, e);
        }
    }

}
