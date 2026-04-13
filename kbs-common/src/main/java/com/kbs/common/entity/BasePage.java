package com.kbs.common.entity;

import lombok.Data;

/**
 * @Desc:
 * @Author: zhoujianbo
 * @Version: 1.0
 * @Date: 2026/3/25 13:55
 **/
@Data
public class BasePage {

    /**
     * 页码
     */
    private int pageNo = 1;

    /**
     * 每页显示数量
     */
    private int pageSize = 10;
}
