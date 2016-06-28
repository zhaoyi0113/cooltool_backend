DROP TABLE IF EXISTS `go2nurse_questionnaire`;
CREATE TABLE `go2nurse_questionnaire` (
  `id`           BIGINT(64)      NOT NULL AUTO_INCREMENT,
  `title`        VARCHAR(200)    NULL DEFAULT '',
  `description`  VARCHAR(500)    NULL DEFAULT '',
  `hospital_id`  INT(11)         NULL DEFAULT 0,
  `time_created` DATETIME    NULL DEFAULT now(),
  `status`       INT(11)    NULL DEFAULT 0,
  PRIMARY KEY (`id`)
)
  DEFAULT CHARACTER SET = utf8;

DROP TABLE IF EXISTS `go2nurse_question`;
CREATE TABLE `go2nurse_question` (
  `id`               BIGINT(64)    NOT NULL AUTO_INCREMENT,
  `questionnaire_id` BIGINT(64)    NULL DEFAULT 0,
  `content`          VARCHAR(500)  NULL DEFAULT '',
  `options`          VARCHAR(500)  NULL DEFAULT '',
  `type`             INT(11)       NULL DEFAULT 0,
  `time_created`     DATETIME      NULL DEFAULT now(),
  `status`           INT(11)       NULL DEFAULT 0,
  PRIMARY KEY (`id`)
)
  DEFAULT CHARACTER SET = utf8;

DROP TABLE IF EXISTS `go2nurse_user_questionnaire_answer`;
CREATE TABLE `go2nurse_user_questionnaire_answer` (
  `id`               BIGINT(64)    NOT NULL AUTO_INCREMENT,
  `user_id`          BIGINT(64)    NULL DEFAULT 0,
  `questionnaire_id` BIGINT(64)    NULL DEFAULT 0,
  `question_id`      BIGINT(64)    NULL DEFAULT 0,
  `answer`           VARCHAR(500)  NULL DEFAULT '',
  `time_created`     DATETIME      NULL DEFAULT now(),
  `status`           INT(11)       NULL DEFAULT 0,
  PRIMARY KEY (`id`)
)
  DEFAULT CHARACTER SET = utf8;
