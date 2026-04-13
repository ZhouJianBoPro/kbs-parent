package com.kbs.core.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文档信息响应VO
 */
@Data
public class DocumentVO {

    /**
     * 文档ID
     */
    private String id;

    /**
     * 文档名称
     */
    private String docName;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    /**
     * 文档类型
     */
    private String docType;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * OSS存储路径/URL
     */
    private String fileUrl;

    /**
     * 文档状态（0-待处理，1-处理中，2-处理成功，3-处理失败）
     */
    private Integer status;

    /**
     * 上传时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime uploadTime;

    /**
     * 处理完成时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime processTime;
}
