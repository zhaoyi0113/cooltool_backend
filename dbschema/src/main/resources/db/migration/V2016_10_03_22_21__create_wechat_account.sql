CREATE TABLE `go2nurse_wechat_account` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `appid` VARCHAR(64) NULL,
  `appsecret` VARCHAR(128) NULL,
  `time_created` DATETIME NULL,
  `status` INT NULL,
  PRIMARY KEY (`id`));
