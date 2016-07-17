
DROP TABLE IF EXISTS `go2nurse_service_order_pingpp`;
CREATE TABLE `go2nurse_service_order_pingpp` (
  `id` BIGINT(64) NOT NULL AUTO_INCREMENT,
  `time_created` DATETIME NULL DEFAULT now(),
  `status` INT(11) NULL DEFAULT 0,
  `app_type` INT(11) NULL DEFAULT 0,
  `pingpp_id` VARCHAR(100) NULL DEFAULT '',
  `pingpp_json` MEDIUMTEXT NULL,
  `pingpp_type` INT(11) NULL DEFAULT 0,
  `order_id` BIGINT(64) NULL DEFAULT 0,
  PRIMARY KEY (`id`))
  DEFAULT CHARACTER SET = utf8;