ALTER TABLE `hospital_department` ADD COLUMN `description`      VARCHAR(1000) NULL DEFAULT '';
ALTER TABLE `hospital_department` ADD COLUMN `enable`           INT(11)       NULL DEFAULT -1;
ALTER TABLE `hospital_department` ADD COLUMN `image_id`         BIGINT(64)    NULL DEFAULT -1;
ALTER TABLE `hospital_department` ADD COLUMN `disable_image_id` BIGINT(64)    NULL DEFAULT -1;
ALTER TABLE `hospital_department` ADD COLUMN `parent_id`        INT(11) NULL  NULL DEFAULT -1;