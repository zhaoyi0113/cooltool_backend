DROP TABLE IF EXISTS `go2nurse_patient_symptoms`;
CREATE TABLE `go2nurse_patient_symptoms` (
  `id`            BIGINT(64) NOT NULL AUTO_INCREMENT,
  `time_created`  DATETIME NULL DEFAULT now(),
  `status`        INT(11) NULL DEFAULT 0,
  `order_id`      BIGINT(64) NULL DEFAULT 0,
  `user_id`       BIGINT(64) NULL DEFAULT 0,
  `patient_id`    BIGINT(64) NULL DEFAULT 0,
  `symptoms`             TEXT NULL,
  `symptoms_description` TEXT NULL,
  `symptoms_images`      TEXT NULL,
  `questionnaire`        TEXT NULL,
PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;


ALTER TABLE `go2nurse_service_item` ADD COLUMN `need_symptoms`    INT(11) NULL DEFAULT 0;
ALTER TABLE `go2nurse_service_item` ADD COLUMN `symptoms_items`   TEXT NULL;
ALTER TABLE `go2nurse_service_item` ADD COLUMN `questionnaire_id` BIGINT(64) NULL DEFAULT 0;


ALTER TABLE `go2nurse_questionnaire`ADD COLUMN `evaluate_before_order` INT(11) NULL DEFAULT 0;
