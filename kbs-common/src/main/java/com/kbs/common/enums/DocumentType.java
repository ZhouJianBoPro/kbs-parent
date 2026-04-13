package com.kbs.common.enums;

/**
 * @Desc:
 * @Author: zhoujianbo
 * @Version: 1.0
 * @Date: 2025/8/22 11:45
 **/
public enum DocumentType {

    PDF("pdf", "pdfDocumentReader"),
    TXT("txt", "txtDocumentReader"),
    MARKDOWN("md|markdown", "markdownDocumentReader"),
    WORD("doc|docx", "wordDocumentReader");

    private String extension;

    private String beanName;

    DocumentType(String extension, String beanName) {
        this.extension = extension;
        this.beanName = beanName;
    }

    public String getExtension() {
        return extension;
    }

    public String getBeanName() {
        return beanName;
    }

    public static String getBeanNameByExtension(String extension) {
        for (DocumentType type : DocumentType.values()) {
            if (type.getExtension().contains(extension.toLowerCase())) {
                return type.getBeanName();
            }
        }
        return null;
    }
}
