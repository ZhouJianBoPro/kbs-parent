package com.kbs.chatai.starter.processor;

import com.alibaba.cloud.ai.document.DocumentWithScore;
import com.alibaba.cloud.ai.model.RerankModel;
import com.alibaba.cloud.ai.model.RerankRequest;
import com.alibaba.cloud.ai.model.RerankResponse;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.postretrieval.document.DocumentPostProcessor;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Desc: 文件检索结果后处理器（精排）
 * @Author: zhoujianbo
 * @Version: 1.0
 * @Date: 2026/3/20 11:13
 **/
@Slf4j
public class RerankDocumentPostProcessor implements DocumentPostProcessor {

    private RerankModel rerankModel;

    public RerankDocumentPostProcessor(RerankModel rerankModel) {
        this.rerankModel = rerankModel;
    }

    @Override
    public List<Document> process(Query query, List<Document> documents) {

        String userMessage = query.text();
        log.info("向量检索结果（粗排），userMessage = {}, documentMessages = {}", userMessage, documents.stream().map(Document::getId).toList());

        if (CollectionUtils.isEmpty(documents)) {
            return documents;
        }

        RerankRequest rerankRequest = new RerankRequest(query.text(), documents);
        RerankResponse response = this.rerankModel.call(rerankRequest);

        List<Document> rerankDocuments = response.getResults().stream()
                .sorted(Comparator.comparingDouble(DocumentWithScore::getScore).reversed())
                .map(DocumentWithScore::getOutput).toList();

        log.info("向量检索结果（精排），documentMessages = {}", rerankDocuments.stream().map(Document::getId).toList());
        return rerankDocuments;
    }

    @Override
    public List<Document> apply(Query query, List<Document> documents) {
        return DocumentPostProcessor.super.apply(query, documents);
    }
}
