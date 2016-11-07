DROP TABLE IF EXISTS `go2nurse_nurse_visit_patient_service_item`;
CREATE TABLE `go2nurse_nurse_visit_patient_service_item` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `time_created` DATETIME NULL DEFAULT now(),
  `status` INT(11) NULL DEFAULT 0,
  `hospital_id` INT(11) NULL DEFAULT 0,
  `department_id` INT(11) NULL DEFAULT 0,
  `item_name` VARCHAR(300) NULL DEFAULT '',
  `item_description` TEXT NULL,
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;


DROP TABLE IF EXISTS `go2nurse_nurse_visit_patient`;
CREATE TABLE `go2nurse_nurse_visit_patient` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `time_created` DATETIME NULL DEFAULT now(),
  `status` INT(11) NULL DEFAULT 0,
  `order_id` BIGINT(64) NULL DEFAULT 0,
  `nurse_id` BIGINT(64) NULL DEFAULT 0,
  `user_id` BIGINT(64) NULL DEFAULT 0,
  `patient_id` BIGINT(64) NULL DEFAULT 0,
  `service_itme` TEXT null,
  `visit_record` TEXT NULL,
  `patient_sign` BIGINT(64) NULL DEFAULT 0,
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;


DROP TABLE IF EXISTS `go2nurse_nurse_visit_patient_photo`;
CREATE TABLE `go2nurse_nurse_visit_patient_photo` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `time_created` DATETIME NULL DEFAULT now(),
  `status` INT(11) NULL DEFAULT 0,
  `nurse_visit_patient_id` BIGINT(64) NULL DEFAULT 0,
  `image_id` BIGINT(64) NULL DEFAULT 0,
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;
