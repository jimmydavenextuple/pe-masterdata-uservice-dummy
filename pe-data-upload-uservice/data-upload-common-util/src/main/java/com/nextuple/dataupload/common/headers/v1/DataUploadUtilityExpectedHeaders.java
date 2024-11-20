/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.common.headers.v1;

import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.*;

import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DataUploadUtilityExpectedHeaders {
  private static final Map<String, List<String>> csvExpectedHeadersMap;

  static {
    csvExpectedHeadersMap =
        Map.ofEntries(
            Map.entry(
                "postal-code-timezone",
                List.of(
                    ACTION,
                    ORG_ID,
                    ZIP_CODE,
                    ZIP_CODE_PREFIX,
                    COUNTRY,
                    STATE,
                    CITY,
                    LATITUDE,
                    LONGITUDE,
                    TIMEZONE1)),
            Map.entry(
                "nodes",
                List.of(
                    ACTION,
                    NODE_ID,
                    ORG_ID,
                    STREET,
                    CITY,
                    STATE,
                    ZIP_CODE,
                    COUNTRY,
                    LATITUDE,
                    LONGITUDE,
                    TIMEZONE,
                    SHIP_TO_HOME,
                    BOPIS_ELIGIBLE,
                    NODE_TYPE,
                    NODE_LABOUR_TIER,
                    IS_ACTIVE,
                    START_WORKING_TIME,
                    LAST_WORKING_TIME)),
            Map.entry(
                "node-calendar",
                List.of(ACTION, CALENDAR_ID, NODE_ID, ORG_ID, DESCRIPTION, EFFECTIVE_DATE)),
            Map.entry(
                "transit",
                List.of(ORG_ID, CARRIER_SERVICE_HEADER, DESTINATION_SOURCE_GEOZONE_HEADER)),
            Map.entry(
                "carrier-service",
                List.of(
                    ACTION,
                    ORG_ID,
                    CARRIER_ID,
                    CARRIER_NAME,
                    CARRIER_SERVICE_ID,
                    SERVICE_NAME,
                    SERVICE_OPTIONS)),
            Map.entry(
                "carrier-service-calendar",
                List.of(
                    ACTION,
                    CALENDAR_ID,
                    ORG_ID,
                    CARRIER_SERVICE_ID,
                    SHIPPING_STAGE,
                    DESCRIPTION,
                    EFFECTIVE_DATE)),
            Map.entry(
                "node-carrier",
                List.of(
                    ACTION, NODE_ID, ORG_ID, CARRIER_SERVICE_ID, SERVICE_OPTION, LAST_PICKUP_TIME)),
            Map.entry("transit-buffer", List.of(SOURCE_GEO_ZONE, DESTINATION_GEO_ZONE)),
            Map.entry(
                "calendar",
                List.of(
                    ACTION,
                    CALENDAR_ID,
                    ORG_ID,
                    DESCRIPTION,
                    IS_MONDAY_WORKING,
                    IS_TUESDAY_WORKING,
                    IS_WEDNESDAY_WORKING,
                    IS_THURSDAY_WORKING,
                    IS_FRIDAY_WORKING,
                    IS_SATURDAY_WORKING,
                    IS_SUNDAY_WORKING,
                    EXCEPTION_DAYS)),
            Map.entry(
                "pickup-calendar",
                List.of(
                    ACTION,
                    CALENDAR_ID,
                    ORG_ID,
                    NODE_ID,
                    CARRIER_SERVICE_ID,
                    DESCRIPTION,
                    EFFECTIVE_DATE)),
            Map.entry(
                "processing-lead-times",
                List.of(ACTION, NODE_ID, ORG_ID, SERVICE_OPTIONS, PROCESSING_TIME_IN_HRS)),
            Map.entry(
                "node-service-option-buffer",
                List.of(
                    ACTION, ORG_ID, NODE_ID, SERVICE_OPTION, BUFFER_HOURS, START_TIME, END_TIME)),
            Map.entry(
                "edd-computation",
                List.of(
                    ORG_ID,
                    SERVICE_OPTIONS,
                    CART_ID,
                    SESSION_ID,
                    PAGE_NAME,
                    SHIP_TO_ADDRESS_ZIPCODE,
                    SHIP_TO_ADDRESS_REGION,
                    SHIP_TO_ADDRESS_STATE,
                    SHIP_TO_ADDRESS_COUNTRY,
                    SHIP_TO_ADDRESS_TIMEZONE,
                    LINES_REQUIRED_QTY,
                    LINES_LINE_ID,
                    LINES_SERVICE_OPTION,
                    LINES_ITEM_ITEM_ID,
                    LINES_ITEM_PRODUCT_CLASS,
                    LINES_ITEM_UNIT_OF_MEASURE,
                    LINES_SHIP_TO_ADDRESS_ZIPCODE,
                    LINES_SHIP_TO_ADDRESS_REGION,
                    LINES_SHIP_TO_ADDRESS_STATE,
                    LINES_SHIP_TO_ADDRESS_COUNTRY,
                    LINES_SHIP_TO_ADDRESS_TIMEZONE)),
            Map.entry(
                "custom-region",
                List.of(
                    ACTION,
                    ORG_ID,
                    REGION_ID,
                    CUSTOM_REGION_NAME,
                    CUSTOM_REGION_DESCRIPTION,
                    GEOZONES)),
            Map.entry("zones", List.of(CARRIER_SERVICE_HEADER, DESTINATION_SOURCE_GEOZONE_HEADER)),
            Map.entry("cost-definition", List.of(COST_DEF_ORG_ID, COST_DEF_COST_TYPE)));
  }

  public static List<String> getCSVExpectedHeaders(String key) {
    return csvExpectedHeadersMap.get(key);
  }
}
