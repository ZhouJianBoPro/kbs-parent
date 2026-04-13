package com.kbs.core.vo;

import lombok.Data;

/**
 * Token统计请求参数
 */
@Data
public class TokenStatRequest {

    /**
     * 统计维度：day(天)、week(周)、month(月)、year(年)
     */
    private String dimension = "day";

    /**
     * 排名数量，默认前十
     */
    private Integer topN = 10;
}
