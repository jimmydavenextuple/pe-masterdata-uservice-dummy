/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.framework.common.utils;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.common.response.error.ErrorType;
import com.nextuple.common.util.JsonUtil;
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
