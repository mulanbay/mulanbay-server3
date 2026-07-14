#2026-07-14 增加资源表
CREATE TABLE `resources` (
 `res_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
 `type` smallint NOT NULL COMMENT '类型',
 `refer_id` bigint NOT NULL ,
 `buss_source` smallint NOT NULL ,
 `path` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '路径',
 `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
 `created_time` datetime DEFAULT NULL,
 `modify_time` datetime DEFAULT NULL,
 PRIMARY KEY (`res_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='资源表';