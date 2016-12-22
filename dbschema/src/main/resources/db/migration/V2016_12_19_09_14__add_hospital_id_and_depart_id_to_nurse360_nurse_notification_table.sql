ALTER TABLE `nurse360_notification`
  ADD COLUMN `vendor_type`  INT(11) NULL DEFAULT 0,
  ADD COLUMN `vendor_id` BIGINT(64) NULL DEFAULT 0,
  ADD COLUMN `depart_id` BIGINT(64) NULL DEFAULT 0;

DROP TABLE IF EXISTS `nurse360_notification_hospital_department_relation`;

