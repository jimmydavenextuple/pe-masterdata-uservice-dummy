/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.service;

import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.CREATE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.DELETE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.UPDATE;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.csvdownload.exception.CsvDataValidationException;
import com.nextuple.jobs.consumers.domain.mapper.CustomRegionRequestMapper;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.pojo.CustomRegionUpload;
import com.nextuple.jobs.framework.common.domain.pojo.RecordInputDto;
import com.nextuple.jobs.framework.common.enums.ModuleEnum;
import com.nextuple.postal.code.timezone.api.domain.feign.PostalCodeFeign;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.mapstruct.factory.Mappers;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomRegionMapper implements FeignClientMapper {

  private final Logger logger = LoggerFactory.getLogger(CustomRegionMapper.class);
  private final PostalCodeFeign postalCodeFeign;

  @Setter private JobTypeEnum jobTypeEnum;

  public static final CustomRegionRequestMapper INSTANCE =
      Mappers.getMapper(CustomRegionRequestMapper.class);

  @Override
  public ModuleEnum getModule() {
    return ModuleEnum.CUSTOM_REGION;
  }

  @Override
  public void setJobType(JobTypeEnum jobType) {
    this.jobTypeEnum = jobType;
  }

  @Override
  public Class mapTODto() {
    return CustomRegionUpload.class;
  }

  @Override
  public ResponseEntity<?> callApi(Object request, RecordInputDto inputs) {
    var customRegionUpload = (CustomRegionUpload) request;
    String action = customRegionUpload.getAction();
    switch (action) {
      case CREATE:
        return ResponseEntity.ok(
            postalCodeFeign.createCustomRegion(
                INSTANCE.convertToCustomRegionRequest(customRegionUpload)));
      case UPDATE:
        return ResponseEntity.ok(
            postalCodeFeign.updateCustomRegion(
                INSTANCE.convertToCustomRegionRequest(customRegionUpload)));
      case DELETE:
        return ResponseEntity.ok(
            postalCodeFeign.deleteCustomRegion(
                customRegionUpload.getOrgId(), customRegionUpload.getId()));
      default:
        throw getCsvDataValidationException(customRegionUpload);
    }
  }

  private static CsvDataValidationException getCsvDataValidationException(
      CustomRegionUpload customRegionUpload) {
    return new CsvDataValidationException(
        "Please provide the valid action: " + customRegionUpload.getAction());
  }

  @Override
  public Map<String, String> getColumnNameMapping(String[] headerColumns) {
    return new HashMap<>();
  }

  @Override
  public Object getDTOFromCustomMapper(String stringRecord) {
    return null;
  }
}
