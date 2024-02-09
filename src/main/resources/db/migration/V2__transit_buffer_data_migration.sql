INSERT INTO transit_buffers_data (
    org_id,
    carrier_service_id,
    source_geozone,
    destination_geozone,
    buffer_days,
    buffer_start_date,
    buffer_end_date,
    transit_buffer_config_request_id,
    created_date,
    last_modified_date,
    created_by,
    updated_by
)
SELECT
    org_id,
    carrier_service_id,
    source_geozone,
    destination_geozone,
    buffer_days,
    buffer_start_date,
    buffer_end_date,
    transit_buffer_config_request_id,
    created_date,
    last_modified_date,
    created_by,
    updated_by
FROM transit_buffer_data;
