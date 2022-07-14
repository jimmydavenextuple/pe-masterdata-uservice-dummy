package com.hbc.dataupload.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostalCodeTimezoneDataUploadConstants {
  public static final String POSTAL_CODE_TIMEZONE_DATA_UPLOAD_INVALID_FILE_TYPE =
      "Postal Code Timezone data uploaded file is not a csv file.";
  public static final String POSTAL_CODE_TIMEZONE_DATA_UPLOAD_FILE_EMPTY_RECORDS =
      "Postal Code Timezone data uploaded file has no records.";
  public static final String POSTAL_CODE_TIMEZONE_DATA_UPLOAD_LARGE_FILE_SIZE =
      "Postal Code Timezone data uploaded file has size greater than 10240 kB.";
  public static final String POSTAL_CODE_TIMEZONE_DATA_UPLOAD_LARGE_ROW_SIZE =
      "Postal Code Timezone data uploaded file has more than 1000 rows.";
  public static final String POSTAL_CODE_TIMEZONE_DATA_UPLOAD_INVALID_FILE_HEADERS =
      "Postal Code Timezone data uploaded file has invalid headers.";
  public static final String POSTAL_CODE_TIMEZONE_DATA_UPLOAD_SUCCESS =
      "Postal Code Timezone Data successfully uploaded!";
  public static final String POSTAL_CODE_TIMEZONE_DATA_UPLOAD_FAILED =
      "Postal Code Timezone Data upload failed!";
  public static final String POSTAL_CODE_TIMEZONE_DATA_UPLOAD_PARTIAL_SUCCESS =
      "Postal Code Timezone Data partially uploaded with some rows failed!";
}
