package com.kbs.api.service;

import com.kbs.core.vo.TokenStatRequest;
import com.kbs.core.vo.TokenStatVO;

/**
 * Token统计服务接口
 */
public interface TokenStatService {

    /**
     * 获取Token使用统计
     *
     * @param request 统计请求参数
     * @return 统计结果
     */
    TokenStatVO getTokenStat(TokenStatRequest request);
}
