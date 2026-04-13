package com.kbs.chatai.starter.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "kbs.document-splitter")
public class DocumentSplitterProperties {

    /**
     * 每个文本块的目标大小，以token为单位
     */
    private Integer chunkSize;

    /**
     * 当文本块超过该配置（最小块）大小时， 按照块的最后（.!?\n。！？）符号截取
     */
    private Integer minChunkSizeChars;

    /**
     * 丢弃小于该长度的文本块（若去掉\r\n，仅剩4个有效文本，那就丢掉）
     */
    private Integer minChunkLengthToEmbed;

    /**
     * 最多生成多少个文本块，超过该配置丢弃不处理
     */
    private Integer maxNumChunks;

    /**
     * 是否保留分隔符，\r\n
     */
    private Boolean keepSeparator;
}
