package com.hbc.jobs.framework.common.utils;

import com.hbc.common.response.error.ErrorResponse;
import com.hbc.common.response.error.ErrorType;
import com.hbc.common.util.JsonUtil;
import feign.FeignException;
import org.springframework.util.ObjectUtils;

/** Exception related utility methods */
public class ExceptionUtils {

  public static final int UPSTREAM_ERROR_CODE = -23234523;

  private ExceptionUtils() {
    // Use static methods
  }

  public static ErrorResponse parseFeignException(FeignException e) {
    if (e == null) {
      return ErrorResponse.builder(ErrorType.ERROR, UPSTREAM_ERROR_CODE)
          .message("Upstream error")
          .build();
    }

    if (!ObjectUtils.isEmpty(e.contentUTF8())) {
      var errorResponse = JsonUtil.convertToObject(e.contentUTF8(), ErrorResponse.class);
      if (errorResponse == null) {
        errorResponse =
            ErrorResponse.builder(ErrorType.ERROR, UPSTREAM_ERROR_CODE)
                .message("Upstream error : " + e.contentUTF8())
                .build();
      }
      return errorResponse;
    } else {
      return ErrorResponse.builder(ErrorType.ERROR, UPSTREAM_ERROR_CODE)
          .message("Upstream error : " + e.getMessage())
          .build();
    }
  }
}
