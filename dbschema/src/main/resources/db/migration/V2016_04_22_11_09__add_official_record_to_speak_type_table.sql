INSERT INTO `speak_type` (`id`, `name`, `type`, `factor`) VALUES ('4', 'OFFICIAL', '3', '1');

UPDATE      `workfile_type` SET `name`='EMPLOYEES_CARD' WHERE `id`='3';
INSERT INTO `workfile_type` (`id`, `name`, `type`, `max_file_count`, `min_file_count`, `factor`) VALUES ('4', 'QUALIFICATION', '3', '1', '0', '1');
