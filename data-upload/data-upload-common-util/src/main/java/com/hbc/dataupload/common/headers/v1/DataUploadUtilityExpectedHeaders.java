package com.hbc.dataupload.common.headers.v1;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CITY;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.COUNTRY;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LATITUDE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LONGITUDE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ORG_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.POSTAL_CODE_PREFIX;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.STATE;
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
                "market-region",
                List.of(
                    ORG_ID,
                    POSTAL_CODE_PREFIX,
                    COUNTRY,
                    STATE,
                    CITY,
                    LATITUDE,
                    LONGITUDE,
                    TIMEZONE1)));
  }

  public static List<String> getCSVExpectedHeaders(String key) {
    return csvExpectedHeadersMap.get(key);
  }
}
