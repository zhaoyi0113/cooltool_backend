DROP TABLE IF EXISTS `cooltoo_nurse_authorization`;
CREATE TABLE `cooltoo_nurse_authorization` (
  `id`           BIGINT(64) NOT NULL AUTO_INCREMENT,
  `time_created`   DATETIME NULL DEFAULT now(),
  `status`          INT(11) NULL DEFAULT 1,
  `nurse_id`     BIGINT(64) NULL DEFAULT 0,
  `enable_order_head_nurse`        INT(11) NULL DEFAULT 1,
  `enable_order_admin`             INT(11) NULL DEFAULT 1,
  `enable_notification_head_nurse` INT(11) NULL DEFAULT 0,
  `enable_consultation_head_nurse` INT(11) NULL DEFAULT 1,
  `enable_consultation_admin`      INT(11) NULL DEFAULT 1,
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;