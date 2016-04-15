ALTER TABLE `badge` ADD COLUMN `ability_id`   INT(11) NULL DEFAULT 0;
ALTER TABLE `badge` ADD COLUMN `ability_type` INT(11) NULL DEFAULT 0;
ALTER TABLE `badge` DROP COLUMN `image_url`;