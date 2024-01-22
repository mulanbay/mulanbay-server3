#4.0版本向5.0版本数据库迁移脚本
#调度
ALTER TABLE `task_server`
    CHANGE COLUMN `support_distri` `distriable` TINYINT NULL DEFAULT NULL ,
    CHANGE COLUMN `last_update_time` `modify_time` DATETIME NULL DEFAULT NULL ;

ALTER TABLE `task_trigger`
DROP COLUMN `sub_task_names`,
DROP COLUMN `sub_task_codes`;

ALTER TABLE `system_function` RENAME TO  `sys_func` ;
ALTER TABLE `sys_func`
DROP COLUMN `short_name`,
CHANGE COLUMN `order_index` `order_index` INT NOT NULL AFTER `id_field_type`,
CHANGE COLUMN `id` `func_id` BIGINT NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN `name` `func_name` VARCHAR(45) CHARACTER SET 'utf8mb4' NOT NULL ,
CHANGE COLUMN `function_type` `func_type` SMALLINT NOT NULL ,
CHANGE COLUMN `function_data_type` `func_data_type` SMALLINT NOT NULL DEFAULT '0' ,
CHANGE COLUMN `record_return_data` `log_res` BIT(1) NOT NULL DEFAULT b'0' ,
CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ;

ALTER TABLE `error_code_define`
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ;

ALTER TABLE `operation_log` RENAME TO  `oper_log` ;
ALTER TABLE `oper_log`
    CHANGE COLUMN `system_function_id` `sys_func_id` BIGINT NULL DEFAULT NULL ,
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ;

ALTER TABLE `role`
    CHANGE COLUMN `id` `role_id` BIGINT NOT NULL AUTO_INCREMENT ,
    CHANGE COLUMN `name` `role_name` VARCHAR(45) CHARACTER SET 'utf8mb4' NOT NULL ,
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ;

ALTER TABLE `system_log` RENAME TO  `sys_log` ;
ALTER TABLE `sys_log`
    CHANGE COLUMN `system_function_id` `sys_func_id` BIGINT NULL DEFAULT NULL ,
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ;

