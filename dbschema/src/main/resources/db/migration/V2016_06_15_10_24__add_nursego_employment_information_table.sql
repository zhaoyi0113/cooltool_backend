
CREATE TABLE `nursego_employment_information` (
  `id`           BIGINT(64)   NOT NULL AUTO_INCREMENT,
  `time_created` DATETIME     NULL DEFAULT now(),
  `status`       INT(11)      NULL DEFAULT 1,
  `title`        VARCHAR(200) NULL DEFAULT '',
  `front_cover`  BIGINT(64)   NULL DEFAULT 0,
  `url`          VARCHAR(500) NULL DEFAULT '',
  `grade`        INT(11)      NULL DEFAULT 0,
  PRIMARY KEY (`id`))
  DEFAULT CHARACTER SET = utf8;