ALTER TABLE `go2nurse_doctor`
CHANGE COLUMN `introduction` `introduction` LONGTEXT NULL DEFAULT NULL ;

ALTER TABLE `cooltoo_hospital_department` ADD COLUMN `transportation` TEXT NULL;