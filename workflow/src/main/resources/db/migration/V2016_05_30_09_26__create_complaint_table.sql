CREATE TABLE `nurse_speak_complaint` (
  `id` BIGINT(64) NOT NULL AUTO_INCREMENT,
  `informant_id` BIGINT(64) NULL DEFAULT 0,
  `speak_id` BIGINT(64) NULL DEFAULT 0,
  `reason` VARCHAR(200) NULL DEFAULT '',
  `time_created` DATETIME NULL DEFAULT now(),
  `status` INT(11) NULL DEFAULT 0,
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;