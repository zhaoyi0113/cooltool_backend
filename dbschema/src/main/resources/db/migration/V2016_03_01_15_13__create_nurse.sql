CREATE TABLE `nurse` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `identificate_id` VARCHAR(32) DEFAULT NULL,
  `name` VARCHAR(45) NULL,
  `gender` INT NULL,
  `age` INT NULL,
  `mobile` VARCHAR(45) NULL,
  PRIMARY KEY (`id`));
