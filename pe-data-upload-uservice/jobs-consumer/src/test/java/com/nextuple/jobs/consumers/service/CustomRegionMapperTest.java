/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.csvdownload.exception.CsvDataValidationException;
import com.nextuple.jobs.consumers.common.TestUtil;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.pojo.CustomRegionUpload;
import com.nextuple.jobs.framework.common.enums.ModuleEnum;
import com.nextuple.postal.code.timezone.api.domain.feign.PostalCodeFeign;
import com.nextuple.postal.code.timezone.api.domain.outbound.CustomRegionResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class CustomRegionMapperTest {

  @Mock private PostalCodeFeign postalCodeFeign;
  @InjectMocks private CustomRegionMapper customRegionMapper;
  @InjectMocks private TestUtil testUtil;

  @Test
  void mapTODto() {
    Class customRegionUpload = customRegionMapper.mapTODto();
    Assertions.assertEquals(CustomRegionUpload.class, customRegionUpload);
  }

  @Test
  void getModule() {
    ModuleEnum moduleEnum = customRegionMapper.getModule();
    Assertions.assertEquals(ModuleEnum.CUSTOM_REGION, moduleEnum);
  }

  @Test
  void setJobType() {
    Assertions.assertDoesNotThrow(
        () -> customRegionMapper.setJobType(JobTypeEnum.UPLOAD_CUSTOM_REGION));
  }

  @Test
  void getDTOFromCustomMapper() {
    Assertions.assertNull(customRegionMapper.getDTOFromCustomMapper("request"));
  }

  @Test
  void getColumnNameMapping() {
    Assertions.assertNotNull(customRegionMapper.getColumnNameMapping(new String[] {"request"}));
  }

  @Test
  void callApiCreatePostalCode() {
    Object object = testUtil.getCustomRegionUpload("CREATE");
    when(postalCodeFeign.createCustomRegion(any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getCustomRegionResponse()).build());

    ResponseEntity<BaseResponse<CustomRegionResponse>> response =
        (ResponseEntity<BaseResponse<CustomRegionResponse>>)
            customRegionMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
  }

  @Test
  void callApiUpdateCustomRegion() {
    Object object = testUtil.getCustomRegionUpload("UPDATE");
    when(postalCodeFeign.updateCustomRegion(any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getCustomRegionResponse()).build());

    ResponseEntity<BaseResponse<CustomRegionResponse>> response =
        (ResponseEntity<BaseResponse<CustomRegionResponse>>)
            customRegionMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
  }

  @Test
  void callApiDeleteCustomRegion() {
    Object object = testUtil.getCustomRegionUpload("DELETE");
    when(postalCodeFeign.deleteCustomRegion(any(), any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getCustomRegionResponse()).build());

    ResponseEntity<BaseResponse<CustomRegionResponse>> response =
        (ResponseEntity<BaseResponse<CustomRegionResponse>>)
            customRegionMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
  }

  @Test
  void callApiInvalidAction() {
    Object object = testUtil.getCustomRegionUpload("DEL");

    Exception exception =
        Assertions.assertThrows(
            CsvDataValidationException.class, () -> customRegionMapper.callApi(object, null));

    Assertions.assertNotNull(exception);
  }
}
