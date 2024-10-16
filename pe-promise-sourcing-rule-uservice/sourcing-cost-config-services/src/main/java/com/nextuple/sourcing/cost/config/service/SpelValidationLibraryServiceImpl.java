/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.service;

import static com.nextuple.sourcing.cost.config.utils.SourcingCostConfigUtil.REGEX_PATTERN_FOR_FORMULA_VALIDATION;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.sourcing.cost.config.domain.configuration.PostConstructValidationBean;
import com.nextuple.sourcing.cost.config.enums.ExpressionLibraryEnum;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpelValidationLibraryServiceImpl implements ValidationLibraryService {

  public static final String LIBRARY_NAME = "libraryName";
  public static final String FORMULA_EXPRESSION = "formulaExpression";
  public static final String EXCEPTION_SPEL_PARSING = "Exception while parsing formula via spel : ";
  private final PostConstructValidationBean postConstructValidationBean;

  @Override
  public Double validateFormula(
      String formulaExpression,
      Map<String, Object> attributeValueMap,
      Map<String, Boolean> attributeScopeMap)
      throws CommonServiceException {
    try {

      ExpressionParser parser = new SpelExpressionParser();
      StandardEvaluationContext context =
          postConstructValidationBean.getSpelStandardEvaluationContext();

      Map<String, Object> variableMap = new HashMap<>();

      for (Map.Entry<String, ?> entry : attributeScopeMap.entrySet()) {
        if (entry.getValue().equals(Boolean.TRUE)) {
          variableMap.put(
              entry.getKey(), Double.parseDouble(attributeValueMap.get(entry.getKey()).toString()));
        }
      }
      context.setVariables(variableMap);

      Expression expression = parser.parseExpression(updateFormulaExpression(formulaExpression));
      return Double.parseDouble(expression.getValue(context).toString());

    } catch (Exception e) {
      log.error(EXCEPTION_SPEL_PARSING + "{}", e.getMessage());
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          LIBRARY_NAME,
          FieldError.builder().rejectedValue(ExpressionLibraryEnum.SPEL.getLibraryName()).build());
      errorMap.put(
          FORMULA_EXPRESSION, FieldError.builder().rejectedValue(formulaExpression).build());
      throw new CommonServiceException(
          EXCEPTION_SPEL_PARSING + e.getMessage(), HttpStatus.BAD_REQUEST, 0x1771, errorMap);
    }
  }

  private String updateFormulaExpression(String formulaExpression) {

    final Pattern pattern = REGEX_PATTERN_FOR_FORMULA_VALIDATION;
    final Matcher matcher = pattern.matcher(formulaExpression);

    StringBuffer result = new StringBuffer();
    while (matcher.find()) {
      matcher.appendReplacement(result, "#" + matcher.group(0));
    }
    matcher.appendTail(result);

    return result.toString();
  }
}
