package com.kbs.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kbs.api.service.ChatService;
import com.kbs.common.consts.TopicConstant;
import com.kbs.common.entity.BasePage;
import com.kbs.common.exception.BusinessException;
import com.kbs.core.entity.ChatMessage;
import com.kbs.core.entity.UserConversation;
import com.kbs.core.mapper.ChatMessageMapper;
import com.kbs.core.mapper.UserConversationMapper;
import com.kbs.core.vo.ChatMessageVO;
import com.kbs.core.vo.ChatRequest;
import com.kbs.core.vo.ConversationVO;
import com.kbs.rocketmq.message.UserChatMessage;
import com.kbs.rocketmq.producer.AbstractRocketProducer;
import com.kbs.sse.SseSessionManager;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 对话服务实现类
 */
@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

    @Resource
    private UserConversationMapper userConversationMapper;

    @Resource
    private ChatMessageMapper chatMessageMapper;

    @Resource
    private AbstractRocketProducer abstractRocketProducer;

    @Override
    public ConversationVO createNewConversation(String userId) {

        UserConversation conversation = new UserConversation();
        conversation.setUserId(userId);

        userConversationMapper.insert(conversation);

        ConversationVO  conversationVO = new ConversationVO();
        conversationVO.setId(conversation.getId());
        conversationVO.setTitle(conversation.getTitle());
        conversationVO.setCreateTime(conversation.getCreateTime());
        return conversationVO;
    }

    @Override
    public void chat(ChatRequest request, String userId) {

        UserConversation userConversation = userConversationMapper.selectById(request.getConversationId());
        if(userConversation == null) {
            throw new BusinessException("会话不存在");
        }

        SseEmitter connection = SseSessionManager.getConnection(request.getSessionId());
        if(connection == null || !SseSessionManager.sendHeartbeat(connection)) {
            throw new BusinessException("请重新创建连接");
        }

        UserChatMessage userChatMessage = new UserChatMessage();
        BeanUtils.copyProperties(request,userChatMessage);

        SendResult sendResult = abstractRocketProducer.send(TopicConstant.KBS_CHAT, userChatMessage);
        if (sendResult == null || !SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
            throw new BusinessException("对话消息发送失败");
        }
    }

    @Override
    public IPage<ConversationVO> getConversationList(BasePage basePage, String userId) {
        Page<UserConversation> page = new Page<>(basePage.getPageNo(), basePage.getPageSize());

        // 查询当前用户的会话列表，按更新时间倒序
        LambdaQueryWrapper<UserConversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserConversation::getUserId, userId).eq(UserConversation::getDelFlag, 0).isNotNull(UserConversation::getTitle)
                .orderByDesc(UserConversation::getUpdateTime);

        IPage<UserConversation> result = userConversationMapper.selectPage(page, wrapper);

        // 转换为VO
        return result.convert(conversation -> {
            ConversationVO vo = new ConversationVO();
            vo.setId(conversation.getId());
            vo.setTitle(conversation.getTitle());
            vo.setCreateTime(conversation.getCreateTime());
            return vo;
        });
    }

    @Override
    public List<ChatMessageVO> getConversationDetail(String conversationId) {

        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatMessage::getConversationId, conversationId)
                .in(ChatMessage::getType, "USER", "ASSISTANT")
                .orderByAsc(ChatMessage::getTimestamp);
        List<ChatMessage> list = chatMessageMapper.selectList(wrapper);

        return list.stream().map(message -> {
            ChatMessageVO vo = new ChatMessageVO();
            vo.setConversationId(message.getConversationId());
            vo.setContent(message.getContent());
            vo.setType(message.getType());
            vo.setTimestamp(message.getTimestamp());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteConversation(String conversationId, String userId) {

        // 验证会话属于当前用户
        UserConversation conversation = userConversationMapper.selectById(conversationId);
        if (conversation == null || !conversation.getUserId().equals(userId)) {
            throw new BusinessException("会话不存在或无权限删除");
        }

        // 更新会话状态
        conversation.setDelFlag(1);
        userConversationMapper.updateById(conversation);
    }
}
