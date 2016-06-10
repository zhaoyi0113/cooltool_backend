CREATE TABLE `go2nurse_course_diagnostic_relation` (
  `id` BIGINT(64) NOT NULL AUTO_INCREMENT,
  `course_id` BIGINT(64) NULL DEFAULT 0,
  `diagnostic_id` BIGINT(64) NULL DEFAULT 0,
  `status` INT NULL DEFAULT 0,
  `time_created` DATETIME NULL,
  PRIMARY KEY (`id`));
