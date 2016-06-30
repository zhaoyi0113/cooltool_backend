ALTER TABLE `go2nurse_user` ADD COLUMN `has_decide` INT(11) NULL DEFAULT 0;
ALTER TABLE `go2nurse_user_diagnostic_point_relation` ADD COLUMN `group_id` BIGINT(64) NULL DEFAULT 0;
ALTER TABLE `go2nurse_user_hospitalized_relation` ADD COLUMN `has_leave` INT(11)    NULL DEFAULT 0;
ALTER TABLE `go2nurse_user_hospitalized_relation` ADD COLUMN `group_id`  BIGINT(64) NULL DEFAULT 0;


CREATE TABLE `go2nurse_user_re_examination` (
  `id` BIGINT(64) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(64) NULL DEFAULT 0,
  `hospitalized_group_id` BIGINT(64) NULL DEFAULT 0,
  `re_examination_start_date` DATETIME NULL DEFAULT now(),
  `time_created` DATETIME NULL DEFAULT now(),
  `status` INT(11) NULL DEFAULT 0,
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;