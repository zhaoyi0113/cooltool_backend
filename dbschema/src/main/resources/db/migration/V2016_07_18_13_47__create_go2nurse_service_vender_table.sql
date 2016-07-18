DROP TABLE IF EXISTS `go2nurse_service_vendor`;
CREATE TABLE `go2nurse_service_vendor` (
  `id` BIGINT(64) NOT NULL AUTO_INCREMENT,
  `time_created` DATETIME NULL DEFAULT now(),
  `status` INT(11) NULL DEFAULT 0,
  `name` VARCHAR(200) NULL DEFAULT '',
  `description` MEDIUMTEXT NULL,
  `logo_id` BIGINT(64) NULL DEFAULT 0,
  PRIMARY KEY (`id`))
  DEFAULT CHARACTER SET = utf8;