ALTER TABLE `nurse_hospital_relation`
CHANGE COLUMN `nurse_id` `nurse_id` BIGINT(11) NULL DEFAULT 0 ,
CHANGE COLUMN `hospital_id` `hospital_id` INT(11) NULL DEFAULT 0 ,
CHANGE COLUMN `department_id` `department_id` INT(11) NULL DEFAULT 0;

ALTER TABLE `nurse_hospital_relation`
ADD COLUMN `time_created` DATETIME NULL DEFAULT now(),
ADD COLUMN `status` INT(11) NULL DEFAULT 1;