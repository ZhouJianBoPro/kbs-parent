package com.kbs.core.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 会话视图对象
 */
@Data
public class ConversationVO {

    /**
     * 会话ID
     */
    private String id;

    /**
     * 会话标题
     */
    private String title;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
