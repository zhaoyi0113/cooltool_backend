
CREATE TABLE `tags_category` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NULL,
  `image` VARCHAR(45) NULL,
  `time_created` DATETIME NULL,
  PRIMARY KEY (`id`));


CREATE TABLE `tags` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `category_id` INT NULL,
  `name` VARCHAR(45) NULL,
  `image` VARCHAR(45) NULL,
  `time_created` DATETIME NULL,
  PRIMARY KEY (`id`));
