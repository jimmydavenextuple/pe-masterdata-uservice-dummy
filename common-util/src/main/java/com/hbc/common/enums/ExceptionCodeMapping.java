package com.hbc.common.enums;

import java.util.HashMap;
import java.util.Map;

public enum ExceptionCodeMapping {

  /** 500 Error */
  UNEXPECTED_ERROR("500"),
  ACCEPT("202"),
  SUCCESS("200"),

  /** 100 to 199 is DAO related codes */
  DAO_SAVE_FAILED("400.100"),
  DAO_DELETE_FAILED("400.101"),
  DAO_UPDATE_FAILED("400.102"),
  DAO_GET_LIST_FAILED("400.103"),
  DAO_FIND_FAILED("400.104"),
  DAO_RECORD_NOT_FOUND("400.105"),
  DAO_RECORD_EXISTS("400.106"),
  DAO_NOT_FOUND("404.107"),

  /** 200 to 299 is KAFKA related codes */
  KAFKA_PUBLISH_FAILED("400.200"),
  KAFKA_OMS_ORDER_MESSAGE_FAILED("400.201"),

  /** 300 to 399 is ELASTIC & QUEUE related codes */
  ELASTIC_SEARCH_PUBLISH_FAILED("400.300"),
  ELASTIC_SEARCH_DELETE_FAILED("400.301"),
  FAILED_TO_FETCH_FROM_ES("400.302"),
  MQ_PUBLISH_FAILED("400.303"),

  /** 400 to 499 is VALIDATION related codes */
  CONVERSION_FAILED("400.400"),
  HEADERS_NOT_CORRECT("400.401"),
  VALIDATION_FAILED("400.402"),
  CONTROLLER_INVALID_INPUT("400.403"),

  /** 500 to 599 is GENERAL related codes */
  BLOCKED_STORE("400.500"),
  SERVICE_INVALID_INPUT("400.501"),
  SERVICE_UNAUTHORIZED_ACTION("401.502"),
  SERVICE_FIND_FAILED("401.503"),
  SERVICE_SAVE_FAILED("401.504");

  private static Map<String, ExceptionCodeMapping> errorCodeMap = new HashMap<>();

  static {
    for (final ExceptionCodeMapping errorCode : ExceptionCodeMapping.values()) {
      ExceptionCodeMapping.errorCodeMap.put(errorCode.getErrorCode(), errorCode);
    }
  }

  private String errorCode;

  private ExceptionCodeMapping(final String errorCode) {
    this.errorCode = errorCode;
  }

  public static ExceptionCodeMapping getErrorCode(final String errorCode) {
    return ExceptionCodeMapping.errorCodeMap.get(errorCode);
  }

  public String getErrorCode() {
    return this.errorCode;
  }
}
