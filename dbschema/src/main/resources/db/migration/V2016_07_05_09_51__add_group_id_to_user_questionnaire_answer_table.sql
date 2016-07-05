ALTER TABLE `go2nurse_user_questionnaire_answer` ADD COLUMN `group_id` BIGINT(64) NULL DEFAULT 0 AFTER `id`;
ALTER TABLE  `go2nurse_user_re_examination` ADD COLUMN `has_operation` INT(11) NULL DEFAULT 0;

DROP TABLE IF EXISTS `go2nurse_questionnaire_category`;
CREATE TABLE IF NOT EXISTS `go2nurse_questionnaire_category` (
  `id`           INT(11) NOT NULL AUTO_INCREMENT,
  `time_created` DATETIME     NULL DEFAULT now(),
  `status`       INT(11)      NULL DEFAULT 1,
  `name`         VARCHAR(200) NULL DEFAULT '',
  `introduction` VARCHAR(500) NULL DEFAULT '',
  PRIMARY KEY (`id`))
  DEFAULT CHARACTER SET = utf8;