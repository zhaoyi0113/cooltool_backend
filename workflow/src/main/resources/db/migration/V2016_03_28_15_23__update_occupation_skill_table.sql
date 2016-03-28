ALTER TABLE `occupation_skill` MODIFY COLUMN `image_id`         BIGINT(64) NULL DEFAULT -1;
ALTER TABLE `occupation_skill` ADD COLUMN    `disable_image_id` BIGINT(64) NULL DEFAULT -1 AFTER `image_id`;