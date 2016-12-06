DROP TABLE IF EXISTS `go2nurse_user_current_visit`;
CREATE TABLE `go2nurse_user_current_visit` (
  `id`                        BIGINT(64) NOT NULL AUTO_INCREMENT,
  `time_created`                DATETIME NULL DEFAULT now(),
  `status`                       INT(11) NULL DEFAULT 0,
  `user_id`                   BIGINT(64) NULL DEFAULT 0,
  `diagnostic_point_id` BIGINT(64) NULL DEFAULT 0,
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;
