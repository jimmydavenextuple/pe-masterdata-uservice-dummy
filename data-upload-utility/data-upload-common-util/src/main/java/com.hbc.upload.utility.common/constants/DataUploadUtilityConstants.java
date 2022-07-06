package com.hbc.upload.utility.common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DataUploadUtilityConstants {
  public static final String FILE_TYPE = "text/csv";
  public static final String CREATE = "CREATE";
  public static final String UPDATE = "UPDATE";
  public static final String DELETE = "DELETE";
  public static final String ACTION = "action";
  public static final String ORG_ID = "orgId";
  public static final String CARRIER_SERVICE_ID = "carrierServiceId";
  public static final String NODE_ID = "nodeId";
  public static final String SERVICE_OPTION = "serviceOption";
  public static final String PROCESSING_TIME = "processingTime";
  public static final String LAST_PICKUP_TIME = "lastPickupTime";
  public static final String FILE_URI = "fileUri";
}
