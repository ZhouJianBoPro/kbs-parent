package com.kbs.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kbs.core.vo.DocumentPageRequest;
import com.kbs.core.vo.DocumentVO;
import com.kbs.core.entity.Document;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 文档 Mapper 接口
 */
@Mapper
public interface DocumentMapper extends BaseMapper<Document> {

    /**
     * 分页查询文档列表
     *
     * @param page    分页参数
     * @param request 查询条件
     * @return 分页结果
     */
    IPage<DocumentVO> selectDocumentPage(Page<DocumentVO> page, @Param("request") DocumentPageRequest request);
}
