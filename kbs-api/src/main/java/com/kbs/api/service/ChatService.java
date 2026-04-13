package com.kbs.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kbs.common.entity.BasePage;
import com.kbs.core.vo.ChatMessageVO;
import com.kbs.core.vo.ChatRequest;
import com.kbs.core.vo.ConversationVO;

import java.util.List;

/**
 * 对话服务接口
 */
public interface ChatService {

    /**
     * 创建新会话
     * @param userId
     * @return
     */
    ConversationVO createNewConversation(String userId);

    /**
     * 对话
     *
     * @param request 对话请求
     * @param userId  用户ID
     * @return 对话响应
     */
    void chat(ChatRequest request, String userId);

    /**
     * 获取会话列表
     *
     * @param basePage 分页参数
     * @param userId 用户ID
     * @return 会话列表
     */
    IPage<ConversationVO> getConversationList(BasePage basePage, String userId);

    /**
     * 获取会话详情
     *
     * @param conversationId 会话ID
     * @return 消息列表
     */
    List<ChatMessageVO> getConversationDetail(String conversationId);

    /**
     * 删除会话
     *
     * @param conversationId 会话ID
     * @param userId         用户ID
     */
    void deleteConversation(String conversationId, String userId);
}
