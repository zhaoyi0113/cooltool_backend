CREATE TABLE `go2nurse_diagnostic_point` (
  `id` BIGINT(64) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NULL,
  `image_id` BIGINT(64) NULL DEFAULT 0,
  `status` INT NULL DEFAULT 0,
  `time_created` VARCHAR(45) NULL,
  PRIMARY KEY (`id`));
