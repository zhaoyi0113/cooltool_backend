ALTER TABLE `go2nurse_advertisement` ADD COLUMN `advertisement_type` INT(11) NULL DEFAULT 0;
ALTER TABLE `go2nurse_advertisement` CHANGE COLUMN `order_index` `order_index` BIGINT(64) DEFAULT '0';