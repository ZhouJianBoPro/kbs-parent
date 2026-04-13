package com.kbs.rocketmq.message;

import com.kbs.core.vo.ChatRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Desc:
 * @Author: zhoujianbo
 * @Version: 1.0
 * @Date: 2026/4/3 10:39
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class UserChatMessage extends ChatRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
