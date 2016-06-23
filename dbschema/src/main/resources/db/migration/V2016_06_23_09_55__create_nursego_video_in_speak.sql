DROP TABLE IF EXISTS `nursego_video_in_speak`;
CREATE TABLE `nursego_video_in_speak` (
  `id`          BIGINT(64) NOT NULL AUTO_INCREMENT,
  `speak_id`    BIGINT(64)     NULL DEFAULT 0,
  `video_id`    VARCHAR(64)    NULL DEFAULT 0,
  `snapshot`    BIGINT(64)     NULL DEFAULT 0,
  `background`  BIGINT(64)     NULL DEFAULT 0,
  `video_status`  INT(11)     NULL DEFAULT 0,
  `time_created`  DATETIME     NULL DEFAULT now(),
  `status`         INT(11)     NULL DEFAULT 0,
  PRIMARY KEY (`id`)
)
DEFAULT CHARACTER SET = utf8;