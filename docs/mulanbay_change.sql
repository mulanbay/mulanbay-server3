# 2025-07-19 人生经历消费增加消费时间字段
ALTER TABLE `experience_consume` ADD COLUMN `buy_time` DATETIME NULL AFTER `consume_name`;
update experience_consume ex set ex.buy_time = (select cs.buy_time from consume cs where cs.consume_id = ex.sc_id) where ex.sc_id is not null and ex.consume_id>0;
update experience_consume ex set ex.buy_time = ex.created_time where ex.sc_id is null and ex.consume_id>0;

# 2025-07-20 人生经历消费增加经历编号字段
ALTER TABLE `experience_consume` ADD COLUMN `exp_id` BIGINT NULL AFTER `buy_time`;
update experience_consume ex set ex.exp_id = (select cs.exp_id from experience_detail cs where cs.detail_id = ex.detail_id) where ex.consume_id>0;

# 2025-07-22 根据经历消费更新原始消费信息
update consume c set c.tags = (SELECT ex.exp_name FROM experience_consume ec,experience ex where ec.exp_id = ex.exp_id and ec.sc_id = c.consume_id) where c.consume_id>0;
