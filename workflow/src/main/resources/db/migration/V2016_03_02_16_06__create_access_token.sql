CREATE TABLE `token_access` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(11) NULL,
  `type` INT NULL,
  `time_created` DATETIME NULL,
  PRIMARY KEY (`id`));