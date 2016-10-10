ALTER TABLE `go2nurse_doctor` ADD COLUMN `introduction` TEXT NULL;

ALTER TABLE `nurse360_course` CHANGE COLUMN `front_corver` `front_cover` BIGINT(64) NULL DEFAULT '0' ;