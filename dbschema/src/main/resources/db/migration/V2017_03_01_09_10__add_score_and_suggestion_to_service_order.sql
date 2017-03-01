ALTER TABLE `go2nurse_service_order`
  CHANGE COLUMN `score` `score_attitude` FLOAT NULL DEFAULT '0' AFTER `item_manage_by`,
  ADD COLUMN `score_standard` FLOAT NULL DEFAULT 0,
  ADD COLUMN `score_in_time` FLOAT NULL DEFAULT 0,
  ADD COLUMN `opinions_suggestions` VARCHAR(300) NULL DEFAULT '';