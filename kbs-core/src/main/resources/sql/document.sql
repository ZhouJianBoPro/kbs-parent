-- ----------------------------
-- 文档表
-- ----------------------------
DROP TABLE IF EXISTS `document`;
CREATE TABLE `document` (
  `id` varchar(19) NOT NULL COMMENT '文档ID（雪花算法）',
  `doc_name` varchar(255) NOT NULL COMMENT '文档名称',
  `doc_type` varchar(50) NOT NULL COMMENT '文档类型（pdf、doc、docx、txt等）',
  `file_size` bigint DEFAULT NULL COMMENT '文件大小（字节）',
  `file_url` varchar(500) NOT NULL COMMENT 'OSS存储路径',
  `file_key` varchar(500) NOT NULL COMMENT 'OSS存储key',
  `status` tinyint DEFAULT 0 COMMENT '文档状态（0-待处理，1-处理中，2-处理成功，3-处理失败）',
  `upload_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `process_time` datetime DEFAULT NULL COMMENT '处理完成时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_user` varchar(50) DEFAULT NULL COMMENT '更新人',
  `del_flag` tinyint DEFAULT 0 COMMENT '删除标志（0-未删除，1-已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_user` (`create_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文档表';
