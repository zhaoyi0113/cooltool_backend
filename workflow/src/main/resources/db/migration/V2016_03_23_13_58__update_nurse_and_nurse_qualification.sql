ALTER TABLE `nurse` ADD COLUMN `real_name` VARCHAR(45) DEFAULT '';
ALTER TABLE `nurse` ADD COLUMN `identification` VARCHAR(45) DEFAULT '';

ALTER TABLE `nurse_qualification` ADD COLUMN `name` VARCHAR(100) DEFAULT '' AFTER `id`;
ALTER TABLE `nurse_qualification` ADD COLUMN `work_file_type` VARCHAR(100) DEFAULT '' AFTER `user_id`;
ALTER TABLE `nurse_qualification` CHANGE COLUMN `id` `id` BIGINT(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `nurse_qualification` DROP COLUMN `real_name`;
ALTER TABLE `nurse_qualification` DROP COLUMN `identification`;
ALTER TABLE `nurse_qualification` DROP COLUMN `identification_file_id`;