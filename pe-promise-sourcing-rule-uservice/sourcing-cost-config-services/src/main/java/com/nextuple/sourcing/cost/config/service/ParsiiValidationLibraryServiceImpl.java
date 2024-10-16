/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.service;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.sourcing.cost.config.enums.ExpressionLibraryEnum;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import parsii.eval.Expression;
import parsii.eval.Parser;
import parsii.eval.Scope;
import parsii.eval.Variable;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParsiiValidationLibraryServiceImpl implements ValidationLibraryService {

  public static final String LIBRARY_NAME = "libraryName";
  public static final String FORMULA_EXPRESSION = "formulaExpression";
  public static final String EXCEPTION_PARSII_PARSING =
      "Exception while parsing formula via parsii : ";

  @Override
  public Double validateFormula(
      String expression,
      Map<String, Object> attributeValueMap,
      Map<String, Boolean> attributeScopeMap)
      throws CommonServiceException {
    try {
      Scope scope = new Scope();
      scope = scope.withStrictLookup(Boolean.TRUE);
      for (Map.Entry<String, ?> entry : attributeScopeMap.entrySet()) {
        if (entry.getValue().equals(Boolean.TRUE)) {
          Variable variable = scope.create(entry.getKey());
          variable.setValue(Double.parseDouble(attributeValueMap.get(entry.getKey()).toString()));
        }
      }
      Expression expr = Parser.parse(expression, scope);
      return expr.evaluate();
    } catch (Exception e) {
      log.error(EXCEPTION_PARSII_PARSING + "{}", e.getMessage());
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          LIBRARY_NAME,
          FieldError.builder()
              .rejectedValue(ExpressionLibraryEnum.PARSII.getLibraryName())
              .build());
      errorMap.put(FORMULA_EXPRESSION, FieldError.builder().rejectedValue(expression).build());
      throw new CommonServiceException(
          EXCEPTION_PARSII_PARSING + e.getMessage(), HttpStatus.BAD_REQUEST, 0x1771, errorMap);
    }
  }
}
