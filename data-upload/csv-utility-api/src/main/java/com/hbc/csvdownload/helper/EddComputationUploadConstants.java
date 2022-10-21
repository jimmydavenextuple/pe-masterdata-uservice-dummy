package com.hbc.csvdownload.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EddComputationUploadConstants {

  public static final String EDD_COMPUTATION_DATA_UPLOAD_INVALID_FILE_TYPE =
      "Edd Computation data uploaded file is not a csv file.";
  public static final String EDD_COMPUTATION_DATA_UPLOAD_FILE_EMPTY_RECORDS =
      "Edd Computation data uploaded file has no records.";
  public static final String EDD_COMPUTATION_DATA_UPLOAD_LARGE_FILE_SIZE =
      "Edd Computation data uploaded file has size greater than 10240 kB.";
  public static final String EDD_COMPUTATION_DATA_UPLOAD_LARGE_ROW_SIZE =
      "Edd Computation data uploaded file has exceeded maximum file size limit.";
  public static final String EDD_COMPUTATION_DATA_UPLOAD_INVALID_FILE_HEADERS =
      "Edd Computation data uploaded file has invalid headers.";
}
