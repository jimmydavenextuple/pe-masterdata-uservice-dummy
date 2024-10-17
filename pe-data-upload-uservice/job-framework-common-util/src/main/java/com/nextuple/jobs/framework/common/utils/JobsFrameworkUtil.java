/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.framework.common.utils;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import java.util.Map;
import java.util.Objects;
import org.springframework.http.HttpStatus;

public class JobsFrameworkUtil {
  private JobsFrameworkUtil() {}

  public static void validateFileName(String fileName) throws CommonServiceException {
    if (Objects.isNull(fileName) || !fileName.endsWith(".csv")) {
      throw new CommonServiceException(
          "Invalid file uploaded, upload a csv file",
          HttpStatus.BAD_REQUEST,
          0x1778,
          Map.of("fileName", FieldError.builder().rejectedValue(fileName).build()));
    }
  }
}
