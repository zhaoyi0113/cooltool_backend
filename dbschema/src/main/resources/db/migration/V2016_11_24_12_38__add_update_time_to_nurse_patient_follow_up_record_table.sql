ALTER TABLE `go2nurse_nurse_patient_follow_up_record`
ADD COLUMN `time_updated` DATETIME NULL DEFAULT now() AFTER `nurse_read`;

