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
