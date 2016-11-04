CREATE TABLE `go2nurse_nurse_push_course` (
  `id` BIGINT(64) NOT NULL AUTO_INCREMENT,
  `time_created` DATETIME NULL DEFAULT now(),
  `read_status` INT(11) NULL DEFAULT 0,
  `nurse_id` BIGINT(64) NULL DEFAULT 0,
  `user_id` BIGINT(64) NULL DEFAULT 0,
  `patient_id` BIGINT(64) NULL DEFAULT 0,
  `course_id` BIGINT(64) NULL DEFAULT 0,
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;