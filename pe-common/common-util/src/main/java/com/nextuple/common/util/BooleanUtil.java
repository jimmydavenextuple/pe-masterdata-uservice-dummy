package com.nextuple.common.util;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.HardExecutionFailureException;
import com.nextuple.common.exception.ServiceUnavailableException;
import com.nextuple.common.response.error.FieldError;
import feign.FeignException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

@Slf4j
public class BooleanUtil {
  private BooleanUtil() {}

  private static final String INVALID_BOOLEAN_MESSAGE = "Invalid boolean value";
  private static final String NULL_BOOLEAN_MESSAGE = "Boolean value is empty/null";

  public static void checkValidBooleanValue(String val, String field)
      throws CommonServiceException {
    if (!StringUtils.hasLength(val)) {
      log.error(field + ": " + NULL_BOOLEAN_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(field, FieldError.builder().rejectedValue(val).build());
      throw new CommonServiceException(
          field + ": " + NULL_BOOLEAN_MESSAGE, HttpStatus.BAD_REQUEST, 0x1971, errorMap);
    } else if (!"true".equalsIgnoreCase(val) && !"false".equalsIgnoreCase(val)) {
      log.error(field + ": " + INVALID_BOOLEAN_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(field, FieldError.builder().rejectedValue(val).build());
      throw new CommonServiceException(
          field + ": " + INVALID_BOOLEAN_MESSAGE, HttpStatus.BAD_REQUEST, 0x1972, errorMap);
    }
  }

  public static boolean isFeignConnectionException(FeignException e) {
    return e.getCause() instanceof SocketException;
  }

  public static boolean isServiceUnavailableException(Exception e) {
    return e.getCause() instanceof ServiceUnavailableException;
  }

  public static boolean isHardExecutionFailureException(Exception e) {
    return e.getCause() instanceof HardExecutionFailureException;
  }
}
