-- ----------------------------
-- 用户会话表
-- ----------------------------
DROP TABLE IF EXISTS `user_conversation`;
CREATE TABLE `user_conversation` (
  `id` varchar(19) NOT NULL COMMENT '会话ID（雪花算法）',
  `user_id` varchar(19) NOT NULL COMMENT '用户ID',
  `title` varchar(255) DEFAULT NULL COMMENT '会话标题',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_user` varchar(50) DEFAULT NULL COMMENT '更新人',
  `del_flag` tinyint DEFAULT 0 COMMENT '删除标志（0-未删除，1-已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户会话表';
