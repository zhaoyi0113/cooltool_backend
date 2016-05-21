ALTER TABLE `nurse_integration`
CHANGE COLUMN `user_id` `user_id` BIGINT(11) NULL DEFAULT 0 ,
CHANGE COLUMN `reason_id` `reason_id` BIGINT(11) NULL DEFAULT 0 ,
CHANGE COLUMN `ability_id` `ability_id` BINARY(11) NULL DEFAULT 0 ,
CHANGE COLUMN `point` `point` INT(11) NULL DEFAULT 0 ;
