DROP TABLE IF EXISTS `go2nurse_user_device_token`;
CREATE TABLE `go2nurse_user_device_token` (
  `id` BIGINT(64) NOT NULL AUTO_INCREMENT,
  `time_created` DATETIME NULL DEFAULT now(),
  `status` INT(11) NULL DEFAULT 0,
  `user_id` BIGINT(64) NULL DEFAULT 0,
  `device_token` VARCHAR(100) NULL DEFAULT '',
  `device_type` INT(11) NULL DEFAULT 0,
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;


ALTER TABLE `go2nurse_consultation_category` ADD COLUMN `icon_id` BIGINT(64) NULL DEFAULT 0;


ALTER TABLE `go2nurse_user_consultation` ADD COLUMN `completed` INT(11) NULL DEFAULT 0;