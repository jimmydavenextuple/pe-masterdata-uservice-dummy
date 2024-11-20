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
import com.nextuple.jobs.consumers.domain.mapper.PostalCodeTimezoneRequestMapper;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.pojo.PostalCodeTimezoneUpload;
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
public class PostalCodeTimeZoneMapper implements FeignClientMapper {

  private final Logger logger = LoggerFactory.getLogger(PostalCodeTimeZoneMapper.class);
  private final PostalCodeFeign postalCodeFeign;

  @Setter private JobTypeEnum jobTypeEnum;

  public static final PostalCodeTimezoneRequestMapper INSTANCE =
      Mappers.getMapper(PostalCodeTimezoneRequestMapper.class);

  @Override
  public ModuleEnum getModule() {
    return ModuleEnum.POSTAL_CODE_TIMEZONE;
  }

  @Override
  public void setJobType(JobTypeEnum jobType) {
    this.jobTypeEnum = jobType;
  }

  @Override
  public Object getDTOFromCustomMapper(String stringRecord) {
    return null;
  }

  @Override
  public Map<String, String> getColumnNameMapping(String[] headerColumns) {
    return new HashMap<>();
  }

  @Override
  public Class mapTODto() {
    return PostalCodeTimezoneUpload.class;
  }

  @Override
  public ResponseEntity<?> callApi(Object request, RecordInputDto inputs) {
    var postalCodeTimezoneUpload = (PostalCodeTimezoneUpload) request;
    String action = postalCodeTimezoneUpload.getAction();
    switch (action) {
      case CREATE:
        return ResponseEntity.ok(
            postalCodeFeign.createPostalCode(
                INSTANCE.convertToPostalCodeRequest(postalCodeTimezoneUpload)));
      case UPDATE:
        return ResponseEntity.ok(
            postalCodeFeign.updatePostalCode(
                INSTANCE.convertToPostalCodeRequest(postalCodeTimezoneUpload)));
      case DELETE:
        return ResponseEntity.ok(
            postalCodeFeign.deletePostalCode(
                postalCodeTimezoneUpload.getOrgId(), postalCodeTimezoneUpload.getZipCode()));
      default:
        {
          logger.error("Invalid action type: {}", postalCodeTimezoneUpload.getAction());
          throw new CsvDataValidationException(
              "Please provide the valid action: " + postalCodeTimezoneUpload.getAction());
        }
    }
  }
}
