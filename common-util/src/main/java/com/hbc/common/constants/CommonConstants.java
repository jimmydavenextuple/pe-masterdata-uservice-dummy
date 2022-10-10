package com.hbc.common.constants;

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
  public static final String DELETE_D = "D";
  public static final String UPDATE_U = "U";
  public static final Set<String> actionTypes = Set.of(DELETE_D, UPDATE_U);
  public static final String HEADER_USER = "X-User";
  public static final String AUTHORIZATION_HEADER = "Authorization";
  public static final String DEFAULT_SORT_ORDER = "ASC";
  public static final String DESC_SORT_ORDER = "DESC";
  public static final String CARRIER_DEFAULT_SORT_BY = "carrierId";
  public static final String CALENDAR_DEFAULT_SORT_BY = "calendarId";
  public static final String NODE_DEFAULT_SORT_BY = "nodeId";
}
