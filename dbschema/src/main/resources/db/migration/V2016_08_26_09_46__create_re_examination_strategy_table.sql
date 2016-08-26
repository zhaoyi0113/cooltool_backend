CREATE TABLE `go2nurse_re_examination_strategy` (
  `id` BIGINT(64) NOT NULL AUTO_INCREMENT,
  `time_created` DATETIME NULL DEFAULT now(),
  `status` INT(11) NULL DEFAULT 0,
  `department_id` INT(11) NULL DEFAULT 0,
  `re_examination_day` MEDIUMTEXT NULL,
  `recycled` INT(11) NULL DEFAULT 0,
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;