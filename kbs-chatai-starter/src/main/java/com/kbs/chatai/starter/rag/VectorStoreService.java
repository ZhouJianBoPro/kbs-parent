package com.kbs.chatai.starter.rag;

import com.google.common.collect.Lists;
import lombok.Builder;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 文档写入向量数据库
 **/
@Builder
public class VectorStoreService {

    private VectorStore vectorStore;

    /**
     * 每次最多添加10个向量文档
     * @param list
     */
    public void addDocuments(List<Document> list) {

        if(CollectionUtils.isEmpty(list)) {
            return;
        }

        List<List<Document>> partition = Lists.partition(list, 10);
        partition.forEach(documents -> vectorStore.add(documents));
    }

    /**
     * 删除向量数据库中的文档
     * @param documentId
     */
    public void deleteDocuments(String documentId) {

        String filterExpression = "document_id == '" + documentId + "'";
        vectorStore.delete(filterExpression);
    }
}
