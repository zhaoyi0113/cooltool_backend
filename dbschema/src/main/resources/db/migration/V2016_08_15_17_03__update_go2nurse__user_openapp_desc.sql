ALTER TABLE `go2nurse_user_openapp_desc`
CHANGE COLUMN `idgo2nurse_user_openapp_desc` `id` BIGINT(11) NOT NULL AUTO_INCREMENT ,
ADD COLUMN `user_id` BIGINT(11) NULL  DEFAULT 0 ;
