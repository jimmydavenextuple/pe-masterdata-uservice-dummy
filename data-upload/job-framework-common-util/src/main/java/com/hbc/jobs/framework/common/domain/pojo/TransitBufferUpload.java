package com.hbc.jobs.framework.common.domain.pojo;

import static com.hbc.common.constants.CommonConstants.ACTION_TYPE;
import static com.hbc.common.constants.CommonConstants.BUFFER_DAYS;
import static com.hbc.common.constants.CommonConstants.BUFFER_END_DATE;
import static com.hbc.common.constants.CommonConstants.BUFFER_START_DATE;
import static com.hbc.common.constants.CommonConstants.CARRIER_SERVICE_ID;
import static com.hbc.common.constants.CommonConstants.CREATE_BY;
import static com.hbc.common.constants.CommonConstants.DESTINATION_GEOZONE;
import static com.hbc.common.constants.CommonConstants.ORG_ID;
import static com.hbc.common.constants.CommonConstants.SOURCE_GEOZONE;
import static com.hbc.common.constants.CommonConstants.TRANSIT_BUFFER_CONFIG_REQUEST_ID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransitBufferUpload {

  private String orgId;
  private String carrierServiceId;
  private String sourceGeozone;
  private String destinationGeozone;
  private String bufferDays;
  private String bufferStartDate;
  private String bufferEndDate;
  private String action;
  private String createdBy;
  private String transitBufferConfigRequestId;

  public static String[] columnHeadersArray() {
    return new String[] {
      ORG_ID,
      CARRIER_SERVICE_ID,
      SOURCE_GEOZONE,
      DESTINATION_GEOZONE,
      BUFFER_DAYS,
      BUFFER_START_DATE,
      BUFFER_END_DATE,
      ACTION_TYPE,
      CREATE_BY,
      TRANSIT_BUFFER_CONFIG_REQUEST_ID
    };
  }
}
