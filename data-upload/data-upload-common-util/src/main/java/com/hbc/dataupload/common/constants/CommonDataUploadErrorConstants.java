package com.hbc.dataupload.common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonDataUploadErrorConstants {
  public static final String MARKET_REGION_DATA_UPLOAD_INVALID_FILE_HEADERS =
      "Market Region data uploaded file has invalid headers.";
  public static final String MARKET_REGION_DATA_UPLOAD_INVALID_FILE_TYPE =
      "Market Region data uploaded file has invalid file type.";

  public static final String NO_RECORDS_FOUND_IN_THE_CSV = "No Records found in the csv";
}
