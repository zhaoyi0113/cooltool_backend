DROP TABLE IF EXISTS `workfile_type`;
CREATE TABLE `workfile_type` (
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(100) NULL DEFAULT '',  # name 只是用来描述 type 类型. 但是不能为空.
    `type` INT NULL DEFAULT -1,           # Type 要唯一,每加一个Type,就要对应的在一个对应的枚举值,并添加对应的API接口.
    `max_file_count` INT(11) NULL DEFAULT 1,
    `min_file_count` INT(11) NULL DEFAULT 1,
    `factor` INT(11) NULL DEFAULT 1,
    `image_id` BIGINT(64) NULL DEFAULT -1,
    `disable_image_id` BIGINT(64) NULL DEFAULT -1,
    PRIMARY KEY (`id`)
)
    DEFAULT CHARACTER SET = utf8;


DROP TABLE IF EXISTS `speak_type`;
CREATE TABLE `speak_type` (
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(100) NULL DEFAULT '', # name 只是用来描述 type 类型. 但是不能为空.
    `type` INT NULL DEFAULT -1,          # Type 要唯一,每加一个Type,就要对应的在一个对应的枚举值,并添加对应的API接口.
    `factor` INT(11) NULL DEFAULT 1,     # 每一条该类型的 Speak 对应的分值,用于计算等级的
    `image_id` BIGINT(64) NULL DEFAULT -1,
    `disable_image_id` BIGINT(64) NULL DEFAULT -1,
    PRIMARY KEY (`id`)
)
    DEFAULT CHARACTER SET = utf8;

ALTER TABLE `nurse_speak`         MODIFY COLUMN `speak_type`     INT(11) NULL DEFAULT -1;
ALTER TABLE `nurse_qualification` MODIFY COLUMN `work_file_type` INT(11) NULL DEFAULT -1;


INSERT INTO `speak_type`(`name`, `type`) VALUES ('SMUG',         0);
INSERT INTO `speak_type`(`name`, `type`) VALUES ('CATHART',      1);
INSERT INTO `speak_type`(`name`, `type`) VALUES ('ASK_QUESTION', 2);

INSERT INTO `workfile_type`(`name`, `type`) VALUES ('UNKNOW',         0);
INSERT INTO `workfile_type`(`name`, `type`) VALUES ('IDENTIFICATION', 1);
INSERT INTO `workfile_type`(`name`, `type`) VALUES ('WORK_FILE',      2);