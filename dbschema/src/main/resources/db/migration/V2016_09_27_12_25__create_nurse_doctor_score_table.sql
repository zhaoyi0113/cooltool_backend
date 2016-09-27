CREATE TABLE `go2nurse_nurse_doctor_score` (
  `id` BIGINT(64) NOT NULL AUTO_INCREMENT,
  `time_created` DATETIME NULL DEFAULT now(),
  `status` INT(11) NULL DEFAULT 0,
  `receiver_type` INT(11) NULL DEFAULT 0,
  `receiver_id` BIGINT(64) NULL DEFAULT 0,
  `user_id` BIGINT(64) NULL DEFAULT 0,
  `reason_type` INT(11) NULL DEFAULT 0,
  `reason_id` BIGINT(64) NULL DEFAULT 0,
  `score` FLOAT NULL DEFAULT 0,
  `weight` INT NULL DEFAULT 1,
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;