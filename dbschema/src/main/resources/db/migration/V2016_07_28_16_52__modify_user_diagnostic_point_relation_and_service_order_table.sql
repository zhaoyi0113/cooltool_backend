ALTER TABLE `go2nurse_user_diagnostic_point_relation`
  ADD COLUMN `has_operation` INT(11) NULL DEFAULT 0;


ALTER TABLE `go2nurse_service_order`
  CHANGE COLUMN `total_consumption` `total_consumption_cent` INT(11) NULL DEFAULT 0 ,
  CHANGE COLUMN `payment_amount` `payment_amount_cent` INT(11) NULL DEFAULT 0 ,
  ADD COLUMN `service_item_id` BIGINT(64) NULL DEFAULT 0 AFTER `status`,
  ADD COLUMN `item_vendor_id` BIGINT(64) NULL DEFAULT 0 AFTER `service_item`,
  ADD COLUMN `item_vendor` MEDIUMTEXT NULL DEFAULT NULL AFTER `item_vendor_id`,
  ADD COLUMN `item_category_id` BIGINT(64) NULL DEFAULT 0 AFTER `item_vendor`,
  ADD COLUMN `item_category` MEDIUMTEXT NULL AFTER `item_category_id`,
  ADD COLUMN `item_top_category_id` BIGINT(64) NULL DEFAULT 0 AFTER `item_category`,
  ADD COLUMN `item_top_category` MEDIUMTEXT NULL DEFAULT NULL AFTER `item_top_category_id`,
  ADD COLUMN `patient_id` BIGINT(64) NULL DEFAULT 0 AFTER `user_id`,
  ADD COLUMN `address_id` BIGINT(64) NULL DEFAULT 0 AFTER `patient`,
  ADD COLUMN `leave_a_message` VARCHAR(400) NULL DEFAULT '' AFTER `payment_amount_cent`;


ALTER TABLE `go2nurse_service_item`
  CHANGE COLUMN `service_price` `service_price_cent` INT(11) NULL DEFAULT 0 ;



