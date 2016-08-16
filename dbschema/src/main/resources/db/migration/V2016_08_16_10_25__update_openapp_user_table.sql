ALTER TABLE `go2nurse_user_openapp_desc`
ADD COLUMN `created_at` BIGINT(11) NULL AFTER `user_id`,
ADD COLUMN `status` INT NULL DEFAULT 0 AFTER `created_at`;
