CREATE TABLE IF NOT EXISTS `go2nurse_course` (
  `id`            INT(11) NOT NULL AUTO_INCREMENT,
  `time_created`  DATETIME DEFAULT CURRENT_TIMESTAMP,
  `status`        INT(11)  DEFAULT 1,
  `name`          VARCHAR(200) DEFAULT '',
  `introduction`  VARCHAR(500) DEFAULT '',
  `content`       LONGTEXT,
  `front_corver`  BIGINT(64)   DEFAULT 0,
  `link`          TEXT,
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;


CREATE TABLE IF NOT EXISTS `go2nurse_course_category` (
  `id`           INT(11) NOT NULL AUTO_INCREMENT,
  `time_created` DATETIME     NULL DEFAULT now(),
  `status`       INT(11)      NULL DEFAULT 1,
  `name`         VARCHAR(200) NULL DEFAULT '',
  `introduction` VARCHAR(500) NULL DEFAULT '',
  `image_id`     BIGINT(64)   NULL DEFAULT 0,
  PRIMARY KEY (`id`))
  DEFAULT CHARACTER SET = utf8;

CREATE TABLE IF NOT EXISTS `go2nurse_course_category_relation` (
  `id`                 INT(11) NOT NULL AUTO_INCREMENT,
  `time_created`       DATETIME NULL DEFAULT now(),
  `status`             INT(11)  NULL DEFAULT 1,
  `course_id`          INT(11)  NULL DEFAULT 0,
  `course_category_id` INT(11)  NULL DEFAULT 0,
  PRIMARY KEY (`id`))
  DEFAULT CHARACTER SET = utf8;


CREATE TABLE IF NOT EXISTS `go2nurse_file_storage` (
  `id`             BIGINT(64) NOT NULL AUTO_INCREMENT,
  `time_created`   DATETIME     DEFAULT now(),
  `status`         INT(11)      DEFAULT 1,
  `file_real_name` VARCHAR(200) DEFAULT NULL,
  `file_path`      VARCHAR(200) DEFAULT NULL,
  PRIMARY KEY (`id`))
  DEFAULT CHARACTER SET = utf8;
