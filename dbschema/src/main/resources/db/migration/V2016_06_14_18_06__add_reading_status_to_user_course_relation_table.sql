ALTER TABLE `go2nurse_user_course_relation` 
ADD COLUMN `reading_status` INT(11) NULL DEFAULT 0 AFTER `course_id`;
