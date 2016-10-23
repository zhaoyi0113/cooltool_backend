DROP TABLE IF EXISTS `go2nurse_casebook`;
CREATE TABLE `go2nurse_casebook` (
  `id` BIGINT(64) NOT NULL AUTO_INCREMENT,
  `time_created` DATETIME NULL DEFAULT now(),
  `status` INT(11) NULL DEFAULT 0,
  `user_id` BIGINT(64) NULL DEFAULT 0,
  `patient_id` BIGINT(64) NULL DEFAULT 0,
  `nurse_id` BIGINT(64) NULL DEFAULT 0,
  `case_name` VARCHAR(200) NULL DEFAULT '',
  `case_description` TEXT NULL,
PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;

DROP TABLE IF EXISTS `go2nurse_case`;
CREATE TABLE `go2nurse_case` (
  `id`           BIGINT(64) NOT NULL AUTO_INCREMENT,
  `time_created` DATETIME NULL DEFAULT now(),
  `status`       INT(11) NULL DEFAULT 0,
  `nurse_id`     BIGINT(64) NULL DEFAULT 0,
  `casebook_id`  BIGINT(64) NULL DEFAULT 0,
  `case_record`  TEXT NULL,
PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;

DROP TABLE IF EXISTS `go2nurse_image_in_case`;
CREATE TABLE `go2nurse_image_in_case` (
  `id`           BIGINT(64) NOT NULL AUTO_INCREMENT,
  `time_created` DATETIME NULL DEFAULT now(),
  `status`       INT(11) NULL DEFAULT 0,
  `casebook_id`  BIGINT(64) NULL DEFAULT 0,
  `case_id`      BIGINT(64) NULL DEFAULT 0,
  `image_id`     BIGINT(64) NULL DEFAULT 0,
PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;