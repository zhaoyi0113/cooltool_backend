ALTER TABLE `hospital` ADD COLUMN    `enable`   INT(11)      DEFAULT -1;
ALTER TABLE `hospital` ADD COLUMN    `address`  VARCHAR(200) DEFAULT '';
ALTER TABLE `hospital` ADD COLUMN    `district` INT(11)      DEFAULT -1;
ALTER TABLE `hospital` MODIFY COLUMN `city`     INT(11)      DEFAULT -1;
ALTER TABLE `hospital` MODIFY COLUMN `province` INT(11)      DEFAULT -1;

# ALTER TABLE `hospital_department` ADD COLUMN `description`      VARCHAR(1000) NULL DEFAULT '';
# ALTER TABLE `hospital_department` ADD COLUMN `enable`           INT(11)       NULL DEFAULT -1;
# ALTER TABLE `hospital_department` ADD COLUMN `image_id`         BIGINT(64)    NULL DEFAULT -1;
# ALTER TABLE `hospital_department` ADD COLUMN `disable_image_id` BIGINT(64)    NULL DEFAULT -1;
# ALTER TABLE `hospital_department` ADD COLUMN `level`            INT(11) NULL  NULL DEFAULT -1;


DROP TABLE IF EXISTS `region`;
CREATE TABLE `region` (
  `id`           INT(11) NOT NULL,
  `code`         VARCHAR(100) NOT NULL,
  `name`         VARCHAR(100) NOT NULL,
  `en_name`      VARCHAR(100) NOT NULL,
  `short_en_name`VARCHAR(100) NOT NULL,
  `parent_id` INT(11) NOT NULL DEFAULT -1,
  PRIMARY KEY (`id`)
)
  DEFAULT CHARACTER SET = utf8;