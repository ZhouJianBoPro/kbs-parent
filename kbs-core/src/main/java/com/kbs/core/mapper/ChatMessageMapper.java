package com.kbs.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kbs.core.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * 对话消息 Mapper 接口（对应 SPRING_AI_CHAT_MEMORY 表）
 */
@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {

}
