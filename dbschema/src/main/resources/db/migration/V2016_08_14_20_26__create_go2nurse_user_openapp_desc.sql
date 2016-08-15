CREATE TABLE `go2nurse_user_openapp_desc` (
  `idgo2nurse_user_openapp_desc`  BIGINT(11) NOT NULL AUTO_INCREMENT,
  `openid` VARCHAR(128) NULL,
  `unionid` VARCHAR(128) NULL,
  `data` TEXT(65535) NULL,
  `channel` INT NULL DEFAULT 0,
  PRIMARY KEY (`idgo2nurse_user_openapp_desc`));
