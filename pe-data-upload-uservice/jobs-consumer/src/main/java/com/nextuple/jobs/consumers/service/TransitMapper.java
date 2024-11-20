/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.service;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.util.DateUtil;
import com.nextuple.csvdownload.common.pojo.TransitDataUpload;
import com.nextuple.csvdownload.exception.CsvDataValidationException;
import com.nextuple.jobs.consumers.domain.mapper.TransitDataUploadMapper;
import com.nextuple.jobs.consumers.exception.TransitMapperException;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.pojo.RecordInputDto;
import com.nextuple.jobs.framework.common.domain.pojo.TransitBufferUpload;
import com.nextuple.jobs.framework.common.enums.ModuleEnum;
import com.nextuple.transit.domain.feign.ITransitBufferFeign;
import com.nextuple.transit.domain.feign.TransitFeign;
import com.nextuple.transit.domain.inbound.TransitBufferRequest;
import com.nextuple.transit.domain.outbound.TransitResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransitMapper implements FeignClientMapper {

  private final Logger logger = LoggerFactory.getLogger(TransitMapper.class);

  private static final String DELETE_D = "DELETE";
  private static final String CREATE_C = "CREATE";
  private static final String UPDATE_U = "UPDATE";
  private final TransitFeign transitFeign;
  @Autowired private ITransitBufferFeign<?, TransitBufferRequest> transitBufferFeign;

  @Setter private JobTypeEnum jobTypeEnum;

  public static final TransitDataUploadMapper INSTANCE =
      Mappers.getMapper(TransitDataUploadMapper.class);

  @Override
  public ModuleEnum getModule() {
    return ModuleEnum.TRANSIT;
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
    return getDefaultColumnNameMapping(headerColumns);
  }

  @Override
  public Class mapTODto() throws TransitMapperException {
    try {
      if (jobTypeEnum == JobTypeEnum.UPLOAD_TRANSIT_TIMES
          || jobTypeEnum == JobTypeEnum.DELETE_TRANSIT_BUFFER) {
        return TransitDataUpload.class;
      } else if (jobTypeEnum == JobTypeEnum.TRANSIT_BUFFER_REQUEST) {
        return TransitBufferUpload.class;
      }
      logger.error("Unable to map an object!");
      throw new TransitMapperException(
          "Error while mapping an object to the expected object", jobTypeEnum);
    } catch (Exception e) {
      logger.error("Error while mapping to DTO");
      throw new TransitMapperException(
          "Exception while mapping an object to expected object", e, jobTypeEnum);
    }
  }

  @Override
  public ResponseEntity<?> callApi(Object request, RecordInputDto inputs)
      throws TransitMapperException {
    if (jobTypeEnum == JobTypeEnum.UPLOAD_TRANSIT_TIMES) {
      return invokeApiForTransitTimeUpload((TransitDataUpload) request);
    } else if (jobTypeEnum == JobTypeEnum.DELETE_TRANSIT_BUFFER) {
      return invokeApiForTransitBufferDelete((TransitDataUpload) request);
    } else if (jobTypeEnum == JobTypeEnum.TRANSIT_BUFFER_REQUEST) {
      return invokeApiForTransitBufferRequestUpload((TransitBufferUpload) request);
    }
    logger.error("Failed to make a call based on job type");
    throw new TransitMapperException("Please provide the valid job type", jobTypeEnum);
  }

  private ResponseEntity<BaseResponse<?>> invokeApiForTransitBufferRequestUpload(
      TransitBufferUpload transitBufferUpload) {

    var transitBufferRequest = INSTANCE.convertToTransitBufferRequest(transitBufferUpload);
    transitBufferRequest.setBufferStartDate(
        DateUtil.getDateUTC(transitBufferUpload.getBufferStartDate()));
    transitBufferRequest.setBufferEndDate(
        DateUtil.getDateUTC(transitBufferUpload.getBufferEndDate()));
    if (CREATE_C.equalsIgnoreCase(transitBufferUpload.getAction())) {
      return ResponseEntity.ok(transitBufferFeign.createTransitBuffer(transitBufferRequest));
    } else if (UPDATE_U.equalsIgnoreCase(transitBufferUpload.getAction())) {
      return ResponseEntity.ok(transitBufferFeign.updateTransitBuffer(transitBufferRequest));
    } else if (DELETE_D.equalsIgnoreCase(transitBufferUpload.getAction())) {
      return ResponseEntity.ok(transitBufferFeign.deleteTransitBufferDetails(transitBufferRequest));
    }
    logger.error("Invalid action type: {}", transitBufferUpload.getAction());
    throw new CsvDataValidationException(
        "Please provide the valid action for transit buffer request: "
            + transitBufferUpload.getAction());
  }

  private ResponseEntity<BaseResponse<TransitResponse>> invokeApiForTransitBufferDelete(
      TransitDataUpload request) {
    return ResponseEntity.ok(
        transitFeign.updateTransitBufferDays(
            request.getOrgId(),
            request.getCarrierServiceId(),
            request.getSourceGeozone(),
            request.getDestinationGeozone()));
  }

  private ResponseEntity<BaseResponse<TransitResponse>> invokeApiForTransitTimeUpload(
      TransitDataUpload request) {
    var transitDaysString = request.getTransitDays();
    validateActionTypeAndTransitDays(request, transitDaysString);
    if (DELETE_D.equalsIgnoreCase(transitDaysString)) {
      return ResponseEntity.ok(
          transitFeign.deleteTransitDetails(
              request.getOrgId(),
              request.getSourceGeozone(),
              request.getDestinationGeozone(),
              request.getCarrierServiceId()));
    } else {
      return ResponseEntity.ok(
          transitFeign.addTransitData(INSTANCE.convertToTransitDataRequest(request)));
    }
  }

  private void validateActionTypeAndTransitDays(
      TransitDataUpload transitDataUpload, String transitDaysString) {
    if (!ObjectUtils.isEmpty(transitDaysString)
        && !NumberUtils.isCreatable(transitDaysString)
        && !DELETE_D.equalsIgnoreCase(transitDaysString)) {
      logger.error(
          "Invalid action or transit days: {} for destinationFsa: {} and sourceFsa: {}",
          transitDaysString,
          transitDataUpload.getDestinationGeozone(),
          transitDataUpload.getSourceGeozone());
      throw new CsvDataValidationException("Invalid action or transit days: " + transitDaysString);
    }

    if (!NumberUtils.isCreatable(transitDaysString)) {
      logger.error("");
    }
  }
}
