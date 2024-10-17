/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.csvdownload.common.TestUtil;
import com.nextuple.csvdownload.exception.CsvFormatValidationFailedException;
import com.nextuple.csvdownload.exception.CsvParsingException;
import com.nextuple.csvdownload.exception.JobSubmissionException;
import com.nextuple.csvdownload.service.CsvUploadUtilityService;
import com.nextuple.transit.domain.outbound.TransitBufferConfigResponse;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class CsvUploadUtilityControllerTest {

  @Mock private CsvUploadUtilityService csvUploadUtilityService;
  @InjectMocks private CsvUploadUtilityController csvUploadUtilityController;

  @InjectMocks private TestUtil testUtil;

  @Test
  void uploadTransitTimesCSV()
      throws CsvFormatValidationFailedException, IOException, JobSubmissionException, CsvException {
    MultipartFile csvFile = Mockito.mock(MultipartFile.class);
    when(csvUploadUtilityService.uploadTransitTimesCsv(any(), any()))
        .thenReturn("Job to upload Transit times received successfully");
    ResponseEntity<BaseResponse<String>> res =
        csvUploadUtilityController.uploadTransitTimes("BAY", csvFile);
    Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
    Assertions.assertNotNull(res);
    Assertions.assertNotNull(res.getBody());
    Assertions.assertFalse(ObjectUtils.isEmpty(res.getBody().getMessage()));
  }

  @Test
  void uploadLeadProcessingTimeCSV()
      throws CsvParsingException,
          CsvFormatValidationFailedException,
          JobSubmissionException,
          IOException,
          CsvException {
    MultipartFile csvFile = Mockito.mock(MultipartFile.class);
    when(csvUploadUtilityService.uploadProcessingLeadTimesCsv(any(), any()))
        .thenReturn("Job to bulk upload processing lead times is submitted successfully");
    ResponseEntity<BaseResponse<String>> res =
        csvUploadUtilityController.uploadProcessingLeadTimes("BAY", csvFile);
    Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
    Assertions.assertNotNull(res);
    Assertions.assertNotNull(res.getBody());
    Assertions.assertFalse(ObjectUtils.isEmpty(res.getBody().getMessage()));
  }

  @Test
  void uploadTransitBufferTest() throws CommonServiceException {
    when(csvUploadUtilityService.uploadTransitBufferData(any()))
        .thenReturn(testUtil.getTransitBufferConfigResponse());
    ResponseEntity<BaseResponse<TransitBufferConfigResponse>> res =
        csvUploadUtilityController.uploadTransitBuffer(testUtil.getTransitBufferConfigRequest());
    Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
    Assertions.assertNotNull(res);
    Assertions.assertNotNull(res.getBody());
    Assertions.assertFalse(ObjectUtils.isEmpty(res.getBody().getMessage()));
  }

  @Test
  void updateTransitBufferTest() throws CommonServiceException {
    when(csvUploadUtilityService.updatingTransitBufferData(any()))
        .thenReturn(testUtil.getTransitBufferConfigResponse());
    ResponseEntity<BaseResponse<TransitBufferConfigResponse>> res =
        csvUploadUtilityController.updateTransitBufferUpload(
            testUtil.getTransitBufferConfigRequest());
    Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
    Assertions.assertNotNull(res);
    Assertions.assertNotNull(res.getBody());
    Assertions.assertFalse(ObjectUtils.isEmpty(res.getBody().getMessage()));
  }

  @Test
  void deleteTransitBufferTest() throws CommonServiceException {
    doNothing().when(csvUploadUtilityService).deletingTransitBufferData(any(), any());
    ResponseEntity<BaseResponse<TransitBufferConfigResponse>> res =
        csvUploadUtilityController.deleteTransitBufferUpload(
            testUtil.getTransitBufferConfigResponse().getId(), TestUtil.CREATED_BY);
    Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
    Assertions.assertNotNull(res);
    Assertions.assertNotNull(res.getBody());
    Assertions.assertFalse(ObjectUtils.isEmpty(res.getBody().getMessage()));
  }
}
