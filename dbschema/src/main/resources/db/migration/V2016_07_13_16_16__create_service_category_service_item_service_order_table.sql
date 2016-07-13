/* 陪护服务分类 */
DROP TABLE IF EXISTS `go2nurse_service_category`;
CREATE TABLE `go2nurse_service_category` (
  `id` BIGINT(64) NOT NULL,
  `time_created` DATETIME NULL DEFAULT now(),
  `status` INT(11) NULL DEFAULT 0,
  `name` VARCHAR(100) NULL DEFAULT '',
  `description` TEXT(1000) NULL,
  `image_id` BIGINT(64) NULL DEFAULT 0,
  `grade` INT(11) NULL DEFAULT 0,
  `parent_id` BIGINT(64) NULL DEFAULT 0,
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;

/* 陪护服务选项 */
DROP TABLE IF EXISTS `go2nurse_service_item`;
CREATE TABLE `go2nurse_service_item` (
  `id` BIGINT(64) NOT NULL,
  `time_created` DATETIME NULL DEFAULT now(),
  `status` INT(11) NULL DEFAULT 0,
  `category_id` BIGINT(64) NULL DEFAULT 0,
  `name` VARCHAR(100) NULL DEFAULT '',
  `class` INT(11) NULL DEFAULT 0,
  `description` TEXT(1000) NULL,
  `image_id` BIGINT(64) NULL DEFAULT 0,
  `service_price` DECIMAL(10,2) NULL DEFAULT 0.00,
  `service_time_duration` INT(11) NULL DEFAULT 0,
  `service_time_unit` INT(11) NULL DEFAULT 0,
  PRIMARY KEY (`id`))
  DEFAULT CHARACTER SET = utf8;

/* 订单 */
DROP TABLE IF EXISTS `go2nurse_service_order`;
CREATE TABLE `go2nurse_service_order` (
  `id` BIGINT(64) NOT NULL,
  `time_created` DATETIME NULL DEFAULT now(),
  `status` INT(11) NULL DEFAULT 0,
  `service_item_id` BIGINT(64) NULL DEFAULT 0,
  `user_id` BIGINT(64) NULL DEFAULT 0,
  `patient_id` BIGINT(64) NULL DEFAULT 0,
  `address_id` BIGINT(64) NULL DEFAULT 0,
  `service_start_time` DATETIME NULL DEFAULT now(),
  `service_time_duration` INT(11) NULL DEFAULT 0,
  `service_time_unit` INT(11) NULL DEFAULT 0,
  `total_consumption` DECIMAL(10,2) NULL DEFAULT 0.00,
  `order_status` INT(11) NULL DEFAULT 0,
  `pay_time` DATETIME NULL,
  `payment_amount` DECIMAL(10,2) NULL DEFAULT 0.00,
  PRIMARY KEY (`id`))
  DEFAULT CHARACTER SET = utf8;
