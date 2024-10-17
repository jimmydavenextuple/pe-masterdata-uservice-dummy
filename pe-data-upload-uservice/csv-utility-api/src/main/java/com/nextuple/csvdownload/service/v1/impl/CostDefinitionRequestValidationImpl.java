/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.service.v1.impl;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.csvdownload.service.v1.ICostDefinitionRequestValidation;
import com.nextuple.sourcing.cost.config.dto.FilterCostFactorInfoDto;
import com.nextuple.sourcing.cost.config.dto.costtyypesdto.CostFactorDescriptionDto;
import com.nextuple.sourcing.cost.config.inbound.CostDefinitionRequest;
import com.nextuple.sourcing.cost.config.outbound.CostTypeValidationResponse;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Slf4j
public class CostDefinitionRequestValidationImpl implements ICostDefinitionRequestValidation {
  private static final String ROW = "row";
  private static final String COLUMN = "column";

  @Override
  public void validateCostDefinitionRequest(
      CostDefinitionRequest request, CostTypeValidationResponse response)
      throws CommonServiceException {
    validateRowAndColumn(request.getRow(), response.getRow(), ROW);
    validateRowAndColumn(request.getColumn(), response.getColumn(), COLUMN);
    validateFilters(request, response);
  }

  @Override
  public Optional<String> getCostItinerary(
      CostDefinitionRequest request, CostTypeValidationResponse costTypeValidationResponse) {
    return Optional.of(costTypeValidationResponse.getCostItinerary());
  }

  private void validateFilters(
      CostDefinitionRequest request, CostTypeValidationResponse costTypeValidationResponse)
      throws CommonServiceException {
    if (!CollectionUtils.isEmpty(request.getFilters())) {
      boolean allFiltersValid =
          request.getFilters().stream()
              .allMatch(
                  requestFilter ->
                      validateRequestFilter(costTypeValidationResponse, requestFilter));

      if (!allFiltersValid) {
        throwCommonServiceException(
            "Invalid filter or filter values", HttpStatus.BAD_REQUEST, 0x1771, null, null);
      }
    }
  }

  private static boolean validateRequestFilter(
      CostTypeValidationResponse costTypeValidationResponse,
      FilterCostFactorInfoDto requestFilter) {
    return costTypeValidationResponse.getCostFactors().stream()
        .anyMatch(
            responseFilter ->
                responseFilter.getCostFactor().equals(requestFilter.getCostFactor())
                    && responseFilter.getValues().contains(requestFilter.getCostFactorValue()));
  }

  private void validateRowAndColumn(
      String requestFactor, CostFactorDescriptionDto expectedFactor, String factorName)
      throws CommonServiceException {
    if (StringUtils.hasLength(requestFactor)
        && (ObjectUtils.isEmpty(expectedFactor)
            || !requestFactor.equals(expectedFactor.getCostFactor()))) {
      throwCommonServiceException(
          "Invalid cost factor in " + factorName,
          HttpStatus.BAD_REQUEST,
          0x1771,
          factorName,
          requestFactor);
    }
  }

  private void throwCommonServiceException(
      String errorMessage, HttpStatus status, Integer errorCode, String field, String fieldValue)
      throws CommonServiceException {
    log.error(errorMessage);
    Map<String, FieldError> errorMap = null;
    if (StringUtils.hasLength(field))
      errorMap = Map.of(field, FieldError.builder().rejectedValue(fieldValue).build());
    throw new CommonServiceException(errorMessage, status, errorCode, errorMap);
  }
}
