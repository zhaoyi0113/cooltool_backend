CREATE TABLE `nurse_qualification` (
  `id` BIGINT(11) NOT NULL,
  `user_id` BIGINT(11) NULL,
  `identification` VARCHAR(45) NULL,
  `real_name` VARCHAR(45) NULL,
  `work_file_id` BIGINT(64) NULL,
  `identification_file_id` BIGINT(64) NULL,
  `status` VARCHAR(45) NULL,
  `create_time` DATETIME NULL,
  PRIMARY KEY (`id`));
