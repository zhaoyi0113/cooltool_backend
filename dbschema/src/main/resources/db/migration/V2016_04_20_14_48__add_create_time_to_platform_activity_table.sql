ALTER TABLE `platform_activities` ADD    COLUMN `create_time` DATETIME   NULL DEFAULT now();
ALTER TABLE `platform_activities` ADD    COLUMN `status`      INT        NULL DEFAULT 1;
ALTER TABLE `platform_activities` ADD    COLUMN `front_cover` BIGINT(64) NULL DEFAULT 0;
ALTER TABLE `platform_activities` CHANGE COLUMN `description` `description` VARCHAR(400) NULL DEFAULT '' ;
