CREATE TABLE `nurse_hospital_relation` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `nurse_id` BIGINT(11) NULL,
  `hospital_id` INT NULL,
  `department_id` INT NULL,
  PRIMARY KEY (`id`));
