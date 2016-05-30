ALTER TABLE `sensitive_words`
CHANGE COLUMN `status` `status` INT(11) NULL DEFAULT 0 ,
CHANGE COLUMN `type` `type` INT(11) NULL DEFAULT 0 ;
