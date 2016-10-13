ALTER TABLE `nurse360_notification` DROP COLUMN `department_id`;
ALTER TABLE `nurse360_notification` DROP COLUMN `hospital_id`;

DROP TABLE IF EXISTS `nurse360_notification_hospital_department_relation`;
CREATE TABLE IF NOT EXISTS `nurse360_notification_hospital_department_relation` (
  `id`               BIGINT(64)   NOT NULL AUTO_INCREMENT,
  `time_created`     DATETIME     NULL DEFAULT now(),
  `status`           INT(11)      NULL DEFAULT 0,
  `hospital_id`      INT(11)      NULL DEFAULT 0,
  `department_id`    INT(11)      NULL DEFAULT 0,
  `notification_id`  BIGINT(64)   NULL DEFAULT 0,
PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;



ALTER TABLE `go2nurse_service_order` ADD COLUMN `completed_time` DATETIME NULL;



DROP TABLE IF EXISTS `go2nurse_doctor_order`;
CREATE TABLE IF NOT EXISTS `go2nurse_doctor_order` (
  `id`               BIGINT(64)   NOT NULL AUTO_INCREMENT,
  `time_created`     DATETIME     NULL DEFAULT now(),
  `status`           INT(11)      NULL DEFAULT 0,
  `hospital_id`      INT(11)      NULL DEFAULT 0,
  `hospital_order`   INT(11)      NULL DEFAULT 0,
  `department_id`    INT(11)      NULL DEFAULT 0,
  `department_order` INT(11)      NULL DEFAULT 0,
  `doctor_id`        BIGINT(64)   NULL DEFAULT 0,
PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;

