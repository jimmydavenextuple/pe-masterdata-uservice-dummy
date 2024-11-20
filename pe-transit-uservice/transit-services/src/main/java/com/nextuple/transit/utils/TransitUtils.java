/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.utils;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.transit.domain.pojo.TransitDetailsValidationDto;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;

public class TransitUtils {
  private TransitUtils() {}

  private static final String ORG_ID = "orgId";
  private static final String CARRIER_SERVICE_ID = "carrierServiceId";
  private static final String SOURCE_GEOZONE = "sourceGeozone";
  private static final String DESTINATION_GEOZONE = "destinationGeozone";

  public static void validateTransitDetails(TransitDetailsValidationDto transitDetails)
      throws CommonServiceException {
    if (transitDetails.getBufferDays() == null) {
      transitDetails.setBufferDays(0.0);
    }
    if ((transitDetails.getTransitDays() + transitDetails.getBufferDays()) <= 0) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(transitDetails.getOrgId()).build());
      errorMap.put(
          SOURCE_GEOZONE,
          FieldError.builder().rejectedValue(transitDetails.getSourceGeozone()).build());
      errorMap.put(
          DESTINATION_GEOZONE,
          FieldError.builder().rejectedValue(transitDetails.getDestinationGeozone()).build());
      errorMap.put(
          CARRIER_SERVICE_ID,
          FieldError.builder().rejectedValue(transitDetails.getCarrierServiceId()).build());
      throw new CommonServiceException(
          "The sum of transit and buffer days is less or equal to 0",
          HttpStatus.BAD_REQUEST,
          0x1776,
          errorMap);
    }
  }
}
