ALTER TABLE `cooltoo_hospital`
  ADD COLUMN `phone_number` VARCHAR(100) NULL DEFAULT '',
  ADD COLUMN `zip_code`     VARCHAR(100) NULL DEFAULT '';


ALTER TABLE `cooltoo_hospital_department`
  ADD COLUMN `phone_number` VARCHAR(100) NULL DEFAULT '',
  ADD COLUMN `longitude`    DOUBLE NULL DEFAULT 0.0,
  ADD COLUMN `latitude`     DOUBLE NULL DEFAULT 0.0;