package com.kbs.api.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.GetObjectRequest;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kbs.api.service.DocumentService;
import com.kbs.chatai.starter.rag.VectorStoreService;
import com.kbs.common.consts.TopicConstant;
import com.kbs.common.enums.DocumentStatus;
import com.kbs.common.exception.BusinessException;
import com.kbs.common.validator.ValidatorUtil;
import com.kbs.core.entity.Document;
import com.kbs.core.mapper.DocumentMapper;
import com.kbs.core.vo.DocumentPageRequest;
import com.kbs.core.vo.DocumentVO;
import com.kbs.file.starter.config.OssProperties;
import com.kbs.rocketmq.message.DocumentMessage;
import com.kbs.rocketmq.producer.AbstractRocketProducer;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 文档服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl extends ServiceImpl<DocumentMapper, Document> implements DocumentService {

    @Resource
    private OSS ossClient;

    @Resource
    private OssProperties ossProperties;

    @Resource
    private AbstractRocketProducer abstractRocketProducer;

    @Resource
    private VectorStoreService vectorStoreService;

    // 支持的文件类型正则
    private static final String FILE_TYPE_REGEX = "(pdf|txt|md|markdown|doc|docx)";

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void uploadDocument(MultipartFile file, String userId) {

        // 1. 获取文件信息
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String docType = fileExtension.replace(".", "").toLowerCase();

        ValidatorUtil.isTure(docType.matches(FILE_TYPE_REGEX), "不支持的文件类型");

        // 2. 生成OSS存储路径
        String ossKey = ossProperties.getPathPrefix() + userId + "/" + UUID.randomUUID() + "." + fileExtension;

        // 3. 上传到OSS
        uploadOss(ossKey, file);

        // 4. 保存文档信息到数据库
        Document document = saveDocument(originalFilename, docType, file, ossKey);

        // 5. 发送文档向量化消息
        sendDocumentVectorMessage(document, userId);
    }

    @Override
    public DocumentVO getDocumentById(String documentId) {
        Document document = this.getById(documentId);
        if (document == null) {
            throw new BusinessException("文档不存在");
        }
        return convertToVO(document);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDocument(String documentId) {
        Document document = this.getById(documentId);
        if (document == null) {
            throw new BusinessException("文档不存在");
        }

        // 1. 删除es向量数据
        vectorStoreService.deleteDocuments(documentId);

        // 2. 从OSS删除文件
        deleteOssFile(document.getFileKey());

        // 3. 删除数据库记录
        this.removeById(documentId);

        log.info("文档删除成功: {}", documentId);
    }

    @Override
    public IPage<DocumentVO> getDocumentPage(Page<DocumentVO> page, DocumentPageRequest request) {
        return this.baseMapper.selectDocumentPage(page, request);
    }

    @Override
    public InputStream getDocumentStream(String documentId) {
        Document document = this.getById(documentId);
        if (document == null) {
            throw new BusinessException("文档不存在");
        }

        try {
            return ossClient.getObject(new GetObjectRequest(ossProperties.getBucketName(), document.getFileKey())).getObjectContent();
        } catch (Exception e) {
            log.error("获取文档流失败: {}", documentId, e);
            throw new BusinessException("获取文档失败");
        }
    }

    private void deleteOssFile(String fileKey) {

        try {
            ossClient.deleteObject(ossProperties.getBucketName(), fileKey);
        } catch (Exception e) {
            log.error("删除OSS文件失败: {}", fileKey, e);
            throw new BusinessException("文档删除失败");
        }
    }

    /**
     * 上传文件到OSS
     * @param ossKey
     * @param file
     */
    private void uploadOss(String ossKey, MultipartFile file) {

        try {
            // 3. 上传到OSS
            ossClient.putObject(ossProperties.getBucketName(), ossKey, file.getInputStream());
        } catch (Exception e) {
            throw new BusinessException("文档上传OSS失败");
        }
    }

    /**
     * 保存文档信息到数据库
     * @param originalFilename
     * @param docType
     * @param file
     * @param ossKey
     * @return
     */
    private Document saveDocument(String originalFilename, String docType, MultipartFile file, String ossKey) {

        Document document = new Document();
        document.setDocName(originalFilename);
        document.setDocType(docType);
        document.setFileSize(file.getSize());
        document.setFileUrl("https://" + ossProperties.getBucketName() + "." + ossProperties.getEndpoint() + "/" + ossKey);
        document.setFileKey(ossKey);
        document.setStatus(DocumentStatus.PENDING.getCode()); // 待处理
        document.setUploadTime(LocalDateTime.now());
        this.save(document);

        return document;
    }


    /**
     * 转换实体为VO
     */
    private DocumentVO convertToVO(Document document) {
        DocumentVO vo = new DocumentVO();
        BeanUtils.copyProperties(document, vo);
        return vo;
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return null;
        }
        int lastIndexOf = filename.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return null;
        }
        return filename.substring(lastIndexOf);
    }


    /**
     * 发送文档向量化处理消息
     *
     * @param document 文档实体
     * @param userId   用户ID
     */
    private void sendDocumentVectorMessage(Document document, String userId) {

        // 封装消息
        DocumentMessage message = new DocumentMessage();
        message.setDocumentId(document.getId());

        SendResult result = abstractRocketProducer.send(TopicConstant.KBS_DOCUMENT, message);
        if (result == null || !SendStatus.SEND_OK.equals(result.getSendStatus())) {
            throw new BusinessException("文档消息发送失败");
        }
    }
}
