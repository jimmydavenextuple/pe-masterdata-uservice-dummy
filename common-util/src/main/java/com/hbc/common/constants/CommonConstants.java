package com.hbc.common.constants;

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
  public static final String HEADER_USER = "X-User";
  public static final String AUTHORIZATION_HEADER = "Authorization";
}
