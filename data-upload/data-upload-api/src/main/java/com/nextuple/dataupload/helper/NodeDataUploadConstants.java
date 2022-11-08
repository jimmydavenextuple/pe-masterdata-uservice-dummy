package com.nextuple.dataupload.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NodeDataUploadConstants {
  public static final String NODE_DATA_UPLOAD_INVALID_FILE_TYPE =
      "Node data uploaded file is not a csv file.";
  public static final String NODE_DATA_UPLOAD_FILE_EMPTY_RECORDS =
      "Node data uploaded file has no records.";
  public static final String NODE_DATA_UPLOAD_LARGE_FILE_SIZE =
      "Node data uploaded file has size greater than 10240 kB.";
  public static final String NODE_DATA_UPLOAD_LARGE_ROW_SIZE =
      "Node data uploaded file has exceeded maximum file size limit.";
  public static final String NODE_DATA_UPLOAD_INVALID_FILE_HEADERS =
      "Node data uploaded file has invalid headers.";
  public static final String NODE_DATA_UPLOAD_SUCCESS = "Node Data successfully uploaded!";
  public static final String NODE_DATA_UPLOAD_FAILED = "Node Data upload failed!";
  public static final String NODE_DATA_UPLOAD_PARTIAL_SUCCESS =
      "Node Data partially uploaded with some rows failed!";
}
