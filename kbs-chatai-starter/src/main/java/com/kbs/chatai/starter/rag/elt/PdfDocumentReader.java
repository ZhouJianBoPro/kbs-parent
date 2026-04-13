package com.kbs.chatai.starter.rag.elt;

import com.kbs.chatai.starter.rag.AbstractDocumentReader;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * @Desc:
 * @Author: zhoujianbo
 * @Version: 1.0
 * @Date: 2025/8/21 17:49
 **/
public class PdfDocumentReader extends AbstractDocumentReader {

    @Override
    public List<Document> readDocument(Resource resource) {

        PdfDocumentReaderConfig config = PdfDocumentReaderConfig.builder().build();
        PagePdfDocumentReader pagePdfDocumentReader = new PagePdfDocumentReader(resource, config);

        return pagePdfDocumentReader.read();
    }
}
