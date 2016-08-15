ALTER TABLE `cooltoo_hospital_department` ADD COLUMN `hospital_id` INT(11) NULL DEFAULT 0 AFTER `id`;
DROP TABLE `nursego_nurse_department_relation`;
DROP TABLE `nursego_hospital_department_relation`;