ALTER TABLE `go2nurse_service_order_pingpp`
  CHANGE COLUMN `order_id` `order_id` BIGINT(64) NULL DEFAULT '0' AFTER `status`,
  CHANGE COLUMN `pingpp_type` `charge_type` INT(11) NULL DEFAULT '0' AFTER `app_type`,
  CHANGE COLUMN `pingpp_id` `charge_id` VARCHAR(100) NULL DEFAULT '' ,
  CHANGE COLUMN `pingpp_json` `charge_json` MEDIUMTEXT NULL DEFAULT NULL ,
  ADD COLUMN `webhooks_event_id` VARCHAR(100) NULL DEFAULT '' AFTER `charge_json`,
  ADD COLUMN `webhooks_event_json` MEDIUMTEXT NULL AFTER `webhooks_event_id`,
  ADD COLUMN `charge_status` VARCHAR(45) NULL AFTER `webhooks_event_json`,
  RENAME TO  `go2nurse_service_order_charge_pingpp` ;
