-- Create postal_code_carrier_service table
-- Primary key: zipcode + carrier_service_id
-- Composite index on zipcode and carrier_service_id

CREATE TABLE IF NOT EXISTS postal_code_carrier_service (
    zipcode VARCHAR(20) NOT NULL,
    carrier_service_id VARCHAR(100) NOT NULL,
    is_red_zone BOOLEAN DEFAULT FALSE,
    red_zone_reason TEXT,
    created_by VARCHAR(100),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_by VARCHAR(100),
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 0,
    
    -- Primary key constraint
    CONSTRAINT pk_postal_code_carrier_service PRIMARY KEY (zipcode, carrier_service_id)
);

-- Create composite index on zipcode and carrier_service_id for performance
CREATE INDEX IF NOT EXISTS idx_zipcode_carrier_service 
ON postal_code_carrier_service (zipcode, carrier_service_id);

-- Create individual indexes for lookup by single fields
CREATE INDEX IF NOT EXISTS idx_postal_code_carrier_service_zipcode 
ON postal_code_carrier_service (zipcode);

CREATE INDEX IF NOT EXISTS idx_postal_code_carrier_service_carrier_service_id 
ON postal_code_carrier_service (carrier_service_id);

-- Create index for red zone lookups
CREATE INDEX IF NOT EXISTS idx_postal_code_carrier_service_red_zone 
ON postal_code_carrier_service (is_red_zone);

-- Add comments to the table and columns
COMMENT ON TABLE postal_code_carrier_service IS 'Stores postal code to carrier service mappings with red zone information';
COMMENT ON COLUMN postal_code_carrier_service.zipcode IS 'ZIP code for the postal code carrier service mapping';
COMMENT ON COLUMN postal_code_carrier_service.carrier_service_id IS 'Unique identifier of the carrier service';
COMMENT ON COLUMN postal_code_carrier_service.is_red_zone IS 'Flag indicating if the postal code is in a red zone for the carrier service';
COMMENT ON COLUMN postal_code_carrier_service.red_zone_reason IS 'Reason for the postal code being marked as red zone';
