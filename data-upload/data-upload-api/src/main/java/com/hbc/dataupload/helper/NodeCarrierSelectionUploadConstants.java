package com.hbc.dataupload.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NodeCarrierSelectionUploadConstants {
  public static final String NODE_CARRIER_SELECTION_DATA_UPLOAD_INVALID_FILE_TYPE =
      "Node service selection data uploaded file is not a csv file.";
  public static final String NODE_CARRIER_SELECTION_DATA_UPLOAD_FILE_EMPTY_RECORDS =
      "Node service selection data uploaded file has no records.";
  public static final String NODE_CARRIER_SELECTION_DATA_UPLOAD_LARGE_FILE_SIZE =
      "Node service selection data uploaded file has size greater than 10240 kB.";
  public static final String NODE_CARRIER_SELECTION_DATA_UPLOAD_LARGE_ROW_SIZE =
      "Node service selection data uploaded file has exceeded maximum file size limit.";
  public static final String NODE_CARRIER_SELECTION_DATA_UPLOAD_INVALID_FILE_HEADERS =
      "Node service selection data uploaded file has invalid headers.";
  public static final String NODE_CARRIER_SELECTION_DATA_UPLOAD_SUCCESS =
      "Node Carrier Selection Data successfully uploaded!";
  public static final String NODE_CARRIER_SELECTION_DATA_UPLOAD_PARTIAL_SUCCESS =
      "Node Carrier Selection Data partially uploaded with some rows failed!";
  public static final String NODE_CARRIER_SELECTION_DATA_UPLOAD_FAILED =
      "Node Carrier Selection Data upload failed!";
}
