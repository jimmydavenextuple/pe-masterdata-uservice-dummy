package com.hbc.dataupload.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TransitDataUploadConstants {
  public static final String TRANSIT_DATA_UPLOAD_INVALID_FILE_TYPE =
      "Transit data uploaded file is not a csv file.";
  public static final String TRANSIT_DATA_UPLOAD_FILE_EMPTY_RECORDS =
      "Transit data uploaded file has no records.";
  public static final String TRANSIT_DATA_UPLOAD_LARGE_FILE_SIZE =
      "Transit data uploaded file has size greater than 10240 kB.";
  public static final String TRANSIT_DATA_UPLOAD_LARGE_ROW_SIZE =
      "Transit data uploaded file has more than 1000 rows.";
  public static final String TRANSIT_DATA_UPLOAD_INVALID_FILE_HEADERS =
      "Transit data uploaded file has invalid headers.";
  public static final String TRANSIT_DATA_UPLOAD_SUCCESS = "Transit Data successfully uploaded!";
  public static final String TRANSIT_DATA_UPLOAD_FAILED = "Transit Data upload failed!";
  public static final String TRANSIT_DATA_UPLOAD_PARTIAL_SUCCESS =
      "Transit Data partially uploaded with some rows failed!";
}
