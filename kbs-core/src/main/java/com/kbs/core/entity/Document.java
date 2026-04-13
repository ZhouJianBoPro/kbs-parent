package com.kbs.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kbs.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 文档实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("document")
public class Document extends BaseEntity {

    /**
     * 文档ID（雪花算法）
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 文档名称
     */
    @TableField("doc_name")
    private String docName;

    /**
     * 文档类型（pdf、doc、docx、txt等）
     */
    @TableField("doc_type")
    private String docType;

    /**
     * 文件大小（字节）
     */
    @TableField("file_size")
    private Long fileSize;

    /**
     * OSS存储路径
     */
    @TableField("file_url")
    private String fileUrl;

    /**
     * OSS存储的文件key
     */
    @TableField("file_key")
    private String fileKey;

    /**
     * 文档状态（0-待处理，1-处理中，2-处理成功，3-处理失败）
     */
    @TableField("status")
    private Integer status;

    /**
     * 上传时间
     */
    @TableField("upload_time")
    private LocalDateTime uploadTime;

    /**
     * 处理完成时间
     */
    @TableField("process_time")
    private LocalDateTime processTime;
}
