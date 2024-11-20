/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CSVCommonConstants {
  public static final String PROCESSING_TIME = "processingTime (in hrs)";
  public static final String NODE_ID = "nodeId";
  public static final String CART_ID = "cartId";
  public static final String SFCC_RESPONSE = "sfccResponse";
  public static final String ORG_ID = "orgId";
  public static final String CARRIER_ID = "carrierId";
  public static final String CARRIER_SERVICE_ID = "carrierServiceId";
  public static final String SERVICE_OPTION = "serviceOption";
  public static final String ACTION_TYPE = "action";
  public static final String CARRIER_NAME = "carrierName";
  public static final String SERVICE_NAME = "serviceName";
  public static final String STATUS = "status";
  public static final String WORKING_CALENDER = "carrierServiceWorkingCalendar";
  public static final String STREET = "street";
  public static final String CITY = "city";
  public static final String CARRIER_SERVICES = "carrierServices";
  public static final String SERVICE_OPTIONS = "serviceOptions";

  public static final String ACTIVE = "ACTIVE";

  public static final String INACTIVE = "INACTIVE";
  public static final String STATE = "state";
  public static final String COUNTRY = "country";
  public static final String LATITUDE = "latitude";
  public static final String LONGITUDE = "longitude";
  public static final String TIMEZONE = "timezone";
  public static final String BUFFER_START_DATE = "bufferStartDate";
  public static final String BUFFER_END_DATE = "bufferEndDate";

  public static final String PROCESSING_LEAD_TIME = "processingLeadTime";
  public static final String NODE_TYPE = "nodeType";
  public static final String BUFFER_HOURS = "bufferHours";
  public static final String ERROR_MESSAGE = "errorMessage";
  public static final String PICKUP_TIME = "pickupTime";
  public static final String START_WORKING_TIME = "startWorkingTime";
  public static final String LAST_WORKING_TIME = "lastWorkingTime";
  public static final String NODE_WORKING_CALENDAR = "nodeWorkingCalendar";
  public static final String CUSTOM_REGIONS = "customRegions";
  public static final String ZIP_CODE = "zipCode";
  public static final String ZIP_CODE_PREFIX = "zipCodePrefix";
  public static final String REGION_ID = "regionId";
  public static final String CUSTOM_REGION_NAME = "customRegionName";
  public static final String CUSTOM_REGION_DESCRIPTION = "customRegionDescription";
  public static final String GEOZONES = "geozones";
  public static final String NODE_LABOUR_TIER = "nodeLabourTier";
  public static final String COST_TYPE = "costType";

  public static final String ORIGIN_NODE = "sourceNodeId";
  public static final String DESTINATION_NODE = "dropOffNodeId";
  public static final String PICKUP = "startTime";
  public static final String DROPOFF = "endTime";
}
