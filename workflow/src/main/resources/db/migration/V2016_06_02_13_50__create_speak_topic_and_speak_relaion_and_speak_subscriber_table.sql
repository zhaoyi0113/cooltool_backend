CREATE TABLE `nurse_speak_topic` (
  `id` BIGINT(64) NOT NULL AUTO_INCREMENT,
  `time_created` DATETIME NULL DEFAULT now(),
  `status` INT(11) NULL DEFAULT 1,
  `creator` BIGINT(64) NULL DEFAULT 0,
  `title` VARCHAR(200) NULL DEFAULT '',
  `profile_photo` BIGINT(64) NULL DEFAULT 0,
  `label` VARCHAR(200) NULL DEFAULT '',
  `taxonomy` VARCHAR(200) NULL DEFAULT '',
  `description` VARCHAR(1000) NULL DEFAULT '',
  `province` INT(11) NULL DEFAULT 0,
  `click_number` BIGINT(64) NULL DEFAULT 0,
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;

CREATE TABLE `nurse_speak_topic_relation` (
  `id` BIGINT(64) NOT NULL AUTO_INCREMENT,
  `time_created` DATETIME NULL DEFAULT now(),
  `status` INT(11) NULL DEFAULT 1,
  `topic_id` BIGINT(64) NULL DEFAULT 0,
  `speak_id` BIGINT(64) NULL DEFAULT 0,
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;

CREATE TABLE `nurse_speak_topic_subscriber` (
  `id` BIGINT(64) NOT NULL,
  `time_created` DATETIME NULL DEFAULT now(),
  `status` INT(11) NULL DEFAULT 1,
  `topic_id` BIGINT(64) NULL DEFAULT 0,
  `user_id` BIGINT(64) NULL DEFAULT 0,
  `user_type` INT(11) NULL DEFAULT 0,
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;


