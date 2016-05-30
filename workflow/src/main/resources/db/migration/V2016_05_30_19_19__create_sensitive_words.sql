CREATE TABLE `sensitive_words` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `word` VARCHAR(45) NULL,
  `time_created` DATETIME NULL,
  `status` INT NULL,
  `type` INT NULL,
  PRIMARY KEY (`id`));
