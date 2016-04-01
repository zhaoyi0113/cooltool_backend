ALTER TABLE `nurse_qualification` ADD COLUMN `status_description` VARCHAR(500) NULL DEFAULT '' AFTER `status`;
ALTER TABLE `nurse_qualification` ADD COLUMN `expiry_time`        DATETIME     NULL;