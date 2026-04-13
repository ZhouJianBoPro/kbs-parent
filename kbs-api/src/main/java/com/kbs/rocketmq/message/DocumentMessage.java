package com.kbs.rocketmq.message;

import lombok.Data;

import java.io.Serializable;

/**
 * 文档处理消息
 */
@Data
public class DocumentMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文档ID
     */
    private String documentId;

}
