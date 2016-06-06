CREATE TABLE `nurse_relationship` (
  `id` BIGINT(64) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(64) NULL DEFAULT 0,
  `relative_user_id` BIGINT(64) NULL DEFAULT 0,
  `relationship` INT(11) NULL DEFAULT 0,
  `time_created` DATETIME NULL DEFAULT now(),
  `status` INT(11) NULL DEFAULT 1,
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;
