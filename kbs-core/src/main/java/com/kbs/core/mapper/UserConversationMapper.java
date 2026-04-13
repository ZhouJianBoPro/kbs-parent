package com.kbs.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kbs.core.entity.UserConversation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户会话 Mapper 接口
 */
@Mapper
public interface UserConversationMapper extends BaseMapper<UserConversation> {

}
