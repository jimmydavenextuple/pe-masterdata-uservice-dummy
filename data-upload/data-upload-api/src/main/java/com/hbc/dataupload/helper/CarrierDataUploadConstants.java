package com.hbc.dataupload.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CarrierDataUploadConstants {
  public static final String CARRIER_DATA_UPLOAD_INVALID_FILE_TYPE =
      "Carrier data uploaded file is not a csv file.";
  public static final String CARRIER_DATA_UPLOAD_FILE_EMPTY_RECORDS =
      "Carrier data uploaded file has no records.";
  public static final String CARRIER_DATA_UPLOAD_LARGE_FILE_SIZE =
      "Carrier data uploaded file has size greater than 10240 kB.";
  public static final String CARRIER_DATA_UPLOAD_LARGE_ROW_SIZE =
      "Carrier data uploaded file has more than 1000 rows.";
  public static final String CARRIER_DATA_UPLOAD_INVALID_FILE_HEADERS =
      "Carrier data uploaded file has invalid headers.";
  public static final String CARRIER_DATA_UPLOAD_SUCCESS = "Carrier Data successfully uploaded!";
  public static final String CARRIER_DATA_UPLOAD_FAILED = "Carrier Data upload failed!";
  public static final String CARRIER_DATA_UPLOAD_PARTIAL_SUCCESS =
      "Carrier Data partially uploaded with some rows failed!";
}
