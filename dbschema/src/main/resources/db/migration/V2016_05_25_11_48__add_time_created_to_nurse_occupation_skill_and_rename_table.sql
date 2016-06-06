ALTER TABLE `nurse_occupation_skill`
CHANGE COLUMN `point` `point` INT(11) NULL DEFAULT 0 ,
ADD COLUMN `time_created` DATETIME NULL DEFAULT now(),
ADD COLUMN `status` INT(11) NULL DEFAULT 1;

ALTER TABLE `nurse_occupation_skill` RENAME TO  `nurse_skill` ;
