/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.utils;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.http.HttpStatus;

public class SourcingCostConfigUtil {

  SourcingCostConfigUtil() {}

  private static final Logger logger = LoggerFactory.getLogger(SourcingCostConfigUtil.class);

  public static final String REGEX_FOR_FORMULA_VALIDATION = "[A-Za-z]+[.]*[0-9A-Za-z]*";

  public static final Pattern REGEX_PATTERN_FOR_FORMULA_VALIDATION =
      Pattern.compile(REGEX_FOR_FORMULA_VALIDATION, Pattern.MULTILINE);

  private static final Set<Class<?>> commonTypes =
      new HashSet<>(
          Arrays.asList(
              Integer.class,
              String.class,
              Long.class,
              Double.class,
              int.class,
              long.class,
              double.class));

  private static final String FORMULA = "formula";
  private static final String NAME = "name";
  private static final String REGEX_FOR_REGULAR_COST_FACTOR_FORMULA = "^[a-zA-Z0-9]+$";
  private static final Pattern REGULAR_COST_FACTOR_FORMULA_PATTERN =
      Pattern.compile(REGEX_FOR_REGULAR_COST_FACTOR_FORMULA);
  private static final Pattern COST_ATTRIBUTE_NAME_PATTERN =
      Pattern.compile(REGEX_FOR_REGULAR_COST_FACTOR_FORMULA);

  public static void validateFormulaForRegularCostFactor(String formula)
      throws CommonServiceException {

    Matcher matcher = REGULAR_COST_FACTOR_FORMULA_PATTERN.matcher(formula);
    if (!matcher.matches()) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(FORMULA, FieldError.builder().rejectedValue(formula).build());
      throw new CommonServiceException(
          "Invalid format! Only alphanumeric characters allowed while configuring formula for regular cost factor",
          HttpStatus.BAD_REQUEST,
          0x1771,
          errorMap);
    }
  }

  public static void validateCostAttributeName(String name) throws CommonServiceException {

    Matcher matcher = COST_ATTRIBUTE_NAME_PATTERN.matcher(name);
    if (!matcher.matches()) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(NAME, FieldError.builder().rejectedValue(name).build());
      throw new CommonServiceException(
          "Invalid format! Only alphanumeric characters allowed.",
          HttpStatus.BAD_REQUEST,
          0x1771,
          errorMap);
    }
  }

  public static boolean isPrimitive(Type typeToCheck) {
    return commonTypes.contains(typeToCheck);
  }
}
