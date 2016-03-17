CREATE TABLE `nurse_speak_comment` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `nurse_speak_id` BIGINT(11) NOT NULL,
  `user_make_comment_id` BIGINT(11) NOT NULL,
  #this field can be zero for the comment just reply to nurse speak
  `user_replied_to_id` BIGINT(11) NOT NULL DEFAULT 0,
  `comment` VARCHAR(512) NULL,
  `time` DATETIME NOT NULL,
  PRIMARY KEY (`id`)
)
DEFAULT CHARACTER SET = utf8;

CREATE TABLE `nurse_speak_thumbs_up` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `nurse_speak_id` BIGINT(11) NOT NULL,
  `thumbs_up_user_id` BIGINT(11) NOT NULL,
  `time` DATETIME NOT NULL,
  PRIMARY KEY (`id`)
)
  DEFAULT CHARACTER SET = utf8;

ALTER TABLE `nurse_speak`
ADD COLUMN `speak_type` VARCHAR(20) NOT NULL;
