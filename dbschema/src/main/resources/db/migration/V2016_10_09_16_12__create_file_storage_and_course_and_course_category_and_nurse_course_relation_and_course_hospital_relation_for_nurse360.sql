DROP TABLE IF EXISTS `nurse360_course`;
CREATE TABLE IF NOT EXISTS `nurse360_course` (
  `id`             INT(11)      NOT NULL AUTO_INCREMENT,
  `time_created`   DATETIME     NULL DEFAULT now(),
  `status`         INT(11)      NULL DEFAULT 1,
  `name`           VARCHAR(200) NULL DEFAULT '',
  `introduction`   VARCHAR(500) NULL DEFAULT '',
  `front_corver`   BIGINT(64)   NULL DEFAULT 0,
  `category_id`    INT(11)      NULL DEFAULT 0,
  `keyword`        VARCHAR(10)  NULL DEFAULT '',
  `unique_id`      VARCHAR(100) NULL DEFAULT '',
  `content`        LONGTEXT,
  `link`           TEXT,
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;


DROP TABLE IF EXISTS `nurse360_course_category`;
CREATE TABLE IF NOT EXISTS `nurse360_course_category` (
  `id`             INT(11)      NOT NULL AUTO_INCREMENT,
  `time_created`   DATETIME     NULL DEFAULT now(),
  `status`         INT(11)      NULL DEFAULT 1,
  `name`           VARCHAR(200) NULL DEFAULT '',
  `introduction`   VARCHAR(500) NULL DEFAULT '',
  `image_id`       BIGINT(64)   NULL DEFAULT 0,
  PRIMARY KEY (`id`))
  DEFAULT CHARACTER SET = utf8;


DROP TABLE IF EXISTS `nurse360_file_storage`;
CREATE TABLE IF NOT EXISTS `nurse360_file_storage` (
  `id`             BIGINT(64)   NOT NULL AUTO_INCREMENT,
  `time_created`   DATETIME     NULL DEFAULT now(),
  `status`         INT(11)      NULL DEFAULT 1,
  `file_real_name` VARCHAR(200) NULL DEFAULT NULL,
  `file_path`      VARCHAR(200) NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
  DEFAULT CHARACTER SET = utf8;


DROP TABLE IF EXISTS `nurse360_nurse_course_relation`;
CREATE TABLE IF NOT EXISTS `nurse360_nurse_course_relation` (
  `id`             BIGINT(64)   NOT NULL AUTO_INCREMENT,
  `time_created`   DATETIME     NULL DEFAULT now(),
  `status`         INT(11)      NULL DEFAULT 0,
  `nurse_id`       BIGINT(64)   NULL DEFAULT 0,
  `course_id`      BIGINT(64)   NULL DEFAULT 0,
  `reading_status` INT(64)      NULL DEFAULT 0,
  PRIMARY KEY (`id`))
  DEFAULT CHARACTER SET = utf8;


DROP TABLE IF EXISTS `nurse360_course_hospital_department_relation`;
CREATE TABLE IF NOT EXISTS `nurse360_course_hospital_department_relation` (
  `id`             BIGINT(64)   NOT NULL AUTO_INCREMENT,
  `time_created`   DATETIME     NULL DEFAULT now(),
  `status`         INT(11)      NULL DEFAULT 0,
  `hospital_id`    INT(11)      NULL DEFAULT 0,
  `department_id`  INT(11)      NULL DEFAULT 0,
  `course_id`      BIGINT(64)   NULL DEFAULT 0,
  PRIMARY KEY (`id`))
  DEFAULT CHARACTER SET = utf8;