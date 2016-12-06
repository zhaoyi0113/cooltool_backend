ALTER TABLE `go2nurse_service_item`
  ADD COLUMN `service_discount_cent`     INT(11) NULL DEFAULT '0',
  ADD COLUMN `server_income_cent`        INT(11) NULL DEFAULT '0',
  ADD COLUMN `need_visit_patient_record` INT(11) NULL DEFAULT '0',
  ADD COLUMN `manager_approved`          INT(11) NULL DEFAULT '0';