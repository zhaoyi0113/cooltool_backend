DROP TABLE IF EXISTS `go2nurse_manager_nurse_deny_user_patient`;
CREATE TABLE `go2nurse_manager_nurse_deny_user_patient` (
  `id`               BIGINT(64) NOT NULL AUTO_INCREMENT,
  `time_created`     DATETIME   NULL DEFAULT now(),
  `status`              INT(11) NULL DEFAULT 0,
  `vendor_type`         INT(11) NULL DEFAULT 0,
  `vendor_id`        BIGINT(64) NULL DEFAULT 0,
  `vendor_depart_id` BIGINT(64) NULL DEFAULT 0,
  `nurse_id`         BIGINT(64) NULL DEFAULT 0,
  `who_deny_user`       INT(11) NULL DEFAULT 0,
  `user_id`          BIGINT(64) NULL DEFAULT 0,
  `patient_id`       BIGINT(64) NULL DEFAULT 0,
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;

DROP TABLE IF EXISTS `go2nurse_service_vendor_authorization`;