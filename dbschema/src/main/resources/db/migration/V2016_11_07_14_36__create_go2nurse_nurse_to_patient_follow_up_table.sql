DROP TABLE IF EXISTS `go2nurse_nurse_patient_follow_up`;
CREATE TABLE `go2nurse_nurse_patient_follow_up` (
  `id`            BIGINT(64) NOT  NULL AUTO_INCREMENT,
  `time_created`  DATETIME   NULL DEFAULT now(),
  `status`           INT(11) NULL DEFAULT 0,
  `hospital_id`      INT(11) NULL DEFAULT 0,
  `department_id`    INT(11) NULL DEFAULT 0,
  `nurse_id`      BIGINT(64) NULL DEFAULT 0,
  `user_id`       BIGINT(64) NULL DEFAULT 0,
  `patient_id`    BIGINT(64) NULL DEFAULT 0,
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;



DROP TABLE IF EXISTS `go2nurse_nurse_patient_follow_up_record`;
CREATE TABLE `go2nurse_nurse_patient_follow_up_record` (
  `id`                                     BIGINT(64) NOT NULL AUTO_INCREMENT,
  `time_created`                           DATETIME   NULL DEFAULT now(),
  `status`                                    INT(11) NULL DEFAULT 0,
  `follow_up_id`                           BIGINT(64) NULL DEFAULT 0,
  `follow_up_type`                            INT(11) NULL DEFAULT 0, /* consultation or questionnaire */
  `relative_consultation_id`               BIGINT(64) NULL DEFAULT 0,
  `relative_questionnaire_id`              BIGINT(64) NULL DEFAULT 0,
  `relative_questionnaire_answer_group_id` BIGINT(64) NULL DEFAULT 0,
  `patient_replied`                           INT(11) NULL DEFAULT 0, /* YES, NO */
  `nurse_read`                                INT(11) NULL DEFAULT 0, /* YES, NO */
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;



ALTER TABLE `go2nurse_nurse_visit_patient`              CHANGE COLUMN `id` `id` BIGINT(64) NOT NULL AUTO_INCREMENT ;
ALTER TABLE `go2nurse_nurse_visit_patient_photo`        CHANGE COLUMN `id` `id` BIGINT(64) NOT NULL AUTO_INCREMENT ;
ALTER TABLE `go2nurse_nurse_visit_patient_service_item` CHANGE COLUMN `id` `id` BIGINT(64) NOT NULL AUTO_INCREMENT ;



ALTER TABLE `go2nurse_user_consultation` ADD COLUMN `creator` INT(11) NULL DEFAULT 0;
ALTER TABLE `go2nurse_user_consultation` ADD COLUMN `reason`  INT(11) NULL DEFAULT 0;
