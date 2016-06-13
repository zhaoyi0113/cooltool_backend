CREATE TABLE IF NOT EXISTS `go2nurse_user_token_access` (
   `id`           BIGINT(64)   NOT NULL AUTO_INCREMENT,
   `time_created` DATETIME     DEFAULT now(),
   `status`       VARCHAR(45)  DEFAULT 1,
   `user_id`      BIGINT(64)   DEFAULT 0,
   `user_type`    INT(11)      DEFAULT 0,
   `token`        VARCHAR(256) DEFAULT '',
   PRIMARY KEY (`id`)
 )
DEFAULT CHARACTER SET = utf8;

ALTER TABLE `go2nurse_user`
   CHANGE COLUMN `time_created` `time_created` DATETIME NULL DEFAULT now() AFTER `id`,
   CHANGE COLUMN `status` `status` INT(11) NULL DEFAULT 0 AFTER `time_created`,
   CHANGE COLUMN `gender` `gender` INT(11) NULL DEFAULT '0' AFTER `name`,
   CHANGE COLUMN `age` `birthday` DATETIME NULL DEFAULT now() AFTER `gender`,
   ADD COLUMN `password` VARCHAR(128) NULL DEFAULT '' AFTER `mobile`,
   ADD COLUMN `profile_photo_id` BIGINT(64) NULL DEFAULT 0 AFTER `password`,
   ADD COLUMN `authority` INT(11) NULL DEFAULT 1 AFTER `profile_photo_id`,
   ADD COLUMN `type` INT(11) NULL DEFAULT 1 AFTER `authority`;

