DROP TABLE IF EXISTS `admin_user`;
CREATE TABLE `admin_user` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `user_type` VARCHAR(20) NOT NULL,
  `user_name` VARCHAR(45) NOT NULL,
  `password` VARCHAR(128) NOT NULL,
  `telephone_number` VARCHAR(20) NOT NULL DEFAULT '',
  `email` VARCHAR(64) NOT NULL DEFAULT '',
  `time_created` DATETIME NOT NULL DEFAULT now(),
  PRIMARY KEY (`id`)
)
DEFAULT CHARACTER SET = utf8;

INSERT INTO `admin_user` (`id`, `user_type`, `user_name`, `password`) VALUES (0, '0', 'admin', '');


DROP TABLE IF EXISTS `admin_user_token_access`;
CREATE TABLE `admin_user_token_access` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(11) NOT NULL,
  `user_type` VARCHAR(11) NOT NULL,
  `time_created` DATETIME NOT NULL,
  `token` VARCHAR(256) NOT NULL,
  `status` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`)
)
DEFAULT CHARACTER SET = utf8;