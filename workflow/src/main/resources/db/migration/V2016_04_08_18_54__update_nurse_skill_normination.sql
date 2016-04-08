ALTER TABLE `nurse_skill_nomination`  ADD COLUMN `skill_type` INT NULL DEFAULT 0;
ALTER TABLE `occupation_skill` DROP COLUMN `type`;

