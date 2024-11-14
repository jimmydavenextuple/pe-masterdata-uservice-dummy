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

import com.nextuple.carrier.domain.feign.CarrierFeign;
import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.csvdownload.exception.CsvDataValidationException;
import com.nextuple.jobs.consumers.domain.mapper.CarrierServiceRequestMapper;
import com.nextuple.jobs.consumers.exception.InvalidActionTypeException;
import com.nextuple.jobs.consumers.exception.NodeCarrierMapperException;
import com.nextuple.jobs.consumers.exception.TransitMapperException;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.pojo.CarrierServiceUpload;
import com.nextuple.jobs.framework.common.domain.pojo.RecordInputDto;
import com.nextuple.jobs.framework.common.enums.ModuleEnum;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.mapstruct.factory.Mappers;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarrierMapper implements FeignClientMapper {

  @Setter private JobTypeEnum jobType;

  private final CarrierFeign carrierFeign;

  public static final CarrierServiceRequestMapper INSTANCE =
      Mappers.getMapper(CarrierServiceRequestMapper.class);

  private final Logger logger = LoggerFactory.getLogger(CarrierMapper.class);

  @Override
  public ModuleEnum getModule() {
    return ModuleEnum.CARRIER_SERVICE;
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
    return new HashMap<>();
  }

  @Override
  public Class mapTODto() throws TransitMapperException, NodeCarrierMapperException {
    return CarrierServiceUpload.class;
  }

  @Override
  public ResponseEntity<?> callApi(Object request, RecordInputDto inputs)
      throws TransitMapperException,
          NodeCarrierMapperException,
          InvalidActionTypeException,
          CommonServiceException {
    var carrierServiceUpload = (CarrierServiceUpload) request;
    String action = carrierServiceUpload.getAction();
    switch (action) {
      case CREATE:
        return ResponseEntity.ok(
            carrierFeign.createCarrierService(
                INSTANCE.convertToCarrierServiceRequest(carrierServiceUpload)));
      case UPDATE:
        return ResponseEntity.ok(
            carrierFeign.updateCarrierServiceDetails(
                carrierServiceUpload.getCarrierId(),
                carrierServiceUpload.getCarrierServiceId(),
                carrierServiceUpload.getOrgId(),
                INSTANCE.convertToCarrierServiceUpdateRequest(carrierServiceUpload)));
      case DELETE:
        return ResponseEntity.ok(
            carrierFeign.deleteCarrierService(
                carrierServiceUpload.getCarrierId(),
                carrierServiceUpload.getCarrierServiceId(),
                carrierServiceUpload.getOrgId()));
      default:
        {
          logger.error("Invalid action type: {}", carrierServiceUpload.getAction());
          throw new CsvDataValidationException(
              "Please provide the valid action: " + carrierServiceUpload.getAction());
        }
    }
  }
}
