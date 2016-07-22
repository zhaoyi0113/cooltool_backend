ALTER TABLE `go2nurse_user_questionnaire_answer` ADD COLUMN `patient_name`    VARCHAR(45) NULL DEFAULT '' AFTER `patient_id`;
ALTER TABLE `go2nurse_user_questionnaire_answer` ADD COLUMN `patient_gender`  INT(11)      NULL DEFAULT 0  AFTER `patient_name`;
ALTER TABLE `go2nurse_user_questionnaire_answer` ADD COLUMN `patient_age`     INT(11)      NULL DEFAULT 0  AFTER `patient_gender`;
ALTER TABLE `go2nurse_user_questionnaire_answer` ADD COLUMN `patient_mobile`  VARCHAR(32) NULL DEFAULT '' AFTER `patient_age`;

ALTER TABLE `go2nurse_user_questionnaire_answer` ADD COLUMN `hospital_id`     INT(11)      NULL DEFAULT 0  AFTER `answer`;
ALTER TABLE `go2nurse_user_questionnaire_answer` ADD COLUMN `hospital_name`   VARCHAR(45) NULL DEFAULT '' AFTER `hospital_id`;
ALTER TABLE `go2nurse_user_questionnaire_answer` ADD COLUMN `department_id`   INT(11)      NULL DEFAULT 0  AFTER `hospital_name`;
ALTER TABLE `go2nurse_user_questionnaire_answer` ADD COLUMN `department_name` VARCHAR(45) NULL DEFAULT '' AFTER `department_id`;

ALTER TABLE `go2nurse_user_questionnaire_answer` ADD COLUMN `answer_completed` INT(11)    NULL DEFAULT 0  AFTER `answer`;

ALTER TABLE `go2nurse_user_questionnaire_answer` ADD COLUMN `questionnaire_name`        VARCHAR(200) NULL DEFAULT '' AFTER `questionnaire_id`;
ALTER TABLE `go2nurse_user_questionnaire_answer` ADD COLUMN `questionnaire_conclusion` TEXT          NULL             AFTER `questionnaire_name`;

ALTER TABLE `go2nurse_user_questionnaire_answer` ADD COLUMN `question_content`          VARCHAR(500) NULL DEFAULT '' AFTER `question_id`;


