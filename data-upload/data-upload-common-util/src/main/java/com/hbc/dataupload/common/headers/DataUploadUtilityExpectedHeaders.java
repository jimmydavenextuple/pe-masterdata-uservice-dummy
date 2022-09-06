package com.hbc.dataupload.common.headers;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ACTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ALLOCATION_RULE_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.BOPIS_ELIGIBLE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.BUFFER_DAYS;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.BUFFER_HOURS;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CALENDAR_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CARRIER_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CARRIER_NAME;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CARRIER_SERVICE_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CITY;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.COUNTRY;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.DESCRIPTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.DESTINATION_GEO_ZONE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.EFFECTIVE_DATE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.END_TIME;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.EXCEPTION_DAYS;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.EXPRESS_ELIGIBLE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.IS_ACTIVE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.IS_FRIDAY_WORKING;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.IS_MONDAY_WORKING;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.IS_SATURDAY_WORKING;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.IS_SUNDAY_WORKING;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.IS_THURSDAY_WORKING;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.IS_TUESDAY_WORKING;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.IS_WEDNESDAY_WORKING;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.KEY;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LAST_PICKUP_TIME;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LATITUDE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LONGITUDE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.NEXTDAY_ELIGIBLE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.NODE_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.NODE_TYPE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ORG_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.POSTAL_CODE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.POSTAL_CODE_PREFIX;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.PRIORITY;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.PROCESSING_TIME;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.PROVINCE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SDND_ELIGIBLE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SELECTION_CRITERIA;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SERVICE_NAME;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SERVICE_OPTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SERVICE_OPTIONS;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SHIPPING_STAGE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SHIP_TO_HOME;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SOURCE_GEO_ZONE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SOURCE_NODES;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.START_TIME;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.STATE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.STREET;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.TIMEZONE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.TIMEZONE1;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.TRANSIT_DAYS;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.TYPE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.WEIGHTAGE;

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
                "node",
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
                "node-carrier",
                List.of(
                    ACTION,
                    NODE_ID,
                    ORG_ID,
                    CARRIER_SERVICE_ID,
                    SERVICE_OPTION,
                    PROCESSING_TIME,
                    LAST_PICKUP_TIME)),
            Map.entry(
                "carrier",
                List.of(
                    ACTION,
                    ORG_ID,
                    CARRIER_ID,
                    CARRIER_NAME,
                    CARRIER_SERVICE_ID,
                    SERVICE_NAME,
                    SERVICE_OPTIONS)),
            Map.entry(
                "promiseSourcingRule",
                List.of(
                    ACTION,
                    ORG_ID,
                    SERVICE_OPTION,
                    DESTINATION_GEO_ZONE,
                    SOURCE_NODES,
                    PRIORITY,
                    ALLOCATION_RULE_ID)),
            Map.entry(
                "postalCodeTimezone",
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
            Map.entry("weightageConfiguration", List.of(ORG_ID, TYPE, KEY, WEIGHTAGE, ACTION)),
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
                "node-calendar",
                List.of(ACTION, CALENDAR_ID, NODE_ID, ORG_ID, DESCRIPTION, EFFECTIVE_DATE)),
            Map.entry(
                "carrier-calendar",
                List.of(
                    ACTION,
                    CALENDAR_ID,
                    ORG_ID,
                    CARRIER_SERVICE_ID,
                    SHIPPING_STAGE,
                    DESCRIPTION,
                    EFFECTIVE_DATE)),
            Map.entry(
                "node-carrier-calendar",
                List.of(
                    ACTION,
                    CALENDAR_ID,
                    ORG_ID,
                    NODE_ID,
                    CARRIER_SERVICE_ID,
                    DESCRIPTION,
                    EFFECTIVE_DATE)),
            Map.entry(
                "transit",
                List.of(
                    ACTION,
                    ORG_ID,
                    SOURCE_GEO_ZONE,
                    DESTINATION_GEO_ZONE,
                    CARRIER_SERVICE_ID,
                    TRANSIT_DAYS)),
            Map.entry(
                "node-service-option-buffer",
                List.of(ORG_ID, NODE_ID, SERVICE_OPTION, BUFFER_HOURS, START_TIME, END_TIME)),
            Map.entry(
                "transit-buffer",
                List.of(
                    ORG_ID,
                    CARRIER_SERVICE_ID,
                    ACTION,
                    SOURCE_GEO_ZONE,
                    DESTINATION_GEO_ZONE,
                    BUFFER_DAYS,
                    START_TIME,
                    END_TIME)),
            Map.entry(
                "node-carrier-selection",
                List.of(
                    ORG_ID,
                    SOURCE_GEO_ZONE,
                    DESTINATION_GEO_ZONE,
                    SERVICE_OPTION,
                    SELECTION_CRITERIA,
                    ACTION)));
  }

  public static List<String> getCSVExpectedHeaders(String key) {
    return csvExpectedHeadersMap.get(key);
  }
}
