ALTER TABLE `go2nurse_advertisement` ADD COLUMN `description` VARCHAR(200) NULL DEFAULT '';
ALTER TABLE `go2nurse_advertisement` CHANGE COLUMN `order` `order_index` INT(11) DEFAULT '0';