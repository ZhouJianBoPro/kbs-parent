package com.kbs.chatai.starter.config;

import com.kbs.chatai.starter.properties.DocumentSplitterProperties;
import com.kbs.chatai.starter.rag.AbstractDocumentReader;
import com.kbs.chatai.starter.rag.DocumentEnhancer;
import com.kbs.chatai.starter.rag.VectorStoreService;
import com.kbs.chatai.starter.rag.elt.MarkdownDocumentReader;
import com.kbs.chatai.starter.rag.elt.PdfDocumentReader;
import com.kbs.chatai.starter.rag.elt.TxtDocumentReader;
import com.kbs.chatai.starter.rag.elt.WordDocumentReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RAG自动配置类
 *
 * @Author: zhoujianbo
 * @Version: 1.0
 * @Date: 2026/3/26 14:59
 **/
@Slf4j
@Configuration
@EnableConfigurationProperties(DocumentSplitterProperties.class)
public class RagAutoConfiguration {

    @Bean(name = "pdfDocumentReader")
    public AbstractDocumentReader pdfReader() {
        return new PdfDocumentReader();
    }

    @Bean(name = "markdownDocumentReader")
    public AbstractDocumentReader markdownReader() {
        return new MarkdownDocumentReader();
    }

    @Bean(name = "txtDocumentReader")
    public AbstractDocumentReader txtReader() {
        return new TxtDocumentReader();
    }

    @Bean(name = "wordDocumentReader")
    public AbstractDocumentReader wordReader() {
        return new WordDocumentReader();
    }

    @Bean
    public DocumentEnhancer documentEnhancer(ChatModel chatModel, DocumentSplitterProperties documentSplitterProperties) {
        return DocumentEnhancer.builder()
                .chatModel(chatModel)
                .splitterProperties(documentSplitterProperties)
                .build();
    }

    @Bean
    public VectorStoreService vectorStoreService(VectorStore vectorStore) {
        return VectorStoreService.builder()
                .vectorStore(vectorStore)
                .build();
    }
}
