CREATE TABLE `patient_badge` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `patient_id` BIGINT(32) NOT NULL,
  `badge_id` INT NOT NULL,
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;