ALTER TABLE `file_storage`
CHANGE COLUMN `file_name` `file_real_name` VARCHAR(45) NULL DEFAULT NULL ,
ADD COLUMN `file_path` VARCHAR(45) NULL AFTER `file_real_name`;
