/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.nextuple.carrier.domain.feign.CarrierFeign;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.csvdownload.exception.CsvDataValidationException;
import com.nextuple.jobs.consumers.common.TestUtil;
import com.nextuple.jobs.consumers.exception.InvalidActionTypeException;
import com.nextuple.jobs.consumers.exception.NodeCarrierMapperException;
import com.nextuple.jobs.consumers.exception.TransitMapperException;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.pojo.CarrierServiceUpload;
import com.nextuple.jobs.framework.common.enums.ModuleEnum;
import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class CarrierMapperTest {

  @Mock private CarrierFeign carrierFeign;
  @InjectMocks private CarrierMapper carrierMapper;
  @InjectMocks private TestUtil testUtil;

  @Test
  void mapTODto() throws TransitMapperException, NodeCarrierMapperException {
    Class carrierServiceUpload = carrierMapper.mapTODto();
    Assertions.assertEquals(CarrierServiceUpload.class, carrierServiceUpload);
  }

  @Test
  void getModule() {
    ModuleEnum moduleEnum = carrierMapper.getModule();
    Assertions.assertEquals(ModuleEnum.CARRIER_SERVICE, moduleEnum);
  }

  @Test
  void setJobType() {
    Assertions.assertDoesNotThrow(() -> carrierMapper.setJobType(JobTypeEnum.UPLOAD_NODE_CALENDER));
  }

  @Test
  void getDTOFromCustomMapper() {
    Assertions.assertNull(carrierMapper.getDTOFromCustomMapper("request"));
  }

  @Test
  void getColumnNameMapping() {
    Assertions.assertNotNull(carrierMapper.getColumnNameMapping(new String[] {"request"}));
  }

  @Test
  void callApiCreateCarrierService()
      throws TransitMapperException,
          NodeCarrierMapperException,
          CommonServiceException,
          InvalidActionTypeException {
    Object object = testUtil.getCarrierServiceUpload("CREATE");
    when(carrierFeign.createCarrierService(any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getCarrierServiceResponse()).build());

    ResponseEntity<BaseResponse<PostalCodeTimezoneDto>> response =
        (ResponseEntity<BaseResponse<PostalCodeTimezoneDto>>) carrierMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
  }

  @Test
  void callApiUpdateCarrierServiceDetails()
      throws TransitMapperException,
          NodeCarrierMapperException,
          CommonServiceException,
          InvalidActionTypeException {
    Object object = testUtil.getCarrierServiceUpload("UPDATE");
    when(carrierFeign.updateCarrierServiceDetails(anyString(), anyString(), anyString(), any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getCarrierServiceResponse()).build());

    ResponseEntity<BaseResponse<PostalCodeTimezoneDto>> response =
        (ResponseEntity<BaseResponse<PostalCodeTimezoneDto>>) carrierMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
  }

  @Test
  void callApiDeleteCarrierService()
      throws TransitMapperException,
          NodeCarrierMapperException,
          CommonServiceException,
          InvalidActionTypeException {
    Object object = testUtil.getCarrierServiceUpload("DELETE");
    when(carrierFeign.deleteCarrierService(anyString(), anyString(), anyString()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getCarrierServiceResponse()).build());

    ResponseEntity<BaseResponse<PostalCodeTimezoneDto>> response =
        (ResponseEntity<BaseResponse<PostalCodeTimezoneDto>>) carrierMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
  }

  @Test
  void callApiInvalidAction() {
    Object object = testUtil.getCarrierServiceUpload("DEL");

    Exception exception =
        Assertions.assertThrows(
            CsvDataValidationException.class, () -> carrierMapper.callApi(object, null));

    Assertions.assertNotNull(exception);
  }
}
