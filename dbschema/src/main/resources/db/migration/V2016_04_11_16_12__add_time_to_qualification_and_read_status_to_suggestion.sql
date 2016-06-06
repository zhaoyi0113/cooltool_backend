ALTER TABLE `nurse_qualification` ADD COLUMN `create_time`  DATETIME NULL DEFAULT NULL AFTER `status_description`;
ALTER TABLE `nurse_qualification` ADD COLUMN `process_time` DATETIME NULL DEFAULT NULL AFTER `create_time`;

ALTER TABLE `user_suggestion`     ADD COLUMN `status`       INT NULL DEFAULT 0 AFTER `create_time`;