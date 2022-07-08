package com.hbc.dataupload.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PromiseSourcingRuleDataUploadConstants {
  public static final String PROMISE_SOURCING_RULE_DATA_UPLOAD_INVALID_FILE_TYPE =
      "Promise Sourcing Rule data uploaded file is not a csv file.";
  public static final String PROMISE_SOURCING_RULE_DATA_UPLOAD_FILE_EMPTY_RECORDS =
      "Promise Sourcing Rule data uploaded file has no records.";
  public static final String PROMISE_SOURCING_RULE_DATA_UPLOAD_LARGE_FILE_SIZE =
      "Promise Sourcing Rule data uploaded file has size greater than 10240 kB.";
  public static final String PROMISE_SOURCING_RULE_DATA_UPLOAD_LARGE_ROW_SIZE =
      "Promise Sourcing Rule data uploaded file has more than 1000 rows.";
  public static final String PROMISE_SOURCING_RULE_DATA_UPLOAD_INVALID_FILE_HEADERS =
      "Promise Sourcing Rule data uploaded file has invalid headers.";
  public static final String PROMISE_SOURCING_RULE_DATA_UPLOAD_SUCCESS =
      "Promise Sourcing Rule Data successfully uploaded!";
  public static final String PROMISE_SOURCING_RULE_DATA_UPLOAD_FAILED =
      "Promise Sourcing Rule Data upload failed!";
  public static final String PROMISE_SOURCING_RULE_DATA_UPLOAD_PARTIAL_SUCCESS =
      "Promise Sourcing Rule Data partially uploaded with some rows failed!";
}
