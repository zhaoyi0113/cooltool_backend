ALTER TABLE `go2nurse_service_order`
  CHANGE COLUMN `total_consumption_cent` `total_price_cent`    INT(11) NULL DEFAULT '0',
  CHANGE COLUMN `preferential_cent`      `total_discount_cent` INT(11) NULL DEFAULT '0';

ALTER TABLE `go2nurse_service_order`
  ADD COLUMN `total_income_cent`         INT(11) NULL DEFAULT 0 AFTER `total_discount_cent`,
  ADD COLUMN `service_item_count`        INT(11) NULL DEFAULT 0 AFTER `total_income_cent`,
  ADD COLUMN `need_visit_patient_record` INT(11) NULL DEFAULT 0;