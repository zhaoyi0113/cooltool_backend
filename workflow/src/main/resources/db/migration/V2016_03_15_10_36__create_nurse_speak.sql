CREATE TABLE `nurse_speak` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(11) NULL DEFAULT 0,
  `content` VARCHAR(512) NULL,
  `time` DATETIME NULL,
  PRIMARY KEY (`id`));