ALTER TABLE `family`
    CHANGE COLUMN `id` `family_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID' ,
    CHANGE COLUMN `name` `family_name` VARCHAR(100) CHARACTER SET 'utf8mb4' NOT NULL COMMENT '名称' ,
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL COMMENT '最后更新时间' ;

ALTER TABLE `family_user`
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL COMMENT '最后更新时间' ;

ALTER TABLE `user`
    CHANGE COLUMN `id` `user_id` BIGINT NOT NULL AUTO_INCREMENT ,
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ;

ALTER TABLE `user_setting`  RENAME TO  `user_set` ;
ALTER TABLE `user_set`
DROP COLUMN `stat_score`,
DROP COLUMN `id`,
CHANGE COLUMN `send_wx_message` `send_wx` TINYINT NOT NULL ,
DROP PRIMARY KEY,
ADD PRIMARY KEY (`user_id`),
ADD UNIQUE INDEX `user_id_UNIQUE` (`user_id` ASC) VISIBLE;
;

ALTER TABLE `user_message`
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ;

ALTER TABLE `system_monitor_user`
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ;

ALTER TABLE `system_monitor_user` RENAME TO `monitor_user` ;

ALTER TABLE `dict_group`
    CHANGE COLUMN `id` `group_id` BIGINT NOT NULL AUTO_INCREMENT ,
    CHANGE COLUMN `name` `group_name` VARCHAR(45) CHARACTER SET 'utf8mb4' NOT NULL ;

ALTER TABLE `dict_item`
    CHANGE COLUMN `id` `item_id` BIGINT NOT NULL AUTO_INCREMENT ,
    CHANGE COLUMN `name` `item_name` VARCHAR(45) CHARACTER SET 'utf8mb4' NOT NULL ;

ALTER TABLE `level_config` RENAME TO  `user_level` ;
ALTER TABLE `user_level`
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL COMMENT '最后更新时间' ;

ALTER TABLE `user_set`
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ;

ALTER TABLE `task_log`
    CHANGE COLUMN `id` `log_id` BIGINT NOT NULL AUTO_INCREMENT ;

ALTER TABLE `task_trigger`
    CHANGE COLUMN `id` `trigger_id` BIGINT NOT NULL AUTO_INCREMENT ,
    CHANGE COLUMN `name` `trigger_name` VARCHAR(64) CHARACTER SET 'utf8mb4' NOT NULL ;

ALTER TABLE `task_server`
    CHANGE COLUMN `id` `server_id` BIGINT NOT NULL AUTO_INCREMENT ;

ALTER TABLE `command_config`
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ;

ALTER TABLE `task_trigger` DROP COLUMN `user_id`;
ALTER TABLE `error_code_define`
    CHANGE COLUMN `realtime_notify` `realtime` TINYINT NOT NULL ;
ALTER TABLE `error_code_define` RENAME TO  `sys_code` ;

ALTER TABLE `user_message` RENAME TO `message` ;
ALTER TABLE `message`
    CHANGE COLUMN `id` `msg_id` BIGINT NOT NULL AUTO_INCREMENT ,
    CHANGE COLUMN `message_type` `msg_type` SMALLINT NOT NULL ;

ALTER TABLE `user_wxpay_info` RENAME TO  `wx_account` ;
ALTER TABLE `wx_account`
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ;

ALTER TABLE `sys_func`
    CHANGE COLUMN `error_code` `code` INT NOT NULL DEFAULT '0' ;

ALTER TABLE `command_config` RENAME TO  `command` ;

ALTER TABLE `database_clean`  RENAME TO  `db_clean` ;

ALTER TABLE `db_clean`
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ;

ALTER TABLE `fast_menu`
    CHANGE COLUMN `function_id` `menu_id` BIGINT NOT NULL ;

ALTER TABLE `buy_type` RENAME TO  `consume_source` ;

ALTER TABLE `consume_source`
ADD COLUMN `remark` VARCHAR(200) NULL AFTER `order_index`,
ADD COLUMN `created_time` DATETIME NULL AFTER `remark`,
ADD COLUMN `modify_time` DATETIME NULL AFTER `created_time`,
CHANGE COLUMN `id` `source_id` BIGINT(20) NOT NULL COMMENT 'ID' ,
CHANGE COLUMN `name` `source_name` VARCHAR(32) CHARACTER SET 'utf8mb4' NOT NULL COMMENT '名称' ;

ALTER TABLE `consume_source`
    CHANGE COLUMN `source_id` `source_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID' ;

ALTER TABLE `goods_type`
    ADD COLUMN `remark` VARCHAR(200) NULL AFTER `tags`,
ADD COLUMN `created_time` DATETIME NULL AFTER `remark`,
ADD COLUMN `modify_time` DATETIME NULL AFTER `created_time`,
CHANGE COLUMN `id` `type_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID' ,
CHANGE COLUMN `name` `type_name` VARCHAR(32) CHARACTER SET 'utf8mb4' NOT NULL COMMENT '名称' ;

ALTER TABLE `goods_type`
    CHANGE COLUMN `statable` `stat` BIT(1) NULL DEFAULT NULL ;


ALTER TABLE `buy_record`  RENAME TO `consume` ;

#更新商品类型
update consume set goods_type_id = sub_goods_type_id where sub_goods_type_id is not null and id>0;

CREATE TABLE `consume_refer` (
`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
`refer_id` BIGINT(20) NOT NULL,
`type` SMALLINT(5) NOT NULL,
`remark` VARCHAR(200) NULL,
`created_time` DATETIME NOT NULL,
`modify_time` DATETIME NULL,
PRIMARY KEY (`id`));

ALTER TABLE `consume_refer`
    ADD COLUMN `consume_id` BIGINT(20) NOT NULL AFTER `id`;

#转移看病记录数据到关联表
insert into consume_refer(consume_id,type,refer_id,created_time) select consume_id,0,treat_record_id,now() from consume where treat_record_id is not null;

