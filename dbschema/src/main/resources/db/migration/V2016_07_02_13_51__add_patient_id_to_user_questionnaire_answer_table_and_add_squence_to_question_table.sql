ALTER TABLE `go2nurse_user_questionnaire_answer` ADD COLUMN `patient_id` BIGINT(64) NULL DEFAULT 0 AFTER `user_id`;
ALTER TABLE `go2nurse_question` ADD COLUMN `grade` INT(11) NULL DEFAULT 0 AFTER `type`;