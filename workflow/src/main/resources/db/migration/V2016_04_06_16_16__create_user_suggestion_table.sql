DROP TABLE IF EXISTS `user_suggestion`;
CREATE TABLE `user_suggestion` (
  `id`          BIGINT(64)    NOT NULL AUTO_INCREMENT,
  `user_id`     BIGINT(64)    NOT NULL DEFAULT 0,
  `suggestion`  VARCHAR(1000) NULL     DEFAULT "",
  `create_time` DATETIME      NULL,
  PRIMARY KEY (`id`)
)
DEFAULT CHARACTER SET=utf8