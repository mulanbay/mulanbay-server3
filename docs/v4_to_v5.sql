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

ALTER TABLE `archive`
DROP COLUMN `bean_name`;

ALTER TABLE `account_flow`
    CHANGE COLUMN `id` `flow_id` BIGINT NOT NULL AUTO_INCREMENT ,
    CHANGE COLUMN `name` `flow_name` VARCHAR(45) CHARACTER SET 'utf8mb4' NULL DEFAULT NULL ,
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ;

ALTER TABLE `account_snapshot_info`  RENAME TO  `account_snapshot` ;
ALTER TABLE `account_snapshot`
    CHANGE COLUMN `id` `snapshot_id` BIGINT NOT NULL AUTO_INCREMENT ,
    CHANGE COLUMN `name` `snapshot_name` VARCHAR(45) CHARACTER SET 'utf8mb4' NOT NULL ,
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ;

#更新预算的消费商品类型绑定
update budget set goods_type_id = sub_goods_type_id where sub_goods_type_id is not null and id>0;

ALTER TABLE `budget`
DROP COLUMN `sub_goods_type_id`,
DROP COLUMN `fee_type`,
CHANGE COLUMN `id` `budget_id` BIGINT NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN `name` `budget_name` VARCHAR(45) CHARACTER SET 'utf8mb4' NOT NULL ,
CHANGE COLUMN `keywords` `tags` VARCHAR(45) CHARACTER SET 'utf8mb4' NULL DEFAULT NULL ,
CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ;

ALTER TABLE `budget`
    ADD COLUMN `icg` TINYINT NOT NULL DEFAULT 1 AFTER `goods_type_id`,
CHANGE COLUMN `goods_type_id` `goods_type_id` BIGINT(20) NULL DEFAULT NULL ;

ALTER TABLE `budget_log`
    CHANGE COLUMN `id` `log_id` BIGINT NOT NULL AUTO_INCREMENT ,
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ;

ALTER TABLE `budget_snapshot`
DROP COLUMN `sub_goods_type_id`,
DROP COLUMN `fee_type`,
ADD COLUMN `icg` TINYINT NULL DEFAULT 1 AFTER `goods_type_id`,
CHANGE COLUMN `buss_key` `buss_key` VARCHAR(45) CHARACTER SET 'utf8mb4' NULL DEFAULT NULL AFTER `budget_log_id`,
CHANGE COLUMN `id` `snapshot_id` BIGINT NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN `name` `budget_name` VARCHAR(45) CHARACTER SET 'utf8mb4' NOT NULL ,
CHANGE COLUMN `keywords` `tags` VARCHAR(45) CHARACTER SET 'utf8mb4' NULL DEFAULT NULL ,
CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ;

ALTER TABLE `budget_timeline`
    CHANGE COLUMN `id` `timeline_id` BIGINT NOT NULL AUTO_INCREMENT ,
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ;
ALTER TABLE `account_flow`
    CHANGE COLUMN `flow_name` `account_name` VARCHAR(45) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_0900_ai_ci' NULL DEFAULT NULL ;

ALTER TABLE `score_config`
    CHANGE COLUMN `id` `config_id` BIGINT NOT NULL AUTO_INCREMENT ,
    CHANGE COLUMN `name` `config_name` VARCHAR(45) CHARACTER SET 'utf8mb4' NOT NULL ,
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ;

ALTER TABLE `score_config_group` RENAME TO  `score_group` ;
ALTER TABLE `score_group`
    CHANGE COLUMN `id` `group_id` BIGINT NOT NULL AUTO_INCREMENT ,
    CHANGE COLUMN `name` `group_name` VARCHAR(45) CHARACTER SET 'utf8mb4' NOT NULL ,
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ;

ALTER TABLE `user_score`
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ;
ALTER TABLE `user_score_detail`
    CHANGE COLUMN `score_config_id` `config_id` BIGINT NOT NULL ,
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ;

ALTER TABLE `user_reward_point_record`  RENAME TO  `user_reward` ;
ALTER TABLE `user_reward`
    CHANGE COLUMN `remark` `remark` VARCHAR(200) CHARACTER SET 'utf8mb4' NULL DEFAULT NULL AFTER `message_id`,
    CHANGE COLUMN `reward_source` `source` SMALLINT NOT NULL DEFAULT '0' ;

ALTER TABLE `model_config` RENAME TO  `ai_model` ;
ALTER TABLE `ai_model`
    CHANGE COLUMN `id` `model_id` BIGINT NOT NULL AUTO_INCREMENT ,
    CHANGE COLUMN `name` `model_name` VARCHAR(64) CHARACTER SET 'utf8mb4' NOT NULL ,
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ;

ALTER TABLE `budget_log`
    ADD COLUMN `total_amount` DECIMAL(9,2) NULL AFTER `tr_amount`;

update budget_log set total_amount=nc_amount+bc_amount+tr_amount where log_id>0;

ALTER TABLE `budget_snapshot`
    ADD COLUMN `ac_amount` DECIMAL(9,2) NULL AFTER `icg`,
ADD COLUMN `factor` INT NULL AFTER `ac_amount`,
ADD COLUMN `rate` DECIMAL(9,4) NULL AFTER `factor`,
ADD COLUMN `ic_rate` DECIMAL(9,4) NULL AFTER `rate`,
CHANGE COLUMN `from_id` `budget_id` BIGINT NOT NULL ;

ALTER TABLE `budget_log`
    CHANGE COLUMN `occur_date` `buss_day` DATETIME NOT NULL ;

ALTER TABLE `budget_snapshot`
    ADD COLUMN `buss_day` DATETIME NULL AFTER `buss_key`;

update budget_snapshot ss,budget_log log set ss.buss_day = log.buss_day where log.log_id = ss.budget_Log_id and ss.snapshot_id>0;

ALTER TABLE `budget_log`
    CHANGE COLUMN `period` `stat_period` SMALLINT NOT NULL ;

ALTER TABLE `budget_timeline`
    CHANGE COLUMN `period` `stat_period` SMALLINT NOT NULL ;

ALTER TABLE `budget_snapshot`
    ADD COLUMN `stat_period` SMALLINT(5) NULL AFTER `budget_log_id`;

update budget_snapshot ss,budget_log log set ss.stat_period = log.stat_period where log.log_id = ss.budget_Log_id and ss.snapshot_id>0;

ALTER TABLE `user_reward`
    ADD COLUMN `modify_time` DATETIME NULL AFTER `created_time`;

ALTER TABLE `music_instrument`  RENAME TO  `instrument` ;
ALTER TABLE `instrument`
    CHANGE COLUMN `id` `instrument_id` BIGINT NOT NULL AUTO_INCREMENT ,
    CHANGE COLUMN `name` `instrument_name` VARCHAR(45) CHARACTER SET 'utf8mb4' NOT NULL ,
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ;

ALTER TABLE `music_practice`
    CHANGE COLUMN `id` `practice_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID' ,
    CHANGE COLUMN `music_instrument_id` `instrument_id` BIGINT NOT NULL ,
    CHANGE COLUMN `practice_start_time` `start_time` DATETIME NULL DEFAULT NULL ,
    CHANGE COLUMN `practice_end_time` `end_time` DATETIME NULL DEFAULT NULL ,
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL COMMENT '最后更新时间' ;

ALTER TABLE `music_practice_tune`  RENAME TO  `music_practice_detail` ;

ALTER TABLE `music_practice_detail`
DROP FOREIGN KEY `music_practice_fk1`;
ALTER TABLE `mulanbay_db3`.`music_practice_detail`
    ADD COLUMN `created_time` DATETIME NULL AFTER `remark`,
ADD COLUMN `modify_time` DATETIME NULL AFTER `created_time`,
CHANGE COLUMN `id` `detail_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID' ,
CHANGE COLUMN `music_practice_id` `practice_id` BIGINT NOT NULL COMMENT '练习外键' ,
DROP INDEX `music_practice_fk1` ;

ALTER TABLE `instrument`
    ADD COLUMN `status` SMALLINT(5) NULL DEFAULT 1 AFTER `user_id`;

ALTER TABLE `sport_type` RENAME TO  `sport` ;

ALTER TABLE `sport`
    ADD COLUMN `remark` VARCHAR(200) NULL AFTER `order_index`,
ADD COLUMN `created_time` DATETIME NULL AFTER `remark`,
ADD COLUMN `modify_time` DATETIME NULL AFTER `created_time`,
CHANGE COLUMN `id` `sport_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID' ,
CHANGE COLUMN `name` `sport_name` VARCHAR(64) CHARACTER SET 'utf8mb4' NOT NULL COMMENT '名称' ;

ALTER TABLE `sport_milestone`
    CHANGE COLUMN `id` `milestone_id` BIGINT NOT NULL AUTO_INCREMENT ,
    CHANGE COLUMN `name` `milestone_name` VARCHAR(45) CHARACTER SET 'utf8mb4' NOT NULL ,
    CHANGE COLUMN `sport_type_id` `sport_id` BIGINT NOT NULL ,
    CHANGE COLUMN `kilometres` `value` DECIMAL(5,2) NOT NULL ,
    CHANGE COLUMN `minutes` `duration` INT NULL DEFAULT NULL ,
    CHANGE COLUMN `sport_exercise_id` `exercise_id` BIGINT NULL DEFAULT NULL ,
    CHANGE COLUMN `from_exercise_date` `start_time` DATE NULL DEFAULT NULL ,
    CHANGE COLUMN `to_exercise_date` `end_time` DATE NULL DEFAULT NULL ,
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ;

ALTER TABLE `sport_exercise` RENAME TO  `exercise` ;

ALTER TABLE `exercise`
DROP FOREIGN KEY `FK_pkkjnt72kmbvk9n87ht1wxeq9`;
ALTER TABLE `mulanbay_db3`.`exercise`
    CHANGE COLUMN `remark` `remark` VARCHAR(200) CHARACTER SET 'utf8mb4' NULL DEFAULT NULL COMMENT '备注' AFTER `fast_best`,
    CHANGE COLUMN `id` `exercise_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID' ,
    CHANGE COLUMN `sport_type_id` `sport_id` BIGINT NOT NULL COMMENT '锻炼类型' ,
    CHANGE COLUMN `exercise_date` `exercise_time` DATETIME NOT NULL ,
    CHANGE COLUMN `kilometres` `value` DECIMAL(5,2) NOT NULL COMMENT '锻炼公里数' ,
    CHANGE COLUMN `minutes` `duration` INT NOT NULL COMMENT '锻炼时间' ,
    CHANGE COLUMN `average_heart_rate` `avg_heart_rate` INT NULL DEFAULT NULL COMMENT '平均心率' ,
    CHANGE COLUMN `mileage_best` `value_best` SMALLINT NULL DEFAULT NULL ,
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL COMMENT '最后更新时间' ,
DROP INDEX `FK_pkkjnt72kmbvk9n87ht1wxeq9` ;

ALTER TABLE `body_abnormal_record` RENAME TO  `body_abnormal` ;
ALTER TABLE `body_abnormal`
    CHANGE COLUMN `last_days` `last_days` INT NOT NULL COMMENT '持续天数' AFTER `finish_date`,
    CHANGE COLUMN `important_level` `important` DECIMAL(5,1) NOT NULL COMMENT '重要等级' ,
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ;

ALTER TABLE `body_basic_info` RENAME TO  `body_info` ;

ALTER TABLE `body_info`
    CHANGE COLUMN `record_date` `record_time` DATETIME NOT NULL ,
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL COMMENT '最后更新时间' ;

ALTER TABLE `treat_record` RENAME TO  `treat` ;

ALTER TABLE `treat`
    CHANGE COLUMN `id` `treat_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID' ,
    CHANGE COLUMN `important_level` `important` DECIMAL(5,1) NOT NULL COMMENT '重要等级' ,
    CHANGE COLUMN `diagnosed_disease` `confirm_disease` VARCHAR(32) CHARACTER SET 'utf8mb4' NULL DEFAULT NULL COMMENT '确诊疾病' ,
    CHANGE COLUMN `is_sick` `sick` SMALLINT NOT NULL COMMENT '是否有病' ,
    CHANGE COLUMN `treat_date` `treat_time` DATETIME NOT NULL COMMENT '看病日期' ,
    CHANGE COLUMN `os_name` `os` VARCHAR(45) CHARACTER SET 'utf8mb4' NULL DEFAULT NULL ,
    CHANGE COLUMN `registered_fee` `reg_fee` DECIMAL(9,2) NOT NULL COMMENT '挂号费' ,
    CHANGE COLUMN `medical_insurance_paid_fee` `mi_fee` DECIMAL(9,2) NOT NULL COMMENT '医保担负费用' ,
    CHANGE COLUMN `personal_paid_fee` `pd_fee` DECIMAL(9,2) NOT NULL COMMENT '个人支付费用' ,
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL COMMENT '最后更新时间' ;

ALTER TABLE `treat_drug`
DROP FOREIGN KEY `FK_h13ygwsedoyd7oe9owa4u0nct`;
ALTER TABLE `treat_drug`
    CHANGE COLUMN `id` `drug_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID' ,
    CHANGE COLUMN `treat_record_id` `treat_id` BIGINT NOT NULL ,
    CHANGE COLUMN `name` `drug_name` VARCHAR(64) CHARACTER SET 'utf8mb4' NOT NULL COMMENT '药名称' ,
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL COMMENT '最后更新时间' ,
DROP INDEX `FK_h13ygwsedoyd7oe9owa4u0nct` ;
;

