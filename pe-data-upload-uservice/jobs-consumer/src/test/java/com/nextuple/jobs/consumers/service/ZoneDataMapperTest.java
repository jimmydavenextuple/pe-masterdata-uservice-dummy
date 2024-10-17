/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.csvdownload.exception.CsvDataValidationException;
import com.nextuple.jobs.consumers.common.TestUtil;
import com.nextuple.jobs.consumers.exception.TransitMapperException;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.pojo.ZoneDataUpload;
import com.nextuple.transit.domain.feign.ZoneFeign;
import com.nextuple.transit.domain.outbound.ZoneResponse;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

@ExtendWith(MockitoExtension.class)
class ZoneDataMapperTest {

  @Mock private ZoneFeign zoneFeign;
  @InjectMocks private ZoneDataMapper zoneDataMapper;
  @InjectMocks private TestUtil testUtil;

  @Test
  void getModuleTest() {
    Assertions.assertEquals("zones", zoneDataMapper.getModule().getModuleValue());
  }

  @Test
  void getDTOFromCustomMapperTest() {
    Assertions.assertNull(zoneDataMapper.getDTOFromCustomMapper(""));
  }

  @Test
  void getColumnNameMappingTest() {
    String[] csvHeaders = {"header"};
    Map<String, String> map = zoneDataMapper.getColumnNameMapping(csvHeaders);
    Assertions.assertFalse(CollectionUtils.isEmpty(map));
  }

  @Test
  void mapTODto() {
    zoneDataMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_ZONES);
    Class res = zoneDataMapper.mapTODto();
    Assertions.assertEquals(ZoneDataUpload.class, res);
  }

  @Test
  void callApiTest() throws TransitMapperException {
    Object object = testUtil.getZoneDataUpload();
    zoneDataMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_ZONES);
    when(zoneFeign.addZoneData(any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getZoneResponse()).build());
    ResponseEntity<BaseResponse<ZoneResponse>> res =
        (ResponseEntity<BaseResponse<ZoneResponse>>) zoneDataMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
    Assertions.assertNotNull(res.getBody());
  }

  @Test
  void callApiForDeleteZoneTest() throws TransitMapperException {
    ZoneDataUpload zoneDataUpload = testUtil.getZoneDataUpload();
    zoneDataUpload.setZone("DELETE");
    zoneDataMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_ZONES);
    when(zoneFeign.deleteZoneDetails(any(), any(), any(), any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getZoneResponse()).build());
    ResponseEntity<BaseResponse<ZoneResponse>> res =
        (ResponseEntity<BaseResponse<ZoneResponse>>) zoneDataMapper.callApi(zoneDataUpload, null);
    Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
    Assertions.assertNotNull(res.getBody());
  }

  @Test
  void callApiForEmptyZoneExceptionTest() {
    ZoneDataUpload zoneDataUpload = testUtil.getZoneDataUpload();
    zoneDataUpload.setZone("");
    zoneDataMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_ZONES);

    Exception exception =
        Assertions.assertThrows(
            CsvDataValidationException.class, () -> zoneDataMapper.callApi(zoneDataUpload, null));
    Assertions.assertNotNull(exception);
    Assertions.assertEquals(
        "Consider giving NA as value for empty zone configuration.", exception.getMessage());
  }
}
