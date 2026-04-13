package com.kbs.chatai.starter.rag.elt;

import com.kbs.chatai.starter.rag.AbstractDocumentReader;
import org.springframework.ai.document.Document;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * doc/docx文档读取
 **/
public class WordDocumentReader extends AbstractDocumentReader {

    @Override
    public List<Document> readDocument(Resource resource) {
        return List.of();
    }
}
