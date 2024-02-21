INSERT INTO node_carriers (
    carrier_service_id,
    node_id,
    org_id,
    service_option,
    created_date,
    last_modified_date,
    created_by,
    updated_by,
    last_pickup_time
)
SELECT
    carrier_service_id,
    node_id,
    org_id,
    service_option,
    created_date,
    last_modified_date,
    created_by,
    updated_by,
    last_pickup_time
FROM node_carrier
WHERE carrier_service_id != '';

INSERT INTO node_service_option (
    node_id,
    org_id,
    service_option,
    processing_time,
    created_date,
    last_modified_date,
    created_by,
    updated_by
)
SELECT
    node_id,
    org_id,
    service_option,
    processing_time,
    created_date,
    last_modified_date,
    created_by,
    updated_by
FROM node_carrier
WHERE carrier_service_id = '' and processing_time is not null;

INSERT INTO node_service_option_buffers (
    created_date,
    last_modified_date,
    created_by,
    updated_by,
    buffer_end_date,
    buffer_hours,
    buffer_start_date,
    node_id,
    org_id,
    service_option
)
SELECT
    created_date,
    last_modified_date,
    created_by,
    updated_by,
    buffer_end_date,
    buffer_hours,
    buffer_start_date,
    node_id,
    org_id,
    service_option
FROM node_carrier
WHERE carrier_service_id = '' and buffer_start_date is not null and buffer_end_date is not null and buffer_hours is not null;
