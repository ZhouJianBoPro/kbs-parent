package com.kbs.chatai.starter.rag;

import org.springframework.ai.document.Document;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * @Desc:
 * @Author: zhoujianbo
 * @Version: 1.0
 * @Date: 2025/8/21 17:45
 **/
public abstract class AbstractDocumentReader {

    public abstract List<Document> readDocument(Resource resource);
}
