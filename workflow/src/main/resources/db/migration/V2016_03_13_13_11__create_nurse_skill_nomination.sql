CREATE TABLE `nurse_skill_nomination` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(11) NOT NULL DEFAULT 0,
  `skill_id` INT NOT NULL,
  `nomiated_user_id` BIGINT(11) NOT NULL,
  `date_time` DATETIME NOT NULL,
  PRIMARY KEY (`id`));
