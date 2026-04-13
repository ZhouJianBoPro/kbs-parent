package com.kbs.chatai.starter.rag;

import com.kbs.chatai.starter.properties.DocumentSplitterProperties;
import lombok.Builder;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.model.transformer.SummaryMetadataEnricher;

import java.util.List;

/**
 * 文档增强目的：
 * 1. 向量模型输入限制：大多数 embedding 模型有最大 token 限制（如 4096/8192），无法处理整篇长文档
 * 2. 提高检索精度：将长文档分割成较小的 chunk 后，用户提问时能检索到最相关的片段，而不是整篇无关文档
 * 3. 上下文关联：通过 SummaryMetadataEnricher 为每个 chunk 添加 PREVIOUS/CURRENT/NEXT 摘要，保持文档上下文连贯性
 * 4. 节省成本：只输入相关 chunk 内容，减少 token 消耗
 **/
@Builder
public class DocumentEnhancer {

    private DocumentSplitterProperties splitterProperties;

    private ChatModel chatModel;

    public DocumentEnhancer(DocumentSplitterProperties splitterProperties, ChatModel chatModel) {
        this.splitterProperties = splitterProperties;
        this.chatModel = chatModel;
    }

    public List<Document> enhancer(String documentId, List<Document> list) {

        // 1. 分割文档：重写了默认的TokenTextSplitter，用于支持中文符号进行文档拆分
        ChineseTokenTextSplitter tokenTextSplitter = getChineseTokenTextSplitter();
        List<Document> documents = tokenTextSplitter.split(list);

        // 2. 提取文档摘要：用于文档之间的关联
        summaryMetadata(documents);

        // 3. 向量数据关联文档ID
        documents.forEach(document -> document.getMetadata().put("document_id", documentId));

        return documents;
    }

    private void summaryMetadata(List<Document> documents) {

        SummaryMetadataEnricher summaryMetadataEnricher = new SummaryMetadataEnricher(chatModel,
                List.of(SummaryMetadataEnricher.SummaryType.PREVIOUS,
                        SummaryMetadataEnricher.SummaryType.CURRENT,
                        SummaryMetadataEnricher.SummaryType.NEXT),
                "这是该部分的内容:\n{context_str}\n\n总结该内容的摘要.\n\n摘要:",
                MetadataMode.ALL);

        summaryMetadataEnricher.apply(documents);
    }

    private ChineseTokenTextSplitter getChineseTokenTextSplitter() {

        return ChineseTokenTextSplitter.builder()
                .withChunkSize(splitterProperties.getChunkSize())
                .withMinChunkSizeChars(splitterProperties.getMinChunkSizeChars())
                .withMinChunkLengthToEmbed(splitterProperties.getMinChunkLengthToEmbed())
                .withMaxNumChunks(splitterProperties.getMaxNumChunks())
                .withKeepSeparator(splitterProperties.getKeepSeparator())
                .build();
    }

}
