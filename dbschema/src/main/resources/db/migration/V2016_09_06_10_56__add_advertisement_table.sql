CREATE TABLE `go2nurse_advertisement` (
  `id` BIGINT(64) NOT NULL AUTO_INCREMENT,
  `time_created` DATETIME DEFAULT now(),
  `status` INT(11) DEFAULT '0',
  `front_cover` BIGINT(64) DEFAULT '0',
  `details_url` VARCHAR(1000) DEFAULT '',
  `order` INT(11) DEFAULT '0',
  PRIMARY KEY (`id`)
)
DEFAULT CHARACTER SET = utf8;