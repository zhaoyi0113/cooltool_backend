ALTER TABLE `go2nurse_doctor` ADD COLUMN `mobile`   VARCHAR(45)  NULL DEFAULT NULL;
ALTER TABLE `go2nurse_doctor` ADD COLUMN `password` VARCHAR(128) NULL DEFAULT NULL;
ALTER TABLE `go2nurse_doctor` RENAME TO  `cooltoo_doctor`;
