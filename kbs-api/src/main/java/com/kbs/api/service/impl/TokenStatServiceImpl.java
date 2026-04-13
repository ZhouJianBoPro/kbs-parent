package com.kbs.api.service.impl;

import com.kbs.api.service.TokenStatService;
import com.kbs.core.mapper.TokenUsageMapper;
import com.kbs.core.vo.TokenStatRequest;
import com.kbs.core.vo.TokenStatVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Token统计服务实现类
 */
@Slf4j
@Service
public class TokenStatServiceImpl implements TokenStatService {

    @Resource
    private TokenUsageMapper tokenUsageMapper;

    @Override
    public TokenStatVO getTokenStat(TokenStatRequest request) {
        String dimension = request.getDimension();
        Integer topN = request.getTopN();

        // 默认值处理
        if (dimension == null || dimension.isEmpty()) {
            dimension = "day";
        }
        if (topN == null || topN <= 0) {
            topN = 10;
        }

        log.info("获取Token统计，维度：{}，TopN：{}", dimension, topN);

        // 1. 获取排名前N的用户
        List<TokenStatVO.UserTokenStat> topUsers = tokenUsageMapper.selectTopUsers(dimension, topN);

        // 2. 获取总消耗量
        TokenStatVO.TotalTokenStat totalStat = tokenUsageMapper.selectTotalStat(dimension);

        // 3. 获取时间维度统计数据（用于图表）
        List<TokenStatVO.TimeDimensionStat> timeDimensionStats = tokenUsageMapper.selectTimeDimensionStat(dimension);

        // 构建返回结果
        if (totalStat == null) {
            totalStat = TokenStatVO.TotalTokenStat.builder()
                    .totalInputToken(0L)
                    .totalOutputToken(0L)
                    .totalToken(0L)
                    .build();
        }
        totalStat.setTimeDimensionStats(timeDimensionStats);

        return TokenStatVO.builder()
                .topUsers(topUsers)
                .totalStat(totalStat)
                .build();
    }
}
