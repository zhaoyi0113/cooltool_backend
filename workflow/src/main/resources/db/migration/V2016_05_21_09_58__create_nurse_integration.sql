CREATE TABLE `nurse_integration` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(11) NULL,
  `user_type` VARCHAR(45) NULL,
  `reason_id` BIGINT(11) NULL,
  `ability_id` BINARY(11) NULL,
  `ability_type` VARCHAR(45) NULL,
  `point` INT NULL,
  `time_created` DATETIME NULL,
  `status` VARCHAR(45) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;
