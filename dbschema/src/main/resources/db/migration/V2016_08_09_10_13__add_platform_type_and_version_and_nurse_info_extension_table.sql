ALTER TABLE `nursego_user_suggestion`
CHANGE COLUMN `suggestion` `suggestion` VARCHAR(1000) NULL DEFAULT '' AFTER `status`,
CHANGE COLUMN `user_id` `user_id` BIGINT(64) NOT NULL DEFAULT '0' AFTER `suggestion`,
ADD COLUMN `user_type` INT(11) NULL DEFAULT 0 AFTER `user_id`,
ADD COLUMN `user_name` VARCHAR(45) NULL DEFAULT '' AFTER `user_type`,
ADD COLUMN `platform` INT(11) NULL DEFAULT 0 AFTER `user_name`,
ADD COLUMN `version` VARCHAR(45) NULL DEFAULT '' AFTER `platform`;

CREATE TABLE `cooltoo_nurse_info_extension` (
  `id` BIGINT(64) NOT NULL AUTO_INCREMENT,
  `time_created` DATETIME NULL DEFAULT now(),
  `status` INT(11) NULL DEFAULT 0,
  `nurse_id` BIGINT(64) NULL DEFAULT 0,
  `answer_nursing_questions` INT(11) NULL DEFAULT 0,
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;