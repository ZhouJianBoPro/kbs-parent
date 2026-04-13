package com.kbs.api.controller;

import com.kbs.api.service.TokenStatService;
import com.kbs.common.model.Result;
import com.kbs.core.vo.TokenStatRequest;
import com.kbs.core.vo.TokenStatVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Token统计控制器
 */
@Slf4j
@PreAuthorize("hasRole('SYSTEM')")
@RestController
@RequestMapping("/token-stat")
public class TokenStatController {

    @Resource
    private TokenStatService tokenStatService;

    /**
     * 获取Token使用统计
     *
     * @param request 统计请求参数
     *                dimension: 统计维度（day/week/month/year），默认当天
     *                topN: 排名前N的用户，默认前十
     * @return 统计结果
     */
    @GetMapping
    public Result<TokenStatVO> getTokenStat(TokenStatRequest request) {
        TokenStatVO result = tokenStatService.getTokenStat(request);
        return Result.success(result);
    }
}
