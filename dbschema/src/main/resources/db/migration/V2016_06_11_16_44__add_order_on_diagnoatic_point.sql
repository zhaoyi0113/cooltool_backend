ALTER TABLE `go2nurse_diagnostic_point`
ADD COLUMN `order` INT NULL DEFAULT 0 AFTER `time_created`;
