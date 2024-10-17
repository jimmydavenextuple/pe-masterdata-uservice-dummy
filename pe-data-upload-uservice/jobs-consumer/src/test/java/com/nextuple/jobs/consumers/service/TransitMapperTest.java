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
import com.nextuple.csvdownload.common.pojo.TransitDataUpload;
import com.nextuple.csvdownload.exception.CsvDataValidationException;
import com.nextuple.jobs.consumers.common.TestUtil;
import com.nextuple.jobs.consumers.exception.TransitMapperException;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.pojo.TransitBufferUpload;
import com.nextuple.transit.domain.feign.ITransitBufferFeign;
import com.nextuple.transit.domain.feign.TransitFeign;
import com.nextuple.transit.domain.outbound.TransitResponse;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.CollectionUtils;

@ExtendWith(MockitoExtension.class)
class TransitMapperTest {

  @Mock private TransitFeign transitFeign;
  @Mock private ITransitBufferFeign<?, ?> transitBufferFeign;
  @InjectMocks private TransitMapper transitMapper;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setup() {
    ReflectionTestUtils.setField(transitMapper, "transitBufferFeign", transitBufferFeign);
  }

  @Test
  void getColumnNameMapping() {
    String[] csvHeaders = {"header"};
    Map<String, String> map = transitMapper.getColumnNameMapping(csvHeaders);
    Assertions.assertFalse(CollectionUtils.isEmpty(map));
  }

  @Test
  void getDTOFromCustomMapper() {
    Assertions.assertNull(transitMapper.getDTOFromCustomMapper(""));
  }

  @Test
  void mapToDtoForUploadTransitTimes() throws TransitMapperException {
    transitMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_TRANSIT_TIMES);
    Class res = transitMapper.mapTODto();
    Assertions.assertEquals(TransitDataUpload.class, res);
  }

  @Test
  void mapToDtoForTransitBufferRequest() throws TransitMapperException {
    transitMapper.setJobTypeEnum(JobTypeEnum.TRANSIT_BUFFER_REQUEST);
    Class res = transitMapper.mapTODto();
    Assertions.assertEquals(TransitBufferUpload.class, res);
  }

  @Test
  void mapToDtoForDeleteTransitBuffer() throws TransitMapperException {
    transitMapper.setJobTypeEnum(JobTypeEnum.DELETE_TRANSIT_BUFFER);
    Class res = transitMapper.mapTODto();
    Assertions.assertEquals(TransitDataUpload.class, res);
  }

  @Test
  void mapToDtoInvalidJobTypeException() {
    transitMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES);
    Exception exception =
        Assertions.assertThrows(TransitMapperException.class, () -> transitMapper.mapTODto());
    Assertions.assertNotNull(exception);
  }

  @Test
  void callApi() throws TransitMapperException {
    Object object = testUtil.getTransitDataUpload();
    transitMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_TRANSIT_TIMES);
    when(transitFeign.addTransitData(any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getTransitResponse()).build());
    ResponseEntity<BaseResponse<TransitResponse>> res =
        (ResponseEntity<BaseResponse<TransitResponse>>) transitMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
    Assertions.assertNotNull(res.getBody());
  }

  @Test
  void callApiForDeleteTransitBuffer() throws TransitMapperException {
    Object object = testUtil.getTransitDataUpload();
    transitMapper.setJobTypeEnum(JobTypeEnum.DELETE_TRANSIT_BUFFER);
    when(transitFeign.updateTransitBufferDays(any(), any(), any(), any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getTransitResponse()).build());
    ResponseEntity<BaseResponse<TransitResponse>> res =
        (ResponseEntity<BaseResponse<TransitResponse>>) transitMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
    Assertions.assertNotNull(res.getBody());
  }

  @Test
  void callApiDeleteTransitEntryTest() throws TransitMapperException {
    TransitDataUpload transitDataUpload = testUtil.getTransitDataUpload();
    transitDataUpload.setTransitDays("DELETE");
    transitMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_TRANSIT_TIMES);
    when(transitFeign.deleteTransitDetails(any(), any(), any(), any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getTransitResponse()).build());
    ResponseEntity<BaseResponse<TransitResponse>> res =
        (ResponseEntity<BaseResponse<TransitResponse>>)
            transitMapper.callApi(transitDataUpload, null);
    Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
    Assertions.assertNotNull(res.getBody());
  }

  @Test
  void callApiInvalidTransitEntryTest() {
    var transitDataUpload = testUtil.getTransitDataUpload();
    transitDataUpload.setTransitDays("invalid");
    transitMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_TRANSIT_TIMES);
    Exception exception =
        Assertions.assertThrows(
            CsvDataValidationException.class, () -> transitMapper.callApi(transitDataUpload, null));

    Assertions.assertNotNull(exception);
  }

  @Test
  void callApiException() {
    TransitDataUpload transitDataUpload = testUtil.getTransitDataUpload();
    transitDataUpload.setTransitDays("");

    transitMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES);
    Exception exception =
        Assertions.assertThrows(
            TransitMapperException.class, () -> transitMapper.callApi(transitDataUpload, null));
    Assertions.assertNotNull(exception);
  }

  @Test
  void callApiForCreateTransitBuffer() throws TransitMapperException {
    Object object = testUtil.getTransitBufferUpload("2", "CREATE");
    transitMapper.setJobTypeEnum(JobTypeEnum.TRANSIT_BUFFER_REQUEST);
    when(transitBufferFeign.createTransitBuffer(any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getTransitBufferResponse()).build());
    ResponseEntity<BaseResponse<?>> res =
        (ResponseEntity<BaseResponse<?>>) transitMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
    Assertions.assertNotNull(res.getBody());
  }

  @Test
  void callApiForUpdateTransitBuffer() throws TransitMapperException {
    Object object = testUtil.getTransitBufferUpload("2", "UPDATE");
    transitMapper.setJobTypeEnum(JobTypeEnum.TRANSIT_BUFFER_REQUEST);
    when(transitBufferFeign.updateTransitBuffer(any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getTransitBufferResponse()).build());
    ResponseEntity<BaseResponse<?>> res =
        (ResponseEntity<BaseResponse<?>>) transitMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
    Assertions.assertNotNull(res.getBody());
  }

  @Test
  void callApiForDeleteTransitBufferUpload() throws TransitMapperException {
    Object object = testUtil.getTransitBufferUpload("2", "DELETE");
    transitMapper.setJobTypeEnum(JobTypeEnum.TRANSIT_BUFFER_REQUEST);
    when(transitBufferFeign.deleteTransitBufferDetails(any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getTransitBufferResponse()).build());
    ResponseEntity<BaseResponse<?>> res =
        (ResponseEntity<BaseResponse<?>>) transitMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
    Assertions.assertNotNull(res.getBody());
  }

  @Test
  void callApiForTransitBufferUploadInvalidAction() {
    Object object = testUtil.getTransitBufferUpload("2", "Z");
    transitMapper.setJobTypeEnum(JobTypeEnum.TRANSIT_BUFFER_REQUEST);

    Exception exception =
        Assertions.assertThrows(
            CsvDataValidationException.class, () -> transitMapper.callApi(object, null));
    Assertions.assertNotNull(exception);
  }
}
