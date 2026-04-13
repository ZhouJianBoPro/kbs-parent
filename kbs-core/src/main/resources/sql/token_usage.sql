-- ----------------------------
-- Token使用记录表
-- ----------------------------
DROP TABLE IF EXISTS `token_usage`;
CREATE TABLE `token_usage` (
  `id` varchar(19) NOT NULL COMMENT '主键ID（雪花算法）',
  `conversation_id` varchar(19) NOT NULL COMMENT '会话ID',
  `user_id` varchar(19) NOT NULL COMMENT '用户ID',
  `input_token` int NOT NULL DEFAULT 0 COMMENT '输入Token数量',
  `output_token` int NOT NULL DEFAULT 0 COMMENT '输出Token数量',
  `total_token` int NOT NULL DEFAULT 0 COMMENT '总Token数量',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_user` varchar(50) DEFAULT NULL COMMENT '更新人',
  `del_flag` tinyint DEFAULT 0 COMMENT '删除标志（0-未删除，1-已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_conversation_id` (`conversation_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Token使用记录表';
