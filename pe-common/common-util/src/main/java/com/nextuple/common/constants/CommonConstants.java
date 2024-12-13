package com.nextuple.common.constants;

import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonConstants {

  public static final String OPERATION_TAG_NAME = "operation";
  public static final String DB_OPERATION_READ = "READ";
  public static final String DB_OPERATION_READ_ALL = "READ_ALL";
  public static final String DB_OPERATION_INSERT = "INSERT";
  public static final String DB_OPERATION_UPDATE = "UPDATE";
  public static final String DB_OPERATION_DELETE = "DELETE";
  public static final String NODE_ID = "nodeId";
  public static final String ORG_ID = "orgId";
  public static final String SERVICE_OPTION = "serviceOptions";
  public static final String PROCESSING_TIME = "processingTime (in hrs)";
  public static final String ACTION_TYPE = "action";
  public static final String DELETE_D = "DELETE";
  public static final String UPDATE_U = "UPDATE";
  public static final Set<String> actionTypes = Set.of(DELETE_D, UPDATE_U);
  public static final String HEADER_USER = "X-User";
  public static final String AUTHORIZATION_HEADER = "Authorization";
  public static final String DEFAULT_SORT_ORDER = "ASC";
  public static final String DESC_SORT_ORDER = "DESC";
  public static final String CARRIER_DEFAULT_SORT_BY = "carrierId";
  public static final String CALENDAR_DEFAULT_SORT_BY = "calendarId";
  public static final String NODE_DEFAULT_SORT_BY = "nodeId";
  public static final String CARRIER_SERVICE_ID = "carrierServiceId";
  public static final String SOURCE_GEOZONE = "sourceGeozone";
  public static final String DESTINATION_GEOZONE = "destinationGeozone";
  public static final String BUFFER_START_DATE = "bufferStartDate";
  public static final String BUFFER_END_DATE = "bufferEndDate";
  public static final String BUFFER_DAYS = "bufferDays";
  public static final String CREATE_BY = "createdBy";
  public static final String AUTH_EXPIRY_TIMESTAMP_HEADER = "AuthExpiryTimestamp";
  public static final String HEADER_TENANT_ID = "X-Tenant-Id";
  public static final String HEADER_API_KEY = "X-API-Key";
  public static final String HEADER_ROLE = "X-User-Role";
  public static final String HEADER_USER_LOCALE = "X-User-Locale";
  public static final String HEADER_EVENT_NAME = "eventName";
  public static final String HEADER_EVENT_TYPE = "eventType";
  public static final String HEADER_EVENT_DATE = "eventDate";
  public static final String TRANSIT_BUFFER_CONFIG_REQUEST_ID = "transitBufferConfigRequestId";
  public static final String CONFIG_KEY = "configKey";
  public static final String SERVER_UNAVAILABLE_ERROR_MESSAGE =
      "Unable to complete the request as a required service is currently down.";
  public static final String HARD_EXECUTION_ERROR_MESSAGE =
      "Unable to process the request due to internal server error.";
}
