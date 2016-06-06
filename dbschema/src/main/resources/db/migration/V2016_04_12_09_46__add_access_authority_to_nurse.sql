ALTER TABLE `nurse` ADD COLUMN `authority` INT NULL DEFAULT 0;
UPDATE      `nurse` SET        `authority` = 1;