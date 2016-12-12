DROP TABLE IF EXISTS `go2nurse_nurse_wallet`;
CREATE TABLE `go2nurse_nurse_wallet` (
  `id`           BIGINT(64) NOT NULL AUTO_INCREMENT,
  `time_created`   DATETIME NULL DEFAULT now(),
  `status`          INT(11) NULL DEFAULT 0,
  `nurse_id`     BIGINT(64) NULL DEFAULT 0,
  `summary`    VARCHAR(200) NULL DEFAULT '',
  `amount`          INT(11) NULL DEFAULT 0,
  `reason`          INT(11) NULL DEFAULT 0,
  `reason_id`   VARCHAR(45) NULL DEFAULT 0,
  PRIMARY KEY (`id`))
  DEFAULT CHARACTER SET = utf8;
