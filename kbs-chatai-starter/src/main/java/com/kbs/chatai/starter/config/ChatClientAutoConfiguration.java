package com.kbs.chatai.starter.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.model.RerankModel;
import com.kbs.chatai.starter.advisor.LoggingAdvisor;
import com.kbs.chatai.starter.processor.RerankDocumentPostProcessor;
import com.kbs.chatai.starter.properties.ChatAiProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.generation.augmentation.QueryAugmenter;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

/**
 * AI大模型自动配置类（基于Spring AI DashScope）
 * DashScopeChatModel 由 spring-ai-alibaba-starter-dashscope 自动配置
 * JdbcChatMemoryRepository 由 spring-ai-starter-model-chat-memory-repository-jdbc 自动配置
 * VectorStore 由 spring-ai-starter-vector-store-elasticsearch 自动配置
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(ChatAiProperties.class)
public class ChatClientAutoConfiguration {

    @Value("classpath:prompt/default_system.st")
    private Resource defaultSystemTemplate;

    @Value("classpath:prompt/title_system.st")
    private Resource titleSystemTemplate;

    private final ChatAiProperties chatAiProperties;

    public ChatClientAutoConfiguration(ChatAiProperties chatAiProperties) {
        this.chatAiProperties = chatAiProperties;
    }

    @Bean
    public ChatMemory chatMemory(JdbcChatMemoryRepository jdbcChatMemoryRepository) {

        return MessageWindowChatMemory.builder()
                // 没轮对话最多保存消息数
                .maxMessages(chatAiProperties.getMaxMessages())
                .chatMemoryRepository(jdbcChatMemoryRepository)
                .build();
    }

    @Bean(name = "titleChatClient")
    public ChatClient titleChatClient(DashScopeChatModel chatModel) {

        return ChatClient.builder(chatModel)
                .defaultOptions(ChatOptions.builder().temperature(chatAiProperties.getTemperature()).build())
                .defaultSystem(titleSystemTemplate)
                .build();
    }


    @Bean(name = "chatClient")
    public ChatClient chatClient(DashScopeChatModel chatModel,
                                 ChatMemory chatMemory,
                                 VectorStore vectorStore,
                                 RerankModel rerankModel) {

        return ChatClient.builder(chatModel)
                .defaultOptions(ChatOptions.builder().temperature(chatAiProperties.getTemperature()).build())
                .defaultAdvisors(
                        getRetrievalAugmentationAdvisor(vectorStore, rerankModel),
                        PromptChatMemoryAdvisor.builder(chatMemory).build(),
                        new LoggingAdvisor())
                .defaultSystem(defaultSystemTemplate)
                .build();
    }

    /**
     * 获取向量数据库检索增强Advisor
     * @param vectorStore
     * @return
     */
    private Advisor getRetrievalAugmentationAdvisor(VectorStore vectorStore, RerankModel rerankModel) {

        DocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                .similarityThreshold(chatAiProperties.getSimilarityThreshold())
                .topK(chatAiProperties.getTopK())
                .vectorStore(vectorStore)
                .build();

        QueryAugmenter queryAugmenter = ContextualQueryAugmenter.builder()
                // false:未从向量数据库检索到相似性内容时，将emptyContextPromptTemplate设置的模版发送给大模型；true：将用户提示词发给大模型
                .allowEmptyContext(true)
                .build();

        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)
                // 文档后置处理器，可以对检索出来的文档进行增强，如进行精排
                .documentPostProcessors(new RerankDocumentPostProcessor(rerankModel))
                .queryAugmenter(queryAugmenter)
                .build();
    }
}
