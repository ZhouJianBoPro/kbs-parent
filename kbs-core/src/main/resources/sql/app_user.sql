-- ----------------------------
-- 应用用户表
-- ----------------------------
DROP TABLE IF EXISTS `app_user`;
CREATE TABLE `app_user` (
  `id` varchar(19) NOT NULL COMMENT '用户ID（雪花算法）',
  `account_type` char(6) NOT NULL DEFAULT 'NORMAL' COMMENT '账户类型（NORMAL-普通用户，SYSTEM-系统用户）',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `status` tinyint DEFAULT 0 COMMENT '状态（0-正常，1-禁用）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_user` varchar(50) DEFAULT NULL COMMENT '更新人',
  `del_flag` tinyint DEFAULT 0 COMMENT '删除标志（0-未删除，1-已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应用用户表';
