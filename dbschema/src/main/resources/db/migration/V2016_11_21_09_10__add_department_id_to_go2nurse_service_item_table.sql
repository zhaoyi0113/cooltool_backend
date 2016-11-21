ALTER TABLE `go2nurse_service_item` ADD COLUMN `vendor_department_id` BIGINT(64) NULL DEFAULT 0 AFTER `vendor_id`;

ALTER TABLE `go2nurse_service_order`
  ADD COLUMN `item_vendor_department_id` BIGINT(64) NULL DEFAULT 0 AFTER `item_vendor`,
  ADD COLUMN `item_vendor_department`    MEDIUMTEXT NULL           AFTER `item_vendor_department_id`;