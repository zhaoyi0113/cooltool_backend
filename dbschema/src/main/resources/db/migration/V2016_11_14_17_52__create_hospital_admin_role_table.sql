DROP TABLE IF EXISTS `nurse360_hospital_management_url`;
DROP TABLE IF EXISTS `nurse360_hospital_admin_access_url`;


DROP TABLE IF EXISTS `nurse360_hospital_admin_roles`;
CREATE TABLE `nurse360_hospital_admin_roles` (
  `id`           BIGINT(64) NOT NULL AUTO_INCREMENT,
  `time_created` DATETIME   NULL DEFAULT now(),
  `status`          INT(11) NULL DEFAULT 0,
  `admin_id`     BIGINT(64) NULL DEFAULT 0,
  `admin_role`      INT(11) NULL DEFAULT 0,
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;