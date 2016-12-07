CREATE OR REPLACE VIEW `go2nurse_view_vendor_patient_relation` AS
SELECT uuid() AS id,
       sr.time_created AS time_created,
       sr.user_id AS user_id,
       us.name AS user_name,
       sr.patient_id AS patient_id,
       pa.name AS patient_name,
       sr.item_vendor_type AS vendor_type,
       sr.item_vendor_id AS vendor_id,
       sr.item_vendor_department_id AS vendor_depart_id,
       "order" AS record_from,
       sr.id AS record_id
FROM go2nurse_service_order sr
     LEFT JOIN go2nurse_user us    ON sr.user_id=us.id
     LEFT JOIN go2nurse_patient pa ON sr.patient_id=pa.id
WHERE sr.status = 1 AND sr.order_status NOT IN (0, 6)
UNION ALL
SELECT uuid() AS id,
       uhr.time_created AS time_created,
       uhr.user_id AS user_id,
       us.name AS user_name,
       0 AS patient_id,
       '' AS patient_name,
       2 AS vendor_type,
       uhr.hospital_id AS vendor_id,
       uhr.department_id AS vendor_depart_id,
       "hospitalized" AS record_from,
       uhr.id AS record_id
FROM go2nurse_user_hospitalized_relation uhr
     LEFT JOIN go2nurse_user us    ON uhr.user_id=us.id
WHERE uhr.status = 1
UNION ALL
SELECT uuid() AS id,
       nvp.time_created AS time_created,
       nvp.user_id AS user_id,
       us.name AS user_name,
       nvp.patient_id AS patient_id,
       pa.name AS patient_name,
       nvp.vendor_type AS vendor_type,
       nvp.vendor_id AS vendor_id,
       nvp.vendor_depart_id AS vendor_depart_id,
       "nurse_visit" AS record_from,
       nvp.id AS record_id
FROM go2nurse_nurse_visit_patient nvp
     LEFT JOIN go2nurse_user us    ON nvp.user_id=us.id
     LEFT JOIN go2nurse_patient pa ON nvp.patient_id=pa.id
WHERE nvp.status = 1;



