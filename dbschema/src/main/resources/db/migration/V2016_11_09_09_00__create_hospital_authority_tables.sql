DROP TABLE IF EXISTS `nurse360_hospital_management_url`;
CREATE TABLE `nurse360_hospital_management_url` (
  `id`                 BIGINT(64)    NOT NULL AUTO_INCREMENT,
  `time_created`       DATETIME      NULL DEFAULT now(),
  `status`             INT(11)       NULL DEFAULT 0,
  `need_token`         INT(11)       NULL DEFAULT 0,
  `http_requests_type` INT(11)       NULL DEFAULT 0,
  `http_relative_url`  VARCHAR(1000) NULL DEFAULT '',
  `introduction`       TEXT          NULL,
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;



DROP TABLE IF EXISTS `nurse360_hospital_administrator`;
CREATE TABLE `nurse360_hospital_administrator` (
  `id`            BIGINT(64)   NOT NULL AUTO_INCREMENT,
  `time_created`  DATETIME     NULL DEFAULT now(),
  `status`        INT(11)      NULL DEFAULT 0,
  `admin_type`    INT(11)      NULL DEFAULT 0,
  `name`          VARCHAR(400) NULL DEFAULT '',
  `password`      VARCHAR(200) NULL DEFAULT '',
  `telephone`     VARCHAR(100) NULL DEFAULT '',
  `email`         VARCHAR(100) NULL DEFAULT '',
  `hospital_id`   INT(11)      NULL DEFAULT 0,
  `department_id` INT(11)      NULL DEFAULT 0,
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;

INSERT INTO `nurse360_hospital_administrator`(`id`, `status`, `name`, `password`)
VALUES (1, 1, 'admin', '7c4a8d09ca3762af61e59520943dc26494f8941b');



DROP TABLE IF EXISTS `nurse360_hospital_admin_access_token`;
CREATE TABLE `nurse360_hospital_admin_access_token` (
  `id`         BIGINT(64)  NOT NULL AUTO_INCREMENT,
  `time_created` DATETIME  NULL DEFAULT now(),
  `status`        INT(11)  NULL DEFAULT 0,
  `admin_id`   BIGINT(64)  NULL DEFAULT 0,
  `token`     VARCHAR(400) NULL DEFAULT '',
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;



DROP TABLE IF EXISTS `nurse360_hospital_admin_access_url`;
CREATE TABLE `nurse360_hospital_admin_access_url` (
  `id`         BIGINT(64)   NOT NULL AUTO_INCREMENT,
  `time_created` DATETIME   NULL DEFAULT now(),
  `status`        INT(11)   NULL DEFAULT 0,
  `admin_id`    BIGINT(64)  NULL DEFAULT 0,
  `url_id`     VARCHAR(400) NULL DEFAULT '',
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;