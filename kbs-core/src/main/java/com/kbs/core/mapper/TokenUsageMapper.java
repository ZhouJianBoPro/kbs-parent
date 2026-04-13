package com.kbs.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kbs.core.entity.TokenUsage;
import com.kbs.core.vo.TokenStatVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Token使用记录 Mapper 接口
 */
@Mapper
public interface TokenUsageMapper extends BaseMapper<TokenUsage> {

    /**
     * 统计排名前N的用户
     *
     * @param dimension 统计维度：day、week、month、year
     * @param topN     排名前N
     * @return 用户Token统计列表
     */
    List<TokenStatVO.UserTokenStat> selectTopUsers(@Param("dimension") String dimension, @Param("topN") Integer topN);

    /**
     * 统计总消耗量
     *
     * @param dimension 统计维度：day、month、year
     * @return 总消耗量统计
     */
    TokenStatVO.TotalTokenStat selectTotalStat(@Param("dimension") String dimension);

    /**
     * 按时间维度统计Token使用量
     *
     * @param dimension 统计维度：day、month、year
     * @return 时间维度统计列表
     */
    List<TokenStatVO.TimeDimensionStat> selectTimeDimensionStat(@Param("dimension") String dimension);
}