ALTER TABLE `treat_drug_detail`
    CHANGE COLUMN `eu` `eu` VARCHAR(32) CHARACTER SET 'utf8mb4' NULL DEFAULT NULL AFTER `occur_time`,
    CHANGE COLUMN `id` `detail_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID' ,
    CHANGE COLUMN `treat_drug_id` `drug_id` BIGINT NOT NULL ,
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL COMMENT '最后更新时间' ;

ALTER TABLE `treat_operation`
DROP FOREIGN KEY `FK_c2k7741waiya3f10forpyfsv0`;
ALTER TABLE `treat_operation`
    CHANGE COLUMN `id` `operation_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID' ,
    CHANGE COLUMN `treat_record_id` `treat_id` BIGINT NOT NULL ,
    CHANGE COLUMN `name` `operation_name` VARCHAR(64) CHARACTER SET 'utf8mb4' NOT NULL COMMENT '手术名' ,
    CHANGE COLUMN `is_sick` `sick` BIT(1) NOT NULL DEFAULT b'0' ,
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL COMMENT '最后更新时间' ,
DROP INDEX `FK_c2k7741waiya3f10forpyfsv0` ;
;

ALTER TABLE `treat_test`
    CHANGE COLUMN `id` `test_id` BIGINT NOT NULL AUTO_INCREMENT ,
    CHANGE COLUMN `treat_operation_id` `operation_id` BIGINT NOT NULL ,
    CHANGE COLUMN `test_date` `test_time` DATETIME NOT NULL ,
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ;

ALTER TABLE `body_abnormal`  CHANGE COLUMN `last_days` `days` INT NOT NULL COMMENT '持续天数' ;

ALTER TABLE `user_set`
DROP COLUMN `buy_type_id`,
DROP COLUMN `treat_sub_goods_type_id`,
CHANGE COLUMN `treat_buy_type_id` `treat_source_id` INT NULL DEFAULT NULL ;

ALTER TABLE `sleep`
    CHANGE COLUMN `id` `sleep_id` BIGINT NOT NULL AUTO_INCREMENT ,
    CHANGE COLUMN `first_wake_up_time` `fwp_time` DATETIME NULL DEFAULT NULL ,
    CHANGE COLUMN `last_wake_up_time` `lwp_time` DATETIME NULL DEFAULT NULL ,
    CHANGE COLUMN `wake_up_count` `wps` INT NULL DEFAULT NULL ,
    CHANGE COLUMN `total_minutes` `duration` INT NULL DEFAULT NULL ,
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ;

ALTER TABLE `city`
    ADD COLUMN `status` SMALLINT(5) NULL DEFAULT 1 AFTER `zip_code`,
ADD COLUMN `remark` VARCHAR(200) NULL AFTER `status`,
ADD COLUMN `created_time` DATETIME NULL AFTER `remark`,
ADD COLUMN `modify_time` DATETIME NULL AFTER `created_time`,
CHANGE COLUMN `id` `city_id` BIGINT(20) NOT NULL DEFAULT '0' COMMENT 'ID' ,
CHANGE COLUMN `name` `city_name` VARCHAR(20) CHARACTER SET 'utf8mb4' NULL DEFAULT NULL COMMENT 'Name' ,
CHANGE COLUMN `province_id` `province_id` BIGINT(20) NULL DEFAULT NULL COMMENT 'PROVINCE' ;

ALTER TABLE `country`
    ADD COLUMN `created_time` DATETIME NULL AFTER `remark`,
ADD COLUMN `modify_time` DATETIME NULL AFTER `created_time`,
CHANGE COLUMN `id` `country_id` BIGINT(20) NOT NULL ;

ALTER TABLE `district`
    ADD COLUMN `order_index` SMALLINT(5) NULL AFTER `city_id`,
ADD COLUMN `status` SMALLINT(5) NULL AFTER `order_index`,
ADD COLUMN `remark` VARCHAR(200) NULL AFTER `status`,
ADD COLUMN `created_time` DATETIME NULL AFTER `remark`,
ADD COLUMN `modify_time` DATETIME NULL AFTER `created_time`,
CHANGE COLUMN `id` `district_id` BIGINT(20) NOT NULL DEFAULT '0' COMMENT 'ID' ,
CHANGE COLUMN `name` `district_name` VARCHAR(20) CHARACTER SET 'utf8mb4' NULL DEFAULT NULL COMMENT 'NAME' ,
CHANGE COLUMN `city_id` `city_id` BIGINT(20) NULL DEFAULT NULL COMMENT 'CITY' ;

ALTER TABLE `province`
    ADD COLUMN `order_index` SMALLINT(5) NULL AFTER `map_name`,
ADD COLUMN `status` SMALLINT(5) NULL AFTER `order_index`,
ADD COLUMN `remark` VARCHAR(200) NULL AFTER `status`,
ADD COLUMN `created_time` DATETIME NULL AFTER `remark`,
ADD COLUMN `modify_time` DATETIME NULL AFTER `created_time`,
CHANGE COLUMN `id` `province_id` BIGINT(20) NOT NULL COMMENT 'ID' ,
CHANGE COLUMN `name` `province_name` VARCHAR(20) CHARACTER SET 'utf8mb4' NULL DEFAULT NULL COMMENT '名称' ;

ALTER TABLE `life_experience` RENAME TO  `experience` ;
ALTER TABLE `experience`
    CHANGE COLUMN `id` `exp_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID' ,
    CHANGE COLUMN `name` `exp_name` VARCHAR(45) CHARACTER SET 'utf8mb4' NULL DEFAULT NULL ,
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL COMMENT '最后更新时间' ;

ALTER TABLE `life_experience_consume` RENAME TO  `experience_consume` ;
ALTER TABLE `experience_consume`
    CHANGE COLUMN `id` `consume_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID' ,
    CHANGE COLUMN `name` `consume_name` VARCHAR(45) CHARACTER SET 'utf8mb4' NULL DEFAULT NULL ,
    CHANGE COLUMN `life_experience_detail_id` `detail_id` BIGINT NOT NULL COMMENT '人生经历明细' ,
    CHANGE COLUMN `consume_type_id` `goods_type_id` BIGINT NOT NULL COMMENT '消费类型编号' ,
    CHANGE COLUMN `buy_record_id` `sc_id` BIGINT NULL DEFAULT NULL COMMENT '消费记录ID' ,
    CHANGE COLUMN `statable` `stat` BIT(1) NOT NULL COMMENT '是否加入统计' ,
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL COMMENT '最后更新时间' ;

ALTER TABLE `life_experience_detail` RENAME TO  `experience_detail` ;
ALTER TABLE `experience_detail`
    CHANGE COLUMN `id` `detail_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID' ,
    CHANGE COLUMN `life_experience_id` `exp_id` BIGINT NOT NULL COMMENT '人生经历ID' ,
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL COMMENT '最后更新时间' ;

ALTER TABLE `life_experience_sum` RENAME TO  `experience_sum` ;
ALTER TABLE `experience_sum`
    CHANGE COLUMN `id` `sum_id` BIGINT NOT NULL AUTO_INCREMENT ,
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ;

ALTER TABLE `company`
    CHANGE COLUMN `id` `company_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID' ,
    CHANGE COLUMN `name` `company_name` VARCHAR(32) CHARACTER SET 'utf8mb4' NOT NULL COMMENT '名称' ,
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL COMMENT '最后更新时间' ;

#人生经历消费类型与商品类型转换(需要各自匹配转换)
update experience_consume set goods_type_id =13 where goods_type_id=1 and consume_id>0;
update experience_consume set goods_type_id =71 where goods_type_id=2 and consume_id>0;
update experience_consume set goods_type_id =72 where goods_type_id=3 and consume_id>0;
update experience_consume set goods_type_id =73 where goods_type_id=4 and consume_id>0;
update experience_consume set goods_type_id =29 where goods_type_id=5 and consume_id>0;
update experience_consume set goods_type_id =28 where goods_type_id=6 and consume_id>0;
update experience_consume set goods_type_id =30 where goods_type_id=7 and consume_id>0;
update experience_consume set goods_type_id =103 where goods_type_id=26 and consume_id>0;

ALTER TABLE `reading_record`  RENAME TO  `book` ;
ALTER TABLE `book`
    CHANGE COLUMN `id` `book_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID' ,
    CHANGE COLUMN `book_category_id` `cate_id` BIGINT NULL DEFAULT NULL ,
    CHANGE COLUMN `published_year` `publish_year` INT NULL DEFAULT NULL ,
    CHANGE COLUMN `proposed_date` `expert_finish_date` DATE NULL DEFAULT NULL COMMENT '对于未读的书期望时间' ,
    CHANGE COLUMN `finished_date` `finish_date` DATE NULL DEFAULT NULL COMMENT '完成时间' ,
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL COMMENT '最后更新时间' ;

ALTER TABLE `book_category`
    CHANGE COLUMN `id` `cate_id` BIGINT NOT NULL AUTO_INCREMENT ,
    CHANGE COLUMN `name` `cate_name` VARCHAR(45) CHARACTER SET 'utf8mb4' NOT NULL ,
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ;

ALTER TABLE `reading_record_detail` RENAME TO  `read` ;
ALTER TABLE `read`
    CHANGE COLUMN `id` `read_id` BIGINT NOT NULL AUTO_INCREMENT ,
    CHANGE COLUMN `reading_record_id` `book_id` BIGINT NOT NULL ,
    CHANGE COLUMN `minutes` `duration` INT NOT NULL ,
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ;

update book set source=2 where source is null and book_id >0;
ALTER TABLE `book`
    CHANGE COLUMN `language` `language` SMALLINT NULL DEFAULT NULL AFTER `book_type`;

ALTER TABLE `book`
    CHANGE COLUMN `country_id` `country_id` BIGINT(20) NULL DEFAULT NULL ,
    CHANGE COLUMN `book_type` `book_type` SMALLINT(5) NULL DEFAULT NULL ;

ALTER TABLE `read`  RENAME TO  `read_detail` ;
ALTER TABLE `read_detail`
    CHANGE COLUMN `read_id` `detail_id` BIGINT NOT NULL AUTO_INCREMENT ;

ALTER TABLE `dream`
DROP COLUMN `reward_point`,
DROP COLUMN `date_change_history`,
DROP COLUMN `max_money`,
CHANGE COLUMN `id` `dream_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID' ,
CHANGE COLUMN `name` `dream_name` VARCHAR(64) CHARACTER SET 'utf8mb4' NOT NULL COMMENT '梦想名称' ,
CHANGE COLUMN `min_money` `cost` DECIMAL(9,2) NULL DEFAULT NULL COMMENT '最低资金要求' ,
CHANGE COLUMN `important_level` `important` DECIMAL(5,1) NOT NULL COMMENT '重要等级' ,
CHANGE COLUMN `proposed_date` `expect_date` DATE NOT NULL COMMENT '提出日期' ,
CHANGE COLUMN `finished_date` `finish_date` DATE NULL DEFAULT NULL COMMENT '实现日期' ,
CHANGE COLUMN `user_plan_id` `plan_id` BIGINT NULL DEFAULT NULL ,
CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL COMMENT '最后更新时间' ;

ALTER TABLE `dream_remind`
    CHANGE COLUMN `id` `remind_id` BIGINT NOT NULL AUTO_INCREMENT ,
    CHANGE COLUMN `from_proposed_days` `form_expect_days` INT NOT NULL ,
    CHANGE COLUMN `finished_remind` `finish_remind` TINYINT NOT NULL ,
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ;

CREATE TABLE `dream_delay` (
  `delay_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(20) NULL,
  `dream_id` BIGINT(20) NOT NULL,
  `from_date` DATETIME NULL,
  `to_date` DATETIME NULL,
  `remark` VARCHAR(200) NULL,
  `created_time` DATETIME NOT NULL,
  `modify_time` DATETIME NULL,
  PRIMARY KEY (`delay_id`),
  UNIQUE INDEX `delay_id_UNIQUE` (`delay_id` ASC) VISIBLE);

ALTER TABLE `dream_remind`
    CHANGE COLUMN `form_rate` `from_rate` INT NOT NULL ,
    CHANGE COLUMN `form_expect_days` `from_expect_days` INT NOT NULL ;

ALTER TABLE `task_trigger`
    ADD COLUMN `undo_check` TINYINT NOT NULL DEFAULT 0 AFTER `redo_type`;

ALTER TABLE `diet_category`  RENAME TO  `food_category` ;
ALTER TABLE `food_category`
    CHANGE COLUMN `user_id` `cate_id` BIGINT NOT NULL ,
    CHANGE COLUMN `keywords` `tags` VARCHAR(200) CHARACTER SET 'utf8mb4' NOT NULL ,
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ,
DROP PRIMARY KEY,
ADD PRIMARY KEY (`id`, `cate_id`);

ALTER TABLE `diet`
    CHANGE COLUMN `id` `diet_id` BIGINT NOT NULL AUTO_INCREMENT ,
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ;

ALTER TABLE `diet_variety_log`
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ;

ALTER TABLE `food_category`
DROP PRIMARY KEY,
ADD PRIMARY KEY (`id`);
;

ALTER TABLE `food_category` DROP COLUMN `cate_id`;

ALTER TABLE `food_category`
    CHANGE COLUMN `id` `cate_id` BIGINT NOT NULL AUTO_INCREMENT ;

ALTER TABLE `work_overtime`
    CHANGE COLUMN `id` `overtime_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID' ,
    CHANGE COLUMN `work_start_time` `start_time` DATETIME NULL DEFAULT NULL ,
    CHANGE COLUMN `work_end_time` `end_time` DATETIME NULL DEFAULT NULL ,
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL COMMENT '最后更新时间' ;

ALTER TABLE `business_trip`
DROP FOREIGN KEY `FK_ecy3fln5j24pu5jpcfx5mxgm2`;
ALTER TABLE `mulanbay_db3`.`business_trip`
DROP COLUMN `city`,
DROP COLUMN `province`,
DROP COLUMN `country`,
ADD COLUMN `country_id` BIGINT(20) NULL AFTER `company_id`,
ADD COLUMN `province_id` BIGINT(20) NULL AFTER `country_id`,
ADD COLUMN `city_id` BIGINT(20) NULL AFTER `province_id`,
ADD COLUMN `district_id` BIGINT(20) NULL AFTER `city_id`,
CHANGE COLUMN `id` `trip_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID' ,
CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL COMMENT '最后更新时间' ,
DROP INDEX `FK_ecy3fln5j24pu5jpcfx5mxgm2` ;
;

ALTER TABLE `province`
    ADD COLUMN `location` VARCHAR(64) NULL AFTER `map_name`;

ALTER TABLE `city`
    ADD COLUMN `location` VARCHAR(64) NULL AFTER `zip_code`;

ALTER TABLE `district`
    ADD COLUMN `location` VARCHAR(64) NULL AFTER `city_id`;

ALTER TABLE `province`
    ADD COLUMN `code` VARCHAR(32) NULL AFTER `map_name`;

ALTER TABLE `city`
    ADD COLUMN `code` VARCHAR(32) NULL AFTER `zip_code`;

ALTER TABLE `district`
    ADD COLUMN `code` VARCHAR(32) NULL AFTER `city_id`;

ALTER TABLE `province`
    ADD COLUMN `country_id` BIGINT(20) NULL DEFAULT 290 AFTER `map_name`;

ALTER TABLE `experience`
DROP COLUMN `location`,
DROP COLUMN `lc_name`,
ADD COLUMN `country_id` BIGINT(20) NULL DEFAULT 290 AFTER `tags`,
ADD COLUMN `province_id` BIGINT(20) NULL AFTER `country_id`,
ADD COLUMN `city_id` BIGINT(20) NULL AFTER `province_id`,
ADD COLUMN `district_id` BIGINT(20) NULL AFTER `city_id`;

ALTER TABLE `experience_detail`
DROP COLUMN `ac_location`,
DROP COLUMN `arrive_city`,
DROP COLUMN `sc_location`,
DROP COLUMN `start_city`,
DROP COLUMN `country_location`,
ADD COLUMN `start_country_id` BIGINT(20) NULL DEFAULT 290 AFTER `exp_id`,
ADD COLUMN `start_province_id` BIGINT(20) NULL AFTER `start_country_id`,
ADD COLUMN `start_city_id` BIGINT(20) NULL AFTER `start_province_id`,
ADD COLUMN `start_district_id` BIGINT(20) NULL AFTER `start_city_id`,
CHANGE COLUMN `country_id` `arrive_country_id` BIGINT(20) NULL DEFAULT 290 ,
CHANGE COLUMN `province_id` `arrive_province_id` BIGINT(20) NULL DEFAULT NULL ,
CHANGE COLUMN `city_id` `arrive_city_id` BIGINT(20) NULL DEFAULT NULL ,
CHANGE COLUMN `district_id` `arrive_district_id` BIGINT(20) NULL DEFAULT NULL ;

ALTER TABLE `common_record_type`  RENAME TO  `common_data_type` ;
ALTER TABLE `common_data_type`
    ADD COLUMN `remark` VARCHAR(200) NULL AFTER `reward_point`,
ADD COLUMN `created_time` DATETIME NULL AFTER `remark`,
ADD COLUMN `modify_time` DATETIME NULL AFTER `created_time`,
CHANGE COLUMN `id` `type_id` INT NOT NULL ,
CHANGE COLUMN `name` `type_name` VARCHAR(45) CHARACTER SET 'utf8mb4' NOT NULL ;

ALTER TABLE `common_record` RENAME TO  `common_data` ;
ALTER TABLE `common_data`
    CHANGE COLUMN `id` `data_id` BIGINT NOT NULL AUTO_INCREMENT ,
    CHANGE COLUMN `common_record_type_id` `type_id` INT NULL DEFAULT NULL ,
    CHANGE COLUMN `name` `data_name` VARCHAR(45) CHARACTER SET 'utf8mb4' NOT NULL ,
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ;

ALTER TABLE `common_data_type`
    CHANGE COLUMN `type_id` `type_id` BIGINT(20) NOT NULL AUTO_INCREMENT ;

ALTER TABLE `common_data_type`
    ADD COLUMN `group_name` VARCHAR(45) NULL AFTER `user_id`;

update common_data_type set group_name ='默认' where type_id>0;

#转换统计配置
ALTER TABLE `stat_value_config`
    ADD COLUMN `config_value` VARCHAR(1000) NULL AFTER `prompt_msg`;

update stat_value_config set config_value=sql_content where source=0 and id>0;
update stat_value_config set config_value=enum_class where source=1 and id>0;
update stat_value_config set config_value=dict_group_code where source=2 and id>0;
update stat_value_config set config_value=json_data where source=3 and id>0;

ALTER TABLE `stat_value_config` RENAME TO  `stat_bind_config` ;

ALTER TABLE `stat_bind_config`
DROP COLUMN `json_data`,
DROP COLUMN `dict_group_code`,
DROP COLUMN `enum_class`,
DROP COLUMN `sql_content`,
ADD COLUMN `remark` VARCHAR(200) NULL AFTER `msg`,
ADD COLUMN `created_time` DATETIME NULL AFTER `remark`,
ADD COLUMN `modify_time` DATETIME NULL AFTER `created_time`,
CHANGE COLUMN `config_value` `config_value` VARCHAR(1000) NULL DEFAULT NULL AFTER `fid`,
CHANGE COLUMN `id` `config_id` BIGINT NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN `name` `config_name` VARCHAR(45) CHARACTER SET 'utf8mb4' NOT NULL ,
CHANGE COLUMN `prompt_msg` `msg` VARCHAR(45) CHARACTER SET 'utf8mb4' NOT NULL ;

ALTER TABLE `notify_config` RENAME TO  `stat_template` ;

ALTER TABLE `stat_template`
DROP COLUMN `tab_name`,
CHANGE COLUMN `status` `status` SMALLINT NOT NULL COMMENT '状态' AFTER `order_index`,
CHANGE COLUMN `id` `template_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键' ,
CHANGE COLUMN `name` `template_name` VARCHAR(45) CHARACTER SET 'utf8mb4' NOT NULL COMMENT '名称' ,
CHANGE COLUMN `notify_type` `compare_type` SMALLINT NULL DEFAULT NULL COMMENT '提醒类型:0告警类1完成类' ,
CHANGE COLUMN `related_beans` `bean_name` VARCHAR(200) CHARACTER SET 'utf8mb4' NULL DEFAULT NULL ,
CHANGE COLUMN `reward_point` `rewards` INT NOT NULL DEFAULT '0' ,
CHANGE COLUMN `default_calendar_title` `calendar_title` VARCHAR(45) CHARACTER SET 'utf8mb4' NULL DEFAULT NULL ,
CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ;

ALTER TABLE `user_notify`  RENAME TO  `user_stat` ;

ALTER TABLE `user_stat`
DROP COLUMN `show_in_index`,
CHANGE COLUMN `id` `stat_id` BIGINT NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN `notify_config_id` `template_id` BIGINT NOT NULL ,
CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ;

ALTER TABLE `user_notify_remind` RENAME TO  `User_stat_remind` ;
ALTER TABLE `User_stat_remind`
    CHANGE COLUMN `id` `remind_id` BIGINT NOT NULL AUTO_INCREMENT ,
    CHANGE COLUMN `user_notify_id` `stat_id` BIGINT NOT NULL ,
    CHANGE COLUMN `over_warning_remind` `owr` TINYINT NOT NULL ,
    CHANGE COLUMN `over_alert_remind` `oar` TINYINT NOT NULL ,
    CHANGE COLUMN `last_modify_time` `modify_time` DATETIME NULL DEFAULT NULL ;

ALTER TABLE `user_set`
DROP COLUMN `score_group`,
ADD COLUMN `score_group_id` BIGINT(20) NULL DEFAULT 1 AFTER `send_wx`;

ALTER TABLE `stat_template`
DROP COLUMN `bean_name`,
ADD COLUMN `buss_type` SMALLINT(5) NOT NULL DEFAULT 0 AFTER `user_field`;

ALTER TABLE `stat_bind_config`
    CHANGE COLUMN `cas_cade_type` `cascade_type` TINYINT NOT NULL DEFAULT '0' ;

ALTER TABLE `user_stat`
DROP COLUMN `warning_value`,
CHANGE COLUMN `alert_value` `expect_value` BIGINT(20) NOT NULL DEFAULT '0' ;

ALTER TABLE `User_stat_remind`
DROP COLUMN `oar`,
DROP COLUMN `owr`,
ADD COLUMN `over_rate` INT NOT NULL DEFAULT 80 AFTER `trigger_interval`;

ALTER TABLE `stat_template`
DROP COLUMN `compare_type`;

ALTER TABLE`user_stat`
    ADD COLUMN `compare_type` SMALLINT(5) NOT NULL DEFAULT 1 AFTER `expect_value`;

CREATE TABLE `user_stat_timeline` (
 `timeline_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
 `user_id` BIGINT(20) NOT NULL,
 `stat_id` BIGINT(20) NOT NULL,
 `value` BIGINT(20) NULL,
 `name_value` VARCHAR(200) NULL,
 `expect_value` BIGINT(20) NULL,
 `unit` VARCHAR(45) NULL,
 `remark` VARCHAR(200) NULL,
 `created_time` DATETIME NOT NULL,
 `modify_time` DATETIME NULL,
 PRIMARY KEY (`timeline_id`));
