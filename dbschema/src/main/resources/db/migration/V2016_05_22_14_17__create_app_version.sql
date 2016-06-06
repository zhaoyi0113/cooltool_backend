CREATE TABLE `platform_version` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `platform_type` INT NULL DEFAULT 0,
  `version` VARCHAR(45) NULL,
  `time_created` DATETIME NULL,
  `status` INT NULL DEFAULT 0,
  PRIMARY KEY (`id`));
