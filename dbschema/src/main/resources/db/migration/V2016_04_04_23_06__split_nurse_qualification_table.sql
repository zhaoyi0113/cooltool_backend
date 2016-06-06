DROP TABLE IF EXISTS `nurse_qualification_file`;
CREATE TABLE `nurse_qualification_file` (
  `id`               BIGINT(64) NOT NULL AUTO_INCREMENT,
  `qualification_id` BIGINT(11) NOT NULL DEFAULT -1,
  `work_file_type_id`   INT(11)    NULL DEFAULT -1,
  `work_file_id`     BIGINT(64) NULL DEFAULT -1,
  `create_time`      DATETIME   NULL,
  `expiry_time`      DATETIME   NULL,
  PRIMARY KEY (`id`)
)
DEFAULT CHARACTER SET = utf8;

ALTER TABLE `nurse_qualification` DROP COLUMN `work_file_type`;
ALTER TABLE `nurse_qualification` DROP COLUMN `work_file_id`;
ALTER TABLE `nurse_qualification` DROP COLUMN `create_time`;
ALTER TABLE `nurse_qualification` DROP COLUMN `expiry_time`;