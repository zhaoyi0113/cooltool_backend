ALTER TABLE `go2nurse_user_consultation` ADD COLUMN `score` FLOAT NULL DEFAULT 0;
ALTER TABLE `go2nurse_doctor_appointment` ADD COLUMN `score` FLOAT NULL DEFAULT 0;
ALTER TABLE `go2nurse_service_order` ADD COLUMN `score` FLOAT NULL DEFAULT 0;

ALTER TABLE `cooltoo_nurse` ADD COLUMN `register_from` INT(11) NULL DEFAULT 0;