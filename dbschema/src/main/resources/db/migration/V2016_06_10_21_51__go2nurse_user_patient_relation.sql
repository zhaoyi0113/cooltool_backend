CREATE TABLE `go2nurse_user_patient_relation` (
  `id` BIGINT(64) NOT NULL,
  `user_id` BIGINT(64) NULL DEFAULT 0,
  `patient_id` BIGINT(64) NULL DEFAULT 0,
  `status` INT NULL DEFAULT 0,
  `time_created` DATETIME NULL,
  PRIMARY KEY (`id`));
