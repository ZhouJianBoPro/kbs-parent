package com.kbs.chatai.starter.rag.elt;

import com.kbs.chatai.starter.rag.AbstractDocumentReader;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * @Desc:
 * @Author: zhoujianbo
 * @Version: 1.0
 * @Date: 2025/8/21 17:48
 **/
public class TxtDocumentReader extends AbstractDocumentReader {

    @Override
    public List<Document> readDocument(Resource resource) {

        TextReader textReader = new TextReader(resource);
        return textReader.read();
    }
}
