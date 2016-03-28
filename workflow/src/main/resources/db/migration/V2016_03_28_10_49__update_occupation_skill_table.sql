ALTER TABLE `occupation_skill` MODIFY COLUMN `image_id`         BIGINT(64) NULL DEFAULT -1;
ALTER TABLE `occupation_skill` ADD COLUMN    `disable_image_id` BIGINT(64) NULL DEFAULT -1 AFTER `image_id`;
ALTER TABLE `occupation_skill` ADD COLUMN    `parent_type`      INT(11)    NULL DEFAULT -1 AFTER `disable_image_id`;