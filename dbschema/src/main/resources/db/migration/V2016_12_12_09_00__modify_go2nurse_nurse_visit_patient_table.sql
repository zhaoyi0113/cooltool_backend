ALTER TABLE `go2nurse_nurse_visit_patient`
    ADD COLUMN `nurse_sign`          BIGINT(64) NULL DEFAULT 0,
    ADD COLUMN `address`                   TEXT NULL,
    ADD COLUMN `patient_record_no` VARCHAR(100) NULL,
    ADD COLUMN `note`                      TEXT NULL;