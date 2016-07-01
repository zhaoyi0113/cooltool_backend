DROP TABLE IF EXISTS `go2nurse_user_address`;
CREATE TABLE `go2nurse_user_address` (
  `id` BIGINT(64) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(64) NULL DEFAULT 0,
  `province_id` INT(11) NULL DEFAULT 0,
  `city_id` INT(11) NULL DEFAULT 0,
  `address` VARCHAR(200) NULL DEFAULT '',
  `grade` INT(11) NULL DEFAULT 0,
  `time_created` DATETIME NULL DEFAULT now(),
  `status` INT(11) NULL DEFAULT 0,
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;
