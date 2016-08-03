DROP TABLE IF EXISTS `go2nurse_doctor_clinic_date`;
CREATE TABLE `go2nurse_doctor_clinic_date` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `time_created` DATETIME NULL DEFAULT now(),
  `status` INT(11) NULL DEFAULT 0,
  `doctor_id` BIGINT(64) NULL DEFAULT 0,
  `clinic_date` DATE NULL DEFAULT '1970-01-01',
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;

DROP TABLE IF EXISTS `go2nurse_doctor_clinic_hours`;
CREATE TABLE `go2nurse_doctor_clinic_hours` (
  `id` BIGINT(64) NOT NULL AUTO_INCREMENT,
  `time_created` DATETIME NULL DEFAULT now(),
  `status` INT(11) NULL DEFAULT 0,
  `doctor_id` BIGINT(64) NULL DEFAULT 0,
  `doctor_clinic_date_id` BIGINT(64) NULL DEFAULT 0,
  `clinic_hour_start` TIME NULL DEFAULT '00:00:00',
  `clinic_hour_end` TIME NULL DEFAULT '00:00:00',
  `number_count` INT(11) NULL DEFAULT 0,
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;

DROP TABLE IF EXISTS `go2nurse_doctor_appointment`;
CREATE TABLE `go2nurse_doctor_appointment` (
  `id` BIGINT(64) NOT NULL AUTO_INCREMENT,
  `time_created` DATETIME NULL DEFAULT now(),
  `status` INT(11) NULL DEFAULT 0,
  `order_no` VARCHAR(45) NULL DEFAULT '',
  `hospital_id` INT(11) NULL DEFAULT 0,
  `hospital` TEXT NULL,
  `department_id` INT(11) NULL DEFAULT 0,
  `department` TEXT NULL,
  `doctor_id` BIGINT(64) NULL DEFAULT 0,
  `doctor` MEDIUMTEXT NULL,
  `clinic_date_id` BIGINT(64) NULL DEFAULT 0,
  `clinic_date` DATE NULL DEFAULT '1970:01:01',
  `clinic_hours_id` BIGINT(64) NULL DEFAULT 0,
  `clinic_hours_start` TIME NULL DEFAULT '00:00:00',
  `clinic_hours_end` TIME NULL DEFAULT '00:00:00',
  `user_id` BIGINT(64) NULL DEFAULT 0,
  `patient_id` BIGINT(64) NULL DEFAULT 0,
  `patient` MEDIUMTEXT NULL,
  `order_status` INT(11) NULL DEFAULT 0,
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;