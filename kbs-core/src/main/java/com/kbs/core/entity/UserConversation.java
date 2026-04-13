package com.kbs.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kbs.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户会话实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_conversation")
public class UserConversation extends BaseEntity {

    /**
     * 会话ID（雪花算法）
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private String userId;

    /**
     * 会话标题
     */
    @TableField("title")
    private String title;
}
