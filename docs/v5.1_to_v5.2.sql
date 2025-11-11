# 这是小版本间的sql更新记录
#v5.1-->v5.2
# 2025-07-19 人生经历消费增加消费时间字段
ALTER TABLE `experience_consume` ADD COLUMN `buy_time` DATETIME NULL AFTER `consume_name`;
update experience_consume ex set ex.buy_time = (select cs.buy_time from consume cs where cs.consume_id = ex.sc_id) where ex.sc_id is not null and ex.consume_id>0;
update experience_consume ex set ex.buy_time = ex.created_time where ex.sc_id is null and ex.consume_id>0;

# 2025-07-20 人生经历消费增加经历编号字段
ALTER TABLE `experience_consume` ADD COLUMN `exp_id` BIGINT NULL AFTER `buy_time`;
update experience_consume ex set ex.exp_id = (select cs.exp_id from experience_detail cs where cs.detail_id = ex.detail_id) where ex.consume_id>0;

# 2025-07-22 根据经历消费更新原始消费信息
# update consume c set c.tags = (SELECT ex.exp_name FROM experience_consume ec,experience ex where ec.exp_id = ex.exp_id and ec.sc_id = c.consume_id) where c.consume_id>0 and c.consume_id in (SELECT ex2.sc_id FROM experience_consume ec2);
# 删除人生经历消费，后期统一到原始消费信息中维护
drop table experience_consume;

# 2025-08-24 消费记录增加商品过期类型
ALTER TABLE `consume` ADD COLUMN `invalid_type` SMALLINT NULL AFTER `sold_price`;
update consume set invalid_type = 0 where invalid_time is not null and sold_price is null and consume_id>0;
update consume set invalid_type = 1 where invalid_time is not null and sold_price is not null and consume_id>0;
