DROP TABLE IF EXISTS `go2nurse_doctor`;
CREATE TABLE `go2nurse_doctor` (
  `id` BIGINT(64) NOT NULL AUTO_INCREMENT,
  `time_created` DATETIME NULL DEFAULT now(),
  `status` INT(11) NULL DEFAULT 0,
  `grade` INT(11) NULL DEFAULT 0,
  `name` VARCHAR(45) NULL DEFAULT '',
  `post` VARCHAR(200) NULL DEFAULT '',
  `job_title` TEXT NULL,
  `be_good_at` TEXT NULL,
  `image_id` BIGINT(64) NULL DEFAULT 0,
  `hospital_id` INT(11) NULL DEFAULT 0,
  `department_id` VARCHAR(45) NULL DEFAULT 0,
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;
