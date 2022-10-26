package com.hbc.dataupload.common.headers.v1;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ACTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.BOPIS_ELIGIBLE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CALENDAR_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CARRIER_SERVICE_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CITY;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.COUNTRY;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.DESCRIPTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.DESTINATION_GEO_ZONE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.EFFECTIVE_DATE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.EXPRESS_ELIGIBLE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.IS_ACTIVE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LAST_PICKUP_TIME;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LATITUDE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LONGITUDE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.NEXTDAY_ELIGIBLE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.NODE_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.NODE_TYPE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ORG_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.POSTAL_CODE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.POSTAL_CODE_PREFIX;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.PROVINCE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SDND_ELIGIBLE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SERVICE_OPTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SHIP_TO_HOME;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SOURCE_GEO_ZONE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.STATE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.STREET;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.TIMEZONE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.TIMEZONE1;

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
                "node-carrier",
                List.of(
                    ACTION, NODE_ID, ORG_ID, CARRIER_SERVICE_ID, SERVICE_OPTION, LAST_PICKUP_TIME)),
            Map.entry("transit-buffer", List.of(SOURCE_GEO_ZONE, DESTINATION_GEO_ZONE)));
  }

  public static List<String> getCSVExpectedHeaders(String key) {
    return csvExpectedHeadersMap.get(key);
  }
}
