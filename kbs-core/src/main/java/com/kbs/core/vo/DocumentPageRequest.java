package com.kbs.core.vo;

import com.kbs.common.entity.BasePage;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 文档分页查询请求VO
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DocumentPageRequest extends BasePage {

    /**
     * 文档名称（模糊查询）
     */
    private String docName;

    /**
     * 文档类型
     */
    private String docType;

    /**
     * 文档状态（0-待处理，1-处理中，2-处理成功，3-处理失败）
     */
    private Integer status;

    /**
     * 上传开始时间
     */
    private LocalDate uploadTimeStart;

    /**
     * 上传结束时间
     */
    private LocalDate uploadTimeEnd;
}
