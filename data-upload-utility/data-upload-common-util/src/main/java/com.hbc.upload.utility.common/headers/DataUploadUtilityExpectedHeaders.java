package com.hbc.upload.utility.common.headers;

import static com.hbc.upload.utility.common.constants.DataUploadUtilityConstants.ACTION;
import static com.hbc.upload.utility.common.constants.DataUploadUtilityConstants.CARRIER_SERVICE_ID;
import static com.hbc.upload.utility.common.constants.DataUploadUtilityConstants.LAST_PICKUP_TIME;
import static com.hbc.upload.utility.common.constants.DataUploadUtilityConstants.NODE_ID;
import static com.hbc.upload.utility.common.constants.DataUploadUtilityConstants.ORG_ID;
import static com.hbc.upload.utility.common.constants.DataUploadUtilityConstants.PROCESSING_TIME;
import static com.hbc.upload.utility.common.constants.DataUploadUtilityConstants.SERVICE_OPTION;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataUploadUtilityExpectedHeaders {
  private static final Map<String, List<String>> csvExpectedHeadersMap;

  static {
    Map<String, List<String>> map = new HashMap<>();

    map.put(
        "node-carrier",
        List.of(
            ACTION,
            NODE_ID,
            ORG_ID,
            CARRIER_SERVICE_ID,
            SERVICE_OPTION,
            PROCESSING_TIME,
            LAST_PICKUP_TIME));

    csvExpectedHeadersMap = Collections.unmodifiableMap(map);
  }

  public static List<String> getCSVExpectedHeaders(String key) {
    return csvExpectedHeadersMap.get(key);
  }
}
