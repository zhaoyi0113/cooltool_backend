ALTER TABLE `tags`          CHANGE COLUMN `image` `image_id` BIGINT(64) NULL DEFAULT 0;
ALTER TABLE `tags_category` CHANGE COLUMN `image` `image_id` BIGINT(64) NULL DEFAULT 0;