DROP TABLE IF EXISTS `cathart_profile_photo`;
CREATE TABLE `cathart_profile_photo` (
  `id`          BIGINT(64)   NOT NULL AUTO_INCREMENT,
  `name`        VARCHAR(100)     NULL DEFAULT "",
  `image_id`    BIGINT(64)       NULL DEFAULT 0,
  `enable`      INT              NULL DEFAULT 1,
  `create_time` DATETIME         NULL DEFAULT now(),
  PRIMARY KEY (`id`)
)
DEFAULT CHARACTER SET=utf8;


DROP TABLE IF EXISTS `platform_activities`;
CREATE TABLE `platform_activities` (
  `id`          BIGINT(64)   NOT NULL AUTO_INCREMENT,
  `title`       VARCHAR(200)     NULL DEFAULT "",
  `subtitle`    VARCHAR(200)     NULL DEFAULT "",
  `description` VARCHAR(200)     NULL DEFAULT "",
  `time`        DATETIME         NULL DEFAULT now(),
  `place`       VARCHAR(200)     NULL DEFAULT "",
  `price`       DECIMAL          NULL DEFAULT 10,
  `content`     TEXT             null,
  PRIMARY KEY (`id`)
)
  DEFAULT CHARACTER SET=utf8;