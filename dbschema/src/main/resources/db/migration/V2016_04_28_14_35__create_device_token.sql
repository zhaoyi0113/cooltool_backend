CREATE TABLE `nurse_device_tokens` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(11) NULL DEFAULT -1,
  `device_token` VARCHAR(256) NULL,
  `status` INT NULL DEFAULT 0,
  `time_created` DATETIME NULL,
  PRIMARY KEY (`id`));
