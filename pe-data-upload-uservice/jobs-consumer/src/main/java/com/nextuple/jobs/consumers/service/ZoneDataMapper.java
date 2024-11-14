/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.service;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.csvdownload.exception.CsvDataValidationException;
import com.nextuple.jobs.consumers.domain.mapper.ZoneDataUploadMapper;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.pojo.RecordInputDto;
import com.nextuple.jobs.framework.common.domain.pojo.ZoneDataUpload;
import com.nextuple.jobs.framework.common.enums.ModuleEnum;
import com.nextuple.transit.domain.feign.ZoneFeign;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class ZoneDataMapper implements FeignClientMapper {
  private final Logger logger = LoggerFactory.getLogger(ZoneDataMapper.class);

  private static final String DELETE_D = "DELETE";
  private final ZoneFeign zoneFeign;

  @Setter private JobTypeEnum jobTypeEnum;

  public static final ZoneDataUploadMapper INSTANCE = Mappers.getMapper(ZoneDataUploadMapper.class);

  @Override
  public ModuleEnum getModule() {
    return ModuleEnum.ZONES;
  }

  @Override
  public void setJobType(JobTypeEnum jobType) {
    this.jobTypeEnum = jobType;
  }

  @Override
  public Map<String, String> getColumnNameMapping(String[] headerColumns) {
    return getDefaultColumnNameMapping(headerColumns);
  }

  @Override
  public Object getDTOFromCustomMapper(String stringRecord) {
    return null;
  }

  @Override
  public Class mapTODto() {
    return ZoneDataUpload.class;
  }

  @Override
  public ResponseEntity<?> callApi(Object request, RecordInputDto inputs) {
    var zoneUploadRequest = ((ZoneDataUpload) request);
    validateZoneValue(zoneUploadRequest);
    if (DELETE_D.equalsIgnoreCase(zoneUploadRequest.getZone())) {
      return ResponseEntity.ok(
          zoneFeign.deleteZoneDetails(
              zoneUploadRequest.getOrgId(),
              zoneUploadRequest.getSourceGeozone(),
              zoneUploadRequest.getDestinationGeozone(),
              zoneUploadRequest.getCarrierServiceId()));
    } else {
      return ResponseEntity.ok(
          zoneFeign.addZoneData(INSTANCE.convertToZoneRequest(zoneUploadRequest)));
    }
  }

  private void validateZoneValue(ZoneDataUpload zoneDataUpload) {
    var zoneValue = zoneDataUpload.getZone();
    if (ObjectUtils.isEmpty(zoneValue)) {
      logger.error(
          "Zone upload can't have empty value for destinationGeozone: {} and sourceGeozone: {}",
          zoneDataUpload.getDestinationGeozone(),
          zoneDataUpload.getSourceGeozone());
      throw new CsvDataValidationException(
          "Consider giving NA as value for empty zone configuration.");
    }
  }
}
