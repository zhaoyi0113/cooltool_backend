ALTER TABLE `nurse`
CHANGE COLUMN `gender` `gender` INT(11) NULL DEFAULT 0 ,
CHANGE COLUMN `age` `age` INT(11) NULL DEFAULT 0 ,
CHANGE COLUMN `profile_photo_id` `profile_photo_id` BIGINT(64) NULL DEFAULT 0 ,
CHANGE COLUMN `background_image_id` `background_image_id` BIGINT(64) NULL DEFAULT 0 ;
