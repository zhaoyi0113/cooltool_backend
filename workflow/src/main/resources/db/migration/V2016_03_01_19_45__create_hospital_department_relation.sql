CREATE TABLE `hospital_department_relation` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `hospital_id` INT NULL,
  `department_id` INT NULL,
  PRIMARY KEY (`id`));
ALTER TABLE `hostipal` RENAME TO  `hospital` ;
ALTER TABLE `hostipal_department` RENAME TO  `hospital_department` ;