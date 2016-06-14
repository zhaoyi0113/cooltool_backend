ALTER TABLE `go2nurse_user_patient_relation` 
CHANGE COLUMN `id` `id` BIGINT(64) NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN `status` `status` INT(11) NULL DEFAULT 1 ,
CHANGE COLUMN `time_created` `time_created` DATETIME NULL DEFAULT now() ;