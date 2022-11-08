package com.nextuple.dataupload.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UploadBufferDataConstants {
  public static final String NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_INVALID_FILE_TYPE =
      "Node Service Option Buffer data uploaded file is not a csv file.";
  public static final String NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_FILE_EMPTY_RECORDS =
      "Node Service Option Buffer data uploaded file has no records.";
  public static final String NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_LARGE_FILE_SIZE =
      "Node Service Option Buffer data uploaded file has exceeded maximum size limit.";
  public static final String NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_LARGE_ROW_SIZE =
      "Node Service Option Buffer data uploaded file has exceeded maximum row size limit.";
  public static final String NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_INVALID_FILE_HEADERS =
      "Node Service Option Buffer data uploaded file has invalid headers.";
  public static final String NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_INCORRECT_DATA =
      "Node Service Option Buffer data has incorrect buffer details";

  public static final String NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_PARTIAL_SUCCESS =
      "Node Service Option Buffer Data partially uploaded with some rows failed!";
  public static final String NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_FAILED =
      "Node ServiceOption Buffer Data upload failed!";

  public static final String TRANSIT_BUFFER_DATA_UPLOAD_INVALID_FILE_TYPE =
      "Transit Buffer data uploaded file is not a csv file.";
  public static final String TRANSIT_BUFFER_DATA_UPLOAD_FILE_EMPTY_RECORDS =
      "Transit Buffer data uploaded file has no records.";
  public static final String TRANSIT_BUFFER_DATA_UPLOAD_LARGE_FILE_SIZE =
      "Transit Buffer data uploaded file has exceeded maximum size limit.";
  public static final String TRANSIT_BUFFER_DATA_UPLOAD_LARGE_ROW_SIZE =
      "Transit Buffer data uploaded file has exceeded maximum row size limit.";
  public static final String TRANSIT_BUFFER_DATA_UPLOAD_INVALID_FILE_HEADERS =
      "Transit Buffer data uploaded file has invalid headers.";
  public static final String TRANSIT_BUFFER_DATA_UPLOAD_INCORRECT_DATA =
      "Transit Buffer data has incorrect buffer details";
}
