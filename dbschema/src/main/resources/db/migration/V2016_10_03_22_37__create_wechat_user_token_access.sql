CREATE TABLE `go2nurse_user_wechat_token_access` (
  `id` BIGINT(64) NOT NULL AUTO_INCREMENT,
  `token` VARCHAR(256) NULL,
  `wechat_account_id` INT NULL,
  `time_created` DATETIME NULL,
  `status` INT NULL,
  PRIMARY KEY (`id`));
