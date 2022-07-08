package com.hbc.dataupload.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NodeCalendarDataUploadConstants {
  public static final String NODE_CALENDAR_DATA_UPLOAD_INVALID_FILE_TYPE =
      "Node Calendar data uploaded file is not a csv file.";
  public static final String NODE_CALENDAR_DATA_UPLOAD_FILE_EMPTY_RECORDS =
      "Node Calendar data uploaded file has no records.";
  public static final String NODE_CALENDAR_DATA_UPLOAD_LARGE_FILE_SIZE =
      "Node Calendar data uploaded file has size greater than 10240 kB.";
  public static final String NODE_CALENDAR_DATA_UPLOAD_LARGE_ROW_SIZE =
      "Node Calendar data uploaded file has more than 1000 rows.";
  public static final String NODE_CALENDAR_DATA_UPLOAD_INVALID_FILE_HEADERS =
      "Node Calendar data uploaded file has invalid headers.";
}
