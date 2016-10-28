CREATE TABLE `go2nurse_course_order` (
  `id` BIGINT(64) NOT NULL,
  `time_created` DATETIME NULL DEFAULT now(),
  `status` INT(11) NULL DEFAULT 0,
  `course_id` BIGINT(64) NULL DEFAULT 0,
  `hospital_id` INT(11) NULL DEFAULT 0,
  `department_id` INT(11) NULL DEFAULT 0,
  `category_id` BIGINT(64) NULL DEFAULT 0,
  `course_order` INT(11) NULL DEFAULT 0,
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;