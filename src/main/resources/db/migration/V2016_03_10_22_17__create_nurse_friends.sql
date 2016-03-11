CREATE TABLE `nurse_friends` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(11) NULL,
  `friend_id` BIGINT(11) NULL,
  PRIMARY KEY (`id`));
