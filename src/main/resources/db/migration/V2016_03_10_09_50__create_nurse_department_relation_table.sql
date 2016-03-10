CREATE TABLE `nurse_department_relation` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(11) NULL,
  `department_id` INT NULL,
  PRIMARY KEY (`id`));