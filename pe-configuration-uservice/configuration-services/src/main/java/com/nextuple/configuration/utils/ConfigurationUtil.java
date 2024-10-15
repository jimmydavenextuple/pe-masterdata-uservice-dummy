/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.configuration.utils;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.http.HttpStatus;

public class ConfigurationUtil {
  ConfigurationUtil() {}

  private static final Logger logger = LoggerFactory.getLogger(ConfigurationUtil.class);
  private static final String REGEX_FOR_CONFIG_KEY = "^[a-z]+(?:-[a-z][a-zA-Z]*){0,100}$";
  private static final Pattern CONFIG_KEY_PATTERN = Pattern.compile(REGEX_FOR_CONFIG_KEY);
  private static final String CONFIG_KEY = "configKey";

  public static void validateConfigKeyFormat(String configKey) throws CommonServiceException {
    logger.debug("-- inside validateConfigKeyFormat --");
    Matcher matcher = CONFIG_KEY_PATTERN.matcher(configKey);
    if (!matcher.matches()) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(CONFIG_KEY, FieldError.builder().rejectedValue(configKey).build());
      throw new CommonServiceException(
          "Invalid format! Only letters and hyphens are allowed in configKey",
          HttpStatus.BAD_REQUEST,
          0x1771,
          errorMap);
    }
  }
}
