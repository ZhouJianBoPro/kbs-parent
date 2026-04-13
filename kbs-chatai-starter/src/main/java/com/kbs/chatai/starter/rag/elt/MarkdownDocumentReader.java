package com.kbs.chatai.starter.rag.elt;

import com.kbs.chatai.starter.rag.AbstractDocumentReader;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * @Desc:
 * @Author: zhoujianbo
 * @Version: 1.0
 * @Date: 2025/8/21 17:50
 **/
public class MarkdownDocumentReader extends AbstractDocumentReader {

    @Override
    public List<Document> readDocument(Resource resource) {

        MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                // 分割线创建新文档
                .withHorizontalRuleCreateDocument(true)
                // 代码标签创建新文档
                .withIncludeBlockquote(false)
                // 引用创建新文档
                .withIncludeBlockquote(false)
                .build();

        org.springframework.ai.reader.markdown.MarkdownDocumentReader markdownDocumentReader = new org.springframework.ai.reader.markdown.MarkdownDocumentReader(resource, config);
        return markdownDocumentReader.read();
    }
}
