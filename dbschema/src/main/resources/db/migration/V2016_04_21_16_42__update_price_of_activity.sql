ALTER TABLE `platform_activities` CHANGE COLUMN `price`  `price`  DECIMAL(10,2) NULL DEFAULT '2' ;
ALTER TABLE `platform_activities` CHANGE COLUMN `status` `status` INT(11)       NULL DEFAULT 0 ;