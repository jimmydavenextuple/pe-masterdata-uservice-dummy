package com.hbc.dataupload.common.headers.v1;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.*;

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
                    POSTAL_CODE_PREFIX,
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
                    PROVINCE,
                    POSTAL_CODE,
                    COUNTRY,
                    LATITUDE,
                    LONGITUDE,
                    TIMEZONE,
                    SHIP_TO_HOME,
                    SDND_ELIGIBLE,
                    BOPIS_ELIGIBLE,
                    EXPRESS_ELIGIBLE,
                    NODE_TYPE,
                    IS_ACTIVE,
                    NEXTDAY_ELIGIBLE)),
            Map.entry(
                "node-calendar",
                List.of(ACTION, CALENDAR_ID, NODE_ID, ORG_ID, DESCRIPTION, EFFECTIVE_DATE)),
            Map.entry(
                "transit",
                List.of(
                    ORG_ID, CARRIER_SERVICE_HEADER_TRANSIT_TIMES, DESTINATION_SOURCE_FSA_HEADER)),
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
                List.of(NODE_ID, ORG_ID, SERVICE_OPTIONS, PROCESSING_TIME_IN_HRS, ACTION)),
            Map.entry(
                "node-service-option-buffer",
                List.of(ORG_ID, NODE_ID, SERVICE_OPTION, BUFFER_HOURS, START_TIME, END_TIME)));
  }

  public static List<String> getCSVExpectedHeaders(String key) {
    return csvExpectedHeadersMap.get(key);
  }
}
