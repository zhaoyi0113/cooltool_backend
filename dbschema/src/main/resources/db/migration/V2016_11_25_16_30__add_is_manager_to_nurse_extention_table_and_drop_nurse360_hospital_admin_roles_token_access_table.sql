ALTER TABLE `cooltoo_nurse_info_extension`
ADD COLUMN `is_manager` INT(11) NULL DEFAULT 0;

DROP TABLE IF EXISTS `nurse360_hospital_administrator`;
DROP TABLE IF EXISTS `nurse360_hospital_admin_roles`;
DROP TABLE IF EXISTS `nurse360_hospital_admin_access_token`;