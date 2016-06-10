CREATE TABLE `go2nurse_user` (
  `id` BIGINT(64) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NULL,
  `mobile` VARCHAR(45) NULL,
  `status` VARCHAR(45) NULL,
  `time_created` DATETIME NULL,
  `age` INT NULL DEFAULT 0,
  `gender` INT NULL DEFAULT 0,
  PRIMARY KEY (`id`));
