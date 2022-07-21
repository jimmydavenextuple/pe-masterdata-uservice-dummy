package com.hbc.dataupload.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CarrierCalendarDataUploadConstants {
  public static final String CARRIER_CALENDAR_DATA_UPLOAD_INVALID_FILE_TYPE =
      "Carrier Calendar data uploaded file is not a csv file.";
  public static final String CARRIER_CALENDAR_DATA_UPLOAD_FILE_EMPTY_RECORDS =
      "Carrier Calendar data uploaded file has no records.";
  public static final String CARRIER_CALENDAR_DATA_UPLOAD_LARGE_FILE_SIZE =
      "Carrier Calendar data uploaded file has size greater than 10240 kB.";
  public static final String CARRIER_CALENDAR_DATA_UPLOAD_LARGE_ROW_SIZE =
      "Carrier Calendar data uploaded file has exceeded maximum file size limit.";
  public static final String CARRIER_CALENDAR_DATA_UPLOAD_INVALID_FILE_HEADERS =
      "Carrier Calendar data uploaded file has invalid headers.";
}
