DROP TABLE IF EXISTS `go2nurse_user_questionnaire_answer`;
CREATE TABLE `go2nurse_user_questionnaire_answer` (
  `id` BIGINT(64) NOT NULL AUTO_INCREMENT,
  `time_created` DATETIME NULL DEFAULT now(),
  `status` INT(11) NULL DEFAULT '0',
  `group_id` BIGINT(64) NULL DEFAULT '0',
  `patient_id` BIGINT(64) NULL DEFAULT '0',
  `patient_name` VARCHAR(45) NULL DEFAULT '',
  `patient_gender` INT(11) NULL DEFAULT 0,
  `patient_age` INT(11) NULL DEFAULT 0,
  `patient_mobile` VARCHAR(45) NULL DEFAULT '',
  `questionnaire_id` BIGINT(64) NULL DEFAULT '0',
  `questionnaire_name` VARCHAR(200) NULL DEFAULT '',
  `question_id` BIGINT(64) NULL DEFAULT '0',
  `question_content` VARCHAR(500) NULL DEFAULT '',
  `patient_answer` VARCHAR(500) NULL DEFAULT '',
  `hospital_id` INT(11) NULL DEFAULT 0,
  `hospital_name` VARCHAR(45) NULL DEFAULT '',
  `department_id` INT(11) NULL DEFAULT 0,
  `departname_name` VARCHAR(45) NULL DEFAULT '',
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;
