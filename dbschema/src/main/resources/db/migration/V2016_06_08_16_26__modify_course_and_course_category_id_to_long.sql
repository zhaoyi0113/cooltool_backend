ALTER TABLE `go2nurse_course_category_relation`
       CHANGE COLUMN `id` `id` BIGINT(64) NOT NULL AUTO_INCREMENT ,
       CHANGE COLUMN `course_id` `course_id` BIGINT(64) NULL DEFAULT '0' ,
       CHANGE COLUMN `course_category_id` `course_category_id` BIGINT(64) NULL DEFAULT '0' ;


ALTER TABLE `go2nurse_course`
       CHANGE COLUMN `id` `id` BIGINT(64) NOT NULL AUTO_INCREMENT,
       CHANGE COLUMN `front_corver` `front_cover` BIGINT(64) NULL DEFAULT '0' ;



ALTER TABLE `go2nurse_course_category`
  CHANGE COLUMN `id` `id` BIGINT(64) NOT NULL AUTO_INCREMENT ;

