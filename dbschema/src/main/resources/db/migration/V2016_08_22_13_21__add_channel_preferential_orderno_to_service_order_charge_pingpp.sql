ALTER TABLE `go2nurse_service_order`
  ADD COLUMN `preferential_cent` INT(11) NULL DEFAULT 0 AFTER `total_consumption_cent`,
  ADD COLUMN `order_no` VARCHAR(45) NULL DEFAULT '' AFTER `preferential_cent`;

ALTER TABLE `go2nurse_service_order_charge_pingpp`
ADD COLUMN `order_no` VARCHAR(45) NULL DEFAULT '' AFTER `order_id`,
ADD COLUMN `channel` VARCHAR(45) NULL DEFAULT '' AFTER `order_no`;

/* 病人咨询分类 */
DROP TABLE IF EXISTS `go2nurse_consultation_category`;
CREATE TABLE `go2nurse_consultation_category` (
  `id` BIGINT(64) NOT NULL,
  `time_created` DATETIME NULL DEFAULT now(),
  `status` INT(11) NULL DEFAULT 0,
  `name` VARCHAR(100) NULL DEFAULT '',
  `description` TEXT(1000) NULL,
  `image_id` BIGINT(64) NULL DEFAULT 0,
  `grade` INT(11) NULL DEFAULT 0,
PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;
