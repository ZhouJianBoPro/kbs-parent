package com.kbs.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kbs.core.vo.DocumentPageRequest;
import com.kbs.core.vo.DocumentVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * 文档服务接口
 */
public interface DocumentService extends IService<com.kbs.core.entity.Document> {

    /**
     * 上传文档
     *
     * @param file   上传的文件
     * @param userId 上传用户ID
     */
    void uploadDocument(MultipartFile file, String userId);

    /**
     * 获取文档详情
     *
     * @param documentId 文档ID
     * @return 文档信息
     */
    DocumentVO getDocumentById(String documentId);

    /**
     * 删除文档
     *
     * @param documentId 文档ID
     */
    void deleteDocument(String documentId);

    /**
     * 分页查询文档列表
     *
     * @param page    分页参数
     * @param request 查询条件
     * @return 分页结果
     */
    IPage<DocumentVO> getDocumentPage(Page<DocumentVO> page, DocumentPageRequest request);

    /**
     * 获取文档下载流
     *
     * @param documentId 文档ID
     * @return 输入流
     */
    InputStream getDocumentStream(String documentId);
}
