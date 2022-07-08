package com.hbc.dataupload.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WeightageConfigurationDataUploadConstants {
  public static final String WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_INVALID_FILE_TYPE =
      "Weightage Configuration data uploaded file is not a csv file.";
  public static final String WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_FILE_EMPTY_RECORDS =
      "Weightage Configuration data uploaded file has no records.";
  public static final String WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_LARGE_FILE_SIZE =
      "Weightage Configuration data uploaded file has size greater than 10240 kB.";
  public static final String WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_LARGE_ROW_SIZE =
      "Weightage Configuration data uploaded file has more than 1000 rows.";
  public static final String WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_INVALID_FILE_HEADERS =
      "Weightage Configuration data uploaded file has invalid headers.";
  public static final String WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_SUCCESS =
      "Weightage Configuration Data successfully uploaded!";
  public static final String WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_FAILED =
      "Weightage Configuration Data upload failed!";
  public static final String WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_PARTIAL_SUCCESS =
      "Weightage Configuration Data partially uploaded with some rows failed!";
}
