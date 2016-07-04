ALTER TABLE `go2nurse_user_diagnostic_point_relation` ADD COLUMN `cancelled` INT(11) NULL DEFAULT 0;
ALTER TABLE `go2nurse_user_re_examination` ADD COLUMN `group_id` BIGINT(11) NULL DEFAULT 0 AFTER `user_id`;
ALTER TABLE `go2nurse_user_re_examination` ADD COLUMN `is_start` INT(11) NULL DEFAULT 0 AFTER `user_id`;
ALTER TABLE `go2nurse_user_re_examination` CHANGE COLUMN `ignore` `is_ignore` INT(11) NULL DEFAULT 0;