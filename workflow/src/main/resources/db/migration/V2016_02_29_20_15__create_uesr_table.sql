CREATE TABLE `patient` (
  `id` BIGINT(32) NOT NULL,
  `name` VARCHAR(45) NULL,
  `office_id` INT DEFAULT 0,
  `nickname` VARCHAR(45) NULL,
  `usercol` VARCHAR(45) NULL,
  `certificate_id` VARCHAR(45) NULL,
  `mobile`  VARCHAR(32) NULL,
  `age` INT DEFAULT 0,
  `birthdate` DATE NULL ,
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8;
