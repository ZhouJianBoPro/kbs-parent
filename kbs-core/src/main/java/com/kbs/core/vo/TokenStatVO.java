package com.kbs.core.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Token统计返回结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenStatVO {

    /**
     * 排名前十的用户Token使用量
     */
    private List<UserTokenStat> topUsers;

    /**
     * 总消耗量统计
     */
    private TotalTokenStat totalStat;

    /**
     * 用户Token使用量
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserTokenStat {
        /**
         * 用户ID
         */
        private String userId;

        /**
         * 用户名
         */
        private String username;

        /**
         * 输入Token
         */
        private Long inputToken;

        /**
         * 输出Token
         */
        private Long outputToken;

        /**
         * 总Token
         */
        private Long totalToken;
    }

    /**
     * 总Token消耗统计
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TotalTokenStat {
        /**
         * 总输入Token
         */
        private Long totalInputToken;

        /**
         * 总输出Token
         */
        private Long totalOutputToken;

        /**
         * 总Token
         */
        private Long totalToken;

        /**
         * 时间维度数据（用于图表展示）
         */
        private List<TimeDimensionStat> timeDimensionStats;
    }

    /**
     * 时间维度统计
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeDimensionStat {
        /**
         * 时间标签（如：2024-01-01）
         */
        private String timeLabel;

        /**
         * 输入Token
         */
        private Long inputToken;

        /**
         * 输出Token
         */
        private Long outputToken;

        /**
         * 总Token
         */
        private Long totalToken;
    }
}
