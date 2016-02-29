CREATE TABLE `order_list` (
  `id` BIGINT(64) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(60) NULL,
  `count` INT NULL,
  `cash` DECIMAL (10,2)  NULL,
  PRIMARY KEY (`id`));
