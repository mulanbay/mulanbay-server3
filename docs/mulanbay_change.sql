# 2025-07-19人生经历消费增加消费时间字段
ALTER TABLE `experience_consume` ADD COLUMN `buy_time` DATETIME NULL AFTER `consume_name`;
update experience_consume ex set ex.buy_time = (select cs.buy_time from consume cs where cs.consume_id = ex.sc_id) where ex.sc_id is not null and ex.consume_id>0;
update experience_consume ex set ex.buy_time = ex.created_time where ex.sc_id is null and ex.consume_id>0;