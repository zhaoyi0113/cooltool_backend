DROP TABLE IF EXISTS `images_in_speak`;
CREATE TABLE `images_in_speak` (
  `id`          BIGINT(64) NOT NULL AUTO_INCREMENT,
  `speak_id`    BIGINT(64)     NULL DEFAULT 0,
  `image_id`    BIGINT(64)     NULL DEFAULT 0,
  `create_time` DATETIME       NULL,
  PRIMARY KEY (`id`)
)
DEFAULT CHARACTER SET = utf8;