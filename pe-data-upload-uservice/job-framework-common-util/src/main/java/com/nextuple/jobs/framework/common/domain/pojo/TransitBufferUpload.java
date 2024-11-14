/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.framework.common.domain.pojo;

import static com.nextuple.common.constants.CommonConstants.ACTION_TYPE;
import static com.nextuple.common.constants.CommonConstants.BUFFER_DAYS;
import static com.nextuple.common.constants.CommonConstants.BUFFER_END_DATE;
import static com.nextuple.common.constants.CommonConstants.BUFFER_START_DATE;
import static com.nextuple.common.constants.CommonConstants.CARRIER_SERVICE_ID;
import static com.nextuple.common.constants.CommonConstants.CREATE_BY;
import static com.nextuple.common.constants.CommonConstants.DESTINATION_GEOZONE;
import static com.nextuple.common.constants.CommonConstants.ORG_ID;
import static com.nextuple.common.constants.CommonConstants.SOURCE_GEOZONE;
import static com.nextuple.common.constants.CommonConstants.TRANSIT_BUFFER_CONFIG_REQUEST_ID;

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
