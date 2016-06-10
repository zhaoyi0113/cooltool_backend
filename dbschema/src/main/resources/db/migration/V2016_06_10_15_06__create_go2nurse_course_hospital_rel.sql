CREATE TABLE `go2nurse_course_hospital_relation` (
  `id` BIGINT(64) NOT NULL DEFAULT 0,
  `course_id` BIGINT(64) NULL DEFAULT 0,
  `hospital_id` INT NULL DEFAULT 0,
  `status` INT NULL DEFAULT 0,
  `time_created` DATETIME NULL,
  PRIMARY KEY (`id`));
