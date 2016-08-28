CREATE TABLE `go2nurse_user_consultation` (
  `id` BIGINT(64) NOT NULL AUTO_INCREMENT,
  `time_created` DATETIME NULL DEFAULT now(),
  `status` INT(11) NULL DEFAULT 0,
  `category_id` BIGINT(64) NULL DEFAULT 0,
  `disease_description` MEDIUMTEXT NULL,
  `clinical_history` VARCHAR(500) DEFAULT '',
  `user_id` BIGINT(64) NULL DEFAULT 0,
  `patient_id` BIGINT(64) NULL DEFAULT 0,
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;

CREATE TABLE `go2nurse_user_consultation_talk` (
  `id` BIGINT(64) NOT NULL AUTO_INCREMENT,
  `time_created` DATETIME NULL DEFAULT now(),
  `status` INT(11) NULL DEFAULT 0,
  `user_consultation_id` BIGINT(64) NULL DEFAULT 0,
  `nurse_id` BIGINT(64) NULL DEFAULT 0,
  `talk_status` INT(11) NULL DEFAULT 0,
  `talk_content` VARCHAR(800) DEFAULT '',
  PRIMARY KEY (`id`))
  DEFAULT CHARACTER SET = utf8;

CREATE TABLE `go2nurse_image_in_user_consultation` (
  `id` BIGINT(64) NOT NULL AUTO_INCREMENT,
  `time_created` DATETIME NULL DEFAULT now(),
  `status` INT(11) NULL DEFAULT 0,
  `consultation_id` BIGINT(64) NULL DEFAULT 0,
  `consultation_talk_id` BIGINT(64) NULL DEFAULT 0,
  `image_id` BIGINT(64) NULL DEFAULT 0,
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;
