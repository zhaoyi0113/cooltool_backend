DROP TABLE IF EXISTS `nurse360_nurse_device_token`;
CREATE TABLE `nurse360_nurse_device_token` (
  `id` BIGINT(64) NOT NULL AUTO_INCREMENT,
  `time_created` DATETIME NULL DEFAULT now(),
  `status` INT(11) NULL DEFAULT 0,
  `user_id` BIGINT(64) NULL DEFAULT 0,
  `device_token` VARCHAR(100) NULL DEFAULT '',
  `device_type` INT(11) NULL DEFAULT 0,
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;