ALTER TABLE `go2nurse_nurse_visit_patient` 
ADD COLUMN `vendor_type` INT(11) NULL DEFAULT 0 AFTER `patient_sign`,
ADD COLUMN `vendor_id` BIGINT(64) NULL DEFAULT 0 AFTER `vendor_type`,
ADD COLUMN `vendor_depart_id` BIGINT(64) NULL DEFAULT 0 AFTER `vendor_id`;



CREATE OR REPLACE VIEW `go2nurse_view_vendor_patient_relation` AS
SELECT uuid() AS id, sr.time_created, sr.user_id, sr.patient_id, sr.item_vendor_type AS vendor_type, sr.item_vendor_id AS vendor_id, sr.item_vendor_department_id AS vendor_depart_id, "order" AS record_from, sr.id AS record_id
FROM go2nurse_service_order sr
WHERE sr.status = 1 AND sr.order_status NOT IN (0, 6)
UNION ALL
SELECT uuid() AS id, uhr.time_created, uhr.user_id, 0, 2 AS vendor_type, uhr.hospital_id AS vendor_id, uhr.department_id AS vendor_depart_id, "hospitalized" AS record_from, uhr.id AS record_id
FROM go2nurse_user_hospitalized_relation uhr
WHERE uhr.status = 1
UNION ALL
SELECT uuid() AS id, nvp.time_created, nvp.user_id, nvp.patient_id, nvp.vendor_type AS vendor_type, nvp.vendor_id AS vendor_id, nvp.vendor_depart_id AS vendor_depart_id, "nurse_visit" AS record_from, nvp.id AS record_id
FROM go2nurse_nurse_visit_patient nvp
WHERE nvp.status = 1;