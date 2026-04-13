package com.kbs.rocketmq.consumer;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.GetObjectRequest;
import com.kbs.api.service.DocumentService;
import com.kbs.chatai.starter.rag.AbstractDocumentReader;
import com.kbs.chatai.starter.rag.DocumentEnhancer;
import com.kbs.chatai.starter.rag.VectorStoreService;
import com.kbs.common.consts.ConsumerGroupConstant;
import com.kbs.common.consts.TopicConstant;
import com.kbs.common.enums.DocumentStatus;
import com.kbs.common.enums.DocumentType;
import com.kbs.common.exception.BusinessException;
import com.kbs.core.entity.Document;
import com.kbs.file.starter.config.OssProperties;
import com.kbs.rocketmq.message.DocumentMessage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 文档处理消费者 - 负责文档向量化处理
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = TopicConstant.KBS_DOCUMENT, consumerGroup = ConsumerGroupConstant.KBS_DOCUMENT, maxReconsumeTimes = 2)
public class DocumentConsumer implements RocketMQListener<DocumentMessage> {

    @Resource
    private DocumentService documentService;

    @Resource
    private Map<String, AbstractDocumentReader> documentReaderMap;

    @Resource
    private DocumentEnhancer documentEnhancer;

    @Resource
    private VectorStoreService vectorStoreService;

    @Resource
    private OSS ossClient;

    @Resource
    private OssProperties ossProperties;

    @Override
    public void onMessage(DocumentMessage message) {

        log.info("收到文档处理消息: message= {}", message);

        Document document = documentService.getById(message.getDocumentId());
        if (document == null) {
            throw new BusinessException("文档不存在");
        }

        document.setStatus(DocumentStatus.PROCESSING.getCode());
        documentService.updateById(document);

        try {
            // 1. 读取文档
            List<org.springframework.ai.document.Document> documents = readDocument(document.getFileKey(), document.getDocType());

            // 2. 文档增强
            documents = documentEnhancer.enhancer(document.getId(), documents);

            // 3. 写入向量数据库
            vectorStoreService.addDocuments(documents);

            // 4. 更新文档状态
            document.setStatus(DocumentStatus.COMPLETED.getCode());

        } catch (Exception e) {
            log.error("文档向量化处理失败: documentId = {}", document.getId(), e);
            document.setStatus(DocumentStatus.FAILED.getCode());
        }

        document.setProcessTime(LocalDateTime.now());
        documentService.updateById(document);
    }


    private List<org.springframework.ai.document.Document> readDocument(String fileKey, String docType) {

        // 1. 从OSS获取文件流并封装为Resource
        org.springframework.core.io.Resource resource = getResourceFromOss(fileKey);

        // 2. 获取对应的文档读取器
        String beanName = DocumentType.getBeanNameByExtension(docType);
        AbstractDocumentReader documentReader = documentReaderMap.get(beanName);

        return documentReader.readDocument(resource);
    }

    /**
     * 从OSS获取文件流并封装为Resource
     *
     * @param fileKey OSS文件Key
     * @return Resource
     */
    private org.springframework.core.io.Resource getResourceFromOss(String fileKey) {
        InputStream inputStream = ossClient.getObject(new GetObjectRequest(ossProperties.getBucketName(), fileKey)).getObjectContent();
        return new InputStreamResource(inputStream);
    }

}
