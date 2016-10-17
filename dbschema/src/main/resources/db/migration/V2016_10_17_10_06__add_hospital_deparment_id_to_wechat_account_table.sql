ALTER TABLE `go2nurse_wechat_account`
ADD COLUMN `hospital_id`   INT(11) NULL DEFAULT 0,
ADD COLUMN `department_id` INT(11) NULL DEFAULT 0;