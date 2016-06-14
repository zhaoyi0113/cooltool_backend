CREATE TABLE IF NOT EXISTS `go2nurse_user_course_relation` (
  `id`           BIGINT(64) NOT NULL AUTO_INCREMENT,
  `time_created` DATETIME   NULL DEFAULT now(),
  `status`       INT(11)    NULL DEFAULT 0,
  `user_id`      BIGINT(64) NULL DEFAULT 0,
  `course_id`    BIGINT(64) NULL DEFAULT 0,
  PRIMARY KEY (`id`))
  DEFAULT CHARACTER SET = utf8;


CREATE TABLE IF NOT EXISTS `go2nurse_user_diagnostic_point_relation` (
  `id`           BIGINT(64) NOT NULL AUTO_INCREMENT,
  `time_created` DATETIME   NULL DEFAULT now(),
  `status`       INT(11)    NULL DEFAULT 0,
  `user_id`      BIGINT(64) NULL DEFAULT 0,
  `diagnostic_point_id`   BIGINT(64) NULL DEFAULT 0,
  `diagnostic_point_time` DATETIME   NULL DEFAULT now(),
  PRIMARY KEY (`id`))
  DEFAULT CHARACTER SET = utf8;


CREATE TABLE IF NOT EXISTS `go2nurse_user_hospitalized_relation` (
  `id`            BIGINT(64) NOT NULL AUTO_INCREMENT,
  `time_created`  DATETIME   NULL DEFAULT now(),
  `status`        INT(11)    NULL DEFAULT 0,
  `user_id`       BIGINT(64) NULL DEFAULT 0,
  `hospital_id`   INT(11)    NULL DEFAULT 0,
  `department_id` INT(11)    NULL DEFAULT 0,
  PRIMARY KEY (`id`))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8;