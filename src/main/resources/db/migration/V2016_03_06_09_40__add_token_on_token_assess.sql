

ALTER TABLE `token_access`
ADD COLUMN `token` VARCHAR(256) NULL AFTER `time_created`;
