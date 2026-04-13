package com.kbs.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kbs.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Token使用记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("token_usage")
public class TokenUsage extends BaseEntity {

    /**
     * 主键ID（雪花算法）
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 会话ID
     */
    @TableField("conversation_id")
    private String conversationId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private String userId;

    /**
     * 输入Token数量
     */
    @TableField("input_token")
    private Integer inputToken;

    /**
     * 输出Token数量
     */
    @TableField("output_token")
    private Integer outputToken;

    /**
     * 总Token数量
     */
    @TableField("total_token")
    private Integer totalToken;
}
