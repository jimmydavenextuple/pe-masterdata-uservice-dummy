/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.service;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.csvdownload.service.v1.CostDefinitionRequestValidationFactory;
import com.nextuple.csvdownload.service.v1.ICostDefinitionRequestValidation;
import com.nextuple.sourcing.cost.config.feign.CostConfigDashboardFeign;
import com.nextuple.sourcing.cost.config.feign.CostValueFeign;
import com.nextuple.sourcing.cost.config.inbound.CostDefinitionRequest;
import com.nextuple.sourcing.cost.config.outbound.CostDefinitionResponse;
import com.nextuple.sourcing.cost.config.outbound.CostTypeValidationResponse;
import feign.FeignException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class CostDefinitionService {
  private static final String COST_TYPE = "costType";
  private static final String ORG_ID = "orgId";
  private final CostConfigDashboardFeign costConfigDashboardFeign;
  private final CostValueFeign costValueFeign;
  private final CostDefinitionRequestValidationFactory costDefinitionRequestValidationFactory;

  public CostDefinitionResponse getCostDefinitionResponse(
      String orgId,
      CostDefinitionRequest request,
      CostTypeValidationResponse costTypeValidationResponse)
      throws CommonServiceException {
    try {
      ICostDefinitionRequestValidation costDefinitionRequestValidation =
          costDefinitionRequestValidationFactory.getCostDefinitionRequestValidationFactory(request);
      costDefinitionRequestValidation.validateCostDefinitionRequest(
          request, costTypeValidationResponse);
      BaseResponse<CostDefinitionResponse> response =
          costConfigDashboardFeign.getRateCardTableData(orgId, request);
      if (ObjectUtils.isEmpty(response) || ObjectUtils.isEmpty(response.getPayload())) {
        log.error("Cost definition data not found for orgId {}", orgId);
        throwCommonServiceException(
            "Cost definition data not found for orgId",
            HttpStatus.NOT_FOUND,
            0x1776,
            ORG_ID,
            orgId);
      }
      return response.getPayload();
    } catch (FeignException ex) {
      log.error("Exception while getting rate card data for orgId {}", orgId);
      throw new CommonServiceException(
          "Exception while getting rate card data for orgId " + orgId,
          HttpStatus.INTERNAL_SERVER_ERROR,
          0x1775,
          Map.of(ORG_ID, FieldError.builder().rejectedValue(orgId).build()));
    }
  }

  public CostTypeValidationResponse getCostTypeValidationResponse(String orgId, String costType)
      throws CommonServiceException {
    try {
      BaseResponse<CostTypeValidationResponse> validationResponse =
          costConfigDashboardFeign.getCostTypesForValidation(orgId, costType);
      return validationResponse.getPayload();
    } catch (FeignException ex) {
      log.error("Invalid cost type for orgId " + orgId);
      Map<String, FieldError> errorMap =
          Map.of(COST_TYPE, FieldError.builder().rejectedValue(costType).build());
      throw new CommonServiceException(
          "Invalid cost type for orgId " + orgId, HttpStatus.BAD_REQUEST, 0x1771, errorMap);
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