ALTER TABLE `consume`
DROP COLUMN `treat_record_id`,
DROP COLUMN `status`,
CHANGE COLUMN `user_id` `user_id` BIGINT NOT NULL AFTER `goods_type_id`,
CHANGE COLUMN `sku_info` `sku_info` VARCHAR(100) CHARACTER SET 'utf8mb4' NULL DEFAULT NULL AFTER `shop_name`,
CHANGE COLUMN `secondhand` `secondhand` BIT(1) NOT NULL AFTER `brand`,
CHANGE COLUMN `id` `consume_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID' ,
CHANGE COLUMN `buy_type_id` `source_id` BIGINT(20) NOT NULL ,
CHANGE COLUMN `goods_type_id` `goods_type_id` BIGINT(20) NOT NULL ,
CHANGE COLUMN `buy_date` `buy_time` DATETIME NULL DEFAULT NULL COMMENT '购买日期' ,
CHANGE COLUMN `consume_date` `consume_time` DATETIME NULL DEFAULT NULL ,
CHANGE COLUMN `delete_date` `invalid_time` DATETIME NULL DEFAULT NULL ,
CHANGE COLUMN `expect_delete_date` `expert_invalid_time` DATETIME NULL DEFAULT NULL ,
CHANGE COLUMN `use_time` `duration` BIGINT NULL DEFAULT NULL ,
CHANGE COLUMN `keywords` `tags` VARCHAR(64) CHARACTER SET 'utf8mb4' NULL DEFAULT NULL ,
CHANGE COLUMN `statable` `stat` BIT(1) NULL DEFAULT NULL ,
CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL COMMENT '最后更新时间' ;

ALTER TABLE `consume`
    CHANGE COLUMN `sku_info` `sku` VARCHAR(100) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_0900_ai_ci' NULL DEFAULT NULL AFTER `goods_name`;

ALTER TABLE `account`
    CHANGE COLUMN `id` `account_id` BIGINT NOT NULL AUTO_INCREMENT ,
    CHANGE COLUMN `name` `account_name` VARCHAR(45) CHARACTER SET 'utf8mb4' NOT NULL ,
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ;

#转移收入数据到关联表
insert into consume_refer(consume_id,type,refer_id,created_time) select buy_record_id,13,id,now() from income where buy_record_id is not null;

ALTER TABLE `income`
DROP COLUMN `buy_record_id`,
CHANGE COLUMN `id` `income_id` BIGINT NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN `name` `income_name` VARCHAR(200) CHARACTER SET 'utf8mb4' NOT NULL ,
CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ;

ALTER TABLE `goods_lifetime`
    CHANGE COLUMN `id` `lifetime_id` BIGINT NOT NULL AUTO_INCREMENT ,
    CHANGE COLUMN `name` `lifetime_name` VARCHAR(32) CHARACTER SET 'utf8mb4' NOT NULL ,
    CHANGE COLUMN `keywords` `tags` VARCHAR(100) CHARACTER SET 'utf8mb4' NOT NULL ,
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ;

ALTER TABLE =`buy_record_match_log`  RENAME TO `consume_match_log` ;
ALTER TABLE `consume_match_log`
DROP COLUMN `brand`,
DROP COLUMN `shop_name`,
DROP COLUMN `sub_goods_type_id`,
DROP COLUMN `goods_type_id`,
DROP COLUMN `buy_record_id`,
ADD COLUMN `consume_id` BIGINT(20) NOT NULL AFTER `user_id`,
ADD COLUMN `consume_data` VARCHAR(2000) NULL AFTER `consume_id`,
ADD COLUMN `compare_data` VARCHAR(2000) NULL AFTER `compare_id`,
ADD COLUMN `match_type` SMALLINT(5) NOT NULL AFTER `compare_data`,
CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ;

ALTER TABLE `consume_match_log` RENAME TO `match_log` ;

ALTER TABLE `life_archives` RENAME TO `archive` ;
ALTER TABLE `archive`
    CHANGE COLUMN `id` `archive_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID' ,
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL COMMENT '最后更新时间' ;
ALTER TABLE `archive`
    CHANGE COLUMN `related_beans` `bean_name` VARCHAR(45) CHARACTER SET 'utf8mb4' NULL DEFAULT NULL COMMENT '关联类' ;







