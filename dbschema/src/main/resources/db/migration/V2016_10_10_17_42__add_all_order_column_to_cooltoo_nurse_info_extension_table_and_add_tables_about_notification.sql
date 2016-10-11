ALTER TABLE `cooltoo_nurse_info_extension` ADD COLUMN `see_all_order` INT(11) NULL DEFAULT 0;

ALTER TABLE `nurse360_course` CHANGE COLUMN `id` `id` BIGINT(64) NOT NULL AUTO_INCREMENT ;


DROP TABLE IF EXISTS `nurse360_notification`;
CREATE TABLE IF NOT EXISTS `nurse360_notification` (
  `id`              BIGINT(11)   NOT NULL AUTO_INCREMENT,
  `time_created`    DATETIME     NULL DEFAULT now(),
  `status`          INT(11)      NULL DEFAULT 1,
  `title`           VARCHAR(200) NULL DEFAULT '',
  `introduction`    VARCHAR(500) NULL DEFAULT '',
  `content`         LONGTEXT,
  `significance`    INT(11)      NULL DEFAULT 0,
  `hospital_id`     INT(11)      NULL DEFAULT 0,
  `department_id`   INT(11)      NULL DEFAULT 0,
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;


DROP TABLE IF EXISTS `nurse360_nurse_notification_relation`;
CREATE TABLE IF NOT EXISTS `nurse360_nurse_notification_relation` (
  `id`              BIGINT(64)   NOT NULL AUTO_INCREMENT,
  `time_created`    DATETIME     NULL DEFAULT now(),
  `status`          INT(11)      NULL DEFAULT 0,
  `nurse_id`        BIGINT(64)   NULL DEFAULT 0,
  `notification_id` BIGINT(64)   NULL DEFAULT 0,
  `reading_status`  INT(64)      NULL DEFAULT 0,
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;