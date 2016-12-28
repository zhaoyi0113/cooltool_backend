ALTER TABLE `go2nurse_service_item`
  ADD COLUMN `managed_by` INT(11) NULL DEFAULT 1;

ALTER TABLE `go2nurse_service_order`
  ADD COLUMN `item_managed_by` INT(11) NULL DEFAULT 1 AFTER `service_item`;
