DROP TABLE IF EXISTS `go2nurse_patient`;
CREATE TABLE `go2nurse_patient` (
   `id`            BIGINT(64) NOT NULL AUTO_INCREMENT,
   `name`          VARCHAR(45) NULL DEFAULT '',
   `gender`        INT(11)     NULL DEFAULT 2,
   `identity_card` VARCHAR(45) NULL DEFAULT '',
   `mobile`        VARCHAR(32) NULL DEFAULT '',
   `birthday`      DATETIME    NULL DEFAULT now(),
   `time_created`  DATETIME    NULL DEFAULT now(),
   `status`        INT(11)     NULL DEFAULT 1,
   PRIMARY KEY (`id`)
)
   DEFAULT CHARSET=utf8;