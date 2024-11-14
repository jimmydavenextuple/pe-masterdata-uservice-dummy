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
import com.nextuple.jobs.framework.common.domain.pojo.PostalCodeTimezoneUpload;
import com.nextuple.jobs.framework.common.enums.ModuleEnum;
import com.nextuple.postal.code.timezone.api.domain.feign.PostalCodeFeign;
import com.nextuple.postal.code.timezone.api.domain.outbound.PostalCodeResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class PostalCodeTimeZoneMapperTest {

  @Mock private PostalCodeFeign postalCodeFeign;
  @InjectMocks private PostalCodeTimeZoneMapper postalCodeTimeZoneMapper;
  @InjectMocks private TestUtil testUtil;

  @Test
  void mapTODto() {
    Class marketRegionMapper1 = postalCodeTimeZoneMapper.mapTODto();
    Assertions.assertEquals(PostalCodeTimezoneUpload.class, marketRegionMapper1);
  }

  @Test
  void getModule() {
    ModuleEnum moduleEnum = postalCodeTimeZoneMapper.getModule();
    Assertions.assertEquals(ModuleEnum.POSTAL_CODE_TIMEZONE, moduleEnum);
  }

  @Test
  void setJobType() {
    Assertions.assertDoesNotThrow(
        () -> postalCodeTimeZoneMapper.setJobType(JobTypeEnum.UPLOAD_POSTAL_CODE_TIMEZONE));
  }

  @Test
  void getDTOFromCustomMapper() {
    Assertions.assertNull(postalCodeTimeZoneMapper.getDTOFromCustomMapper("request"));
  }

  @Test
  void getColumnNameMapping() {
    Assertions.assertNotNull(
        postalCodeTimeZoneMapper.getColumnNameMapping(new String[] {"request"}));
  }

  @Test
  void callApiCreatePostalCodeTimezone() {
    Object object = testUtil.getPostalCodeTimezoneUpload("CREATE");
    when(postalCodeFeign.createPostalCode(any()))
        .thenReturn(
            BaseResponse.builder().payload(testUtil.getPostalCodeTimezoneResponse()).build());

    ResponseEntity<BaseResponse<PostalCodeResponse>> response =
        (ResponseEntity<BaseResponse<PostalCodeResponse>>)
            postalCodeTimeZoneMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
  }

  @Test
  void callApiUpdatePostalCodeTimezone() {
    Object object = testUtil.getPostalCodeTimezoneUpload("UPDATE");
    when(postalCodeFeign.updatePostalCode(any()))
        .thenReturn(
            BaseResponse.builder().payload(testUtil.getPostalCodeTimezoneResponse()).build());

    ResponseEntity<BaseResponse<PostalCodeResponse>> response =
        (ResponseEntity<BaseResponse<PostalCodeResponse>>)
            postalCodeTimeZoneMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
  }

  @Test
  void callApiDeletePostalCodeTimezone() {
    Object object = testUtil.getPostalCodeTimezoneUpload("DELETE");
    when(postalCodeFeign.deletePostalCode(any(), any()))
        .thenReturn(
            BaseResponse.builder().payload(testUtil.getPostalCodeTimezoneResponse()).build());

    ResponseEntity<BaseResponse<PostalCodeResponse>> response =
        (ResponseEntity<BaseResponse<PostalCodeResponse>>)
            postalCodeTimeZoneMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
  }

  @Test
  void callApiInvalidAction() {
    Object object = testUtil.getPostalCodeTimezoneUpload("DEL");

    Exception exception =
        Assertions.assertThrows(
            CsvDataValidationException.class, () -> postalCodeTimeZoneMapper.callApi(object, null));

    Assertions.assertNotNull(exception);
  }
}
