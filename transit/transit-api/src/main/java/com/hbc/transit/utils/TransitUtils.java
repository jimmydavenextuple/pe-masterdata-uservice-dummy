package com.hbc.transit.utils;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.error.FieldError;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;

public class TransitUtils {
  private TransitUtils() {}

  private static final String ORG_ID = "orgId";
  private static final String CARRIER_SERVICE_ID = "carrierServiceId";
  private static final String SOURCE_GEOZONE = "sourceGeozone";
  private static final String DESTINATION_GEOZONE = "destinationGeozone";

  public static void validateTransitDetails(
      Float transitDays,
      Double bufferDays,
      String orgId,
      String sourceGeozone,
      String destinationGeozone,
      String carrierServiceId)
      throws CommonServiceException {
    if (bufferDays == null) {
      bufferDays = 0.0;
    }
    if ((transitDays + bufferDays) <= 0) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(SOURCE_GEOZONE, FieldError.builder().rejectedValue(sourceGeozone).build());
      errorMap.put(
          DESTINATION_GEOZONE, FieldError.builder().rejectedValue(destinationGeozone).build());
      errorMap.put(
          CARRIER_SERVICE_ID, FieldError.builder().rejectedValue(carrierServiceId).build());
      throw new CommonServiceException(
          "The sum of transit and buffer days is less or equal to 0",
          HttpStatus.BAD_REQUEST,
          0x1776,
          errorMap);
    }
  }
}
