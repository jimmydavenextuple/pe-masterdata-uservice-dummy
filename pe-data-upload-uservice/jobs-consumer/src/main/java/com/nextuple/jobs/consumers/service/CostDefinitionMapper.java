/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.csvdownload.exception.CsvDataValidationException;
import com.nextuple.jobs.consumers.domain.mapper.CostDefinitionDataUploadMapper;
import com.nextuple.jobs.consumers.exception.InvalidJobTypeException;
import com.nextuple.jobs.consumers.exception.NodeCarrierMapperException;
import com.nextuple.jobs.consumers.exception.TransitMapperException;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.pojo.CostValueDataUpload;
import com.nextuple.jobs.framework.common.domain.pojo.RecordInputDto;
import com.nextuple.jobs.framework.common.enums.ModuleEnum;
import com.nextuple.sourcing.cost.config.feign.CostValueFeign;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class CostDefinitionMapper implements FeignClientMapper {

  private static final String DELETE_D = "DELETE";
  private static final String NA = "NA";
  private final CostValueFeign costValueFeign;

  private JobTypeEnum jobType;

  public static final CostDefinitionDataUploadMapper INSTANCE =
      Mappers.getMapper(CostDefinitionDataUploadMapper.class);

  @Override
  public ModuleEnum getModule() {
    return ModuleEnum.COST_DEFINITION;
  }

  @Override
  public void setJobType(JobTypeEnum jobType) {
    this.jobType = jobType;
  }

  @Override
  public Object getDTOFromCustomMapper(String stringRecord) {
    return null;
  }

  @Override
  public Map<String, String> getColumnNameMapping(String[] headerColumns) {
    return getDefaultColumnNameMapping(headerColumns);
  }

  @Override
  public Class mapTODto()
      throws TransitMapperException, NodeCarrierMapperException, InvalidJobTypeException {
    return CostValueDataUpload.class;
  }

  @Override
  public ResponseEntity<?> callApi(Object request, RecordInputDto inputs) {
    var costValueUploadRequest = ((CostValueDataUpload) request);
    var costValueString = costValueUploadRequest.getCostValue();
    validateActionAndCostValue(costValueUploadRequest);

    if (DELETE_D.equalsIgnoreCase(costValueString)) {
      return ResponseEntity.ok(
          costValueFeign.deleteCostValueCostFactorCombinationKey(
              costValueUploadRequest.getOrgId(),
              costValueUploadRequest.getCostItinerary(),
              costValueUploadRequest.getCostFactorCombinationKey()));
    } else if (NA.equalsIgnoreCase(costValueString)) {
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("We are skipping this upload because of cost value NA")
              .build());
    } else {
      return ResponseEntity.ok(
          costValueFeign.createCostValue(
              costValueUploadRequest.getOrgId(),
              INSTANCE.convertToCreateCostValueRequest(costValueUploadRequest)));
    }
  }

  private void validateActionAndCostValue(CostValueDataUpload costValueUpload) {
    var costValueString = costValueUpload.getCostValue();
    if (!ObjectUtils.isEmpty(costValueString)
        && !NumberUtils.isCreatable(costValueString)
        && !DELETE_D.equalsIgnoreCase(costValueString)
        && !NA.equalsIgnoreCase(costValueString)) {
      log.error(
          "Invalid action or cost value: {} for cost factor combination key: {}",
          costValueString,
          costValueUpload.getCostFactorCombinationKey());
      throw new CsvDataValidationException(
          "Invalid action or cost value: %s".formatted(costValueString));
    }
  }
}
