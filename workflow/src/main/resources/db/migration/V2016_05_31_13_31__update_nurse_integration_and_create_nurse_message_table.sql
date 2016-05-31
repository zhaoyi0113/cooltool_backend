ALTER TABLE `nurse_integration`
  CHANGE COLUMN `id` `id` BIGINT(64) NOT NULL AUTO_INCREMENT,
  CHANGE COLUMN `user_id` `user_id` BIGINT(64) NOT NULL DEFAULT 0,
  CHANGE COLUMN `user_type` `user_type` INT(11) NULL DEFAULT 0,
  CHANGE COLUMN `reason_id` `reason_id` BIGINT(64) NULL DEFAULT 0,
  CHANGE COLUMN `ability_id` `ability_id` INT(11) NULL DEFAULT 0,
  CHANGE COLUMN `ability_type` `ability_type` INT(11) NULL DEFAULT 0,
  CHANGE COLUMN `point` `point` BIGINT(64) NULL DEFAULT 0,
  CHANGE COLUMN `time_created` `time_created` DATETIME NULL DEFAULT now(),
  CHANGE COLUMN `status` `status` INT(11) NULL DEFAULT 0;

CREATE TABLE IF NOT EXISTS `nurse_message` (
  `id` BIGINT(64) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(64) NOT NULL DEFAULT 0,
  `user_type` INT(11) NULL DEFAULT 0,
  `reason_id` BIGINT(64) NULL DEFAULT 0,
  `ability_id` INT(11) NULL DEFAULT 0,
  `ability_type` INT(11) NULL DEFAULT 0,
  `time_created` DATETIME NULL DEFAULT now(),
  `status` INT(11) NULL DEFAULT 0,
  PRIMARY KEY (`id`))
  DEFAULT CHARACTER SET = utf8;
