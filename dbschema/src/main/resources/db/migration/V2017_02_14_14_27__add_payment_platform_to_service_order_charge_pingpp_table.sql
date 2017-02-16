ALTER TABLE `go2nurse_service_order_charge_pingpp`
  ADD COLUMN `payment_platform` INT(11) NULL DEFAULT 0 AFTER `order_no`;