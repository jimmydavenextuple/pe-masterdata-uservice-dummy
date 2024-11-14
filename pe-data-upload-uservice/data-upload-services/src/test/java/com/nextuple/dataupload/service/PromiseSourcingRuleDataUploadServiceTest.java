/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.service;

import static com.nextuple.dataupload.helper.PromiseSourcingRuleDataUploadConstants.PROMISE_SOURCING_RULE_DATA_UPLOAD_FAILED;
import static com.nextuple.dataupload.helper.PromiseSourcingRuleDataUploadConstants.PROMISE_SOURCING_RULE_DATA_UPLOAD_FILE_EMPTY_RECORDS;
import static com.nextuple.dataupload.helper.PromiseSourcingRuleDataUploadConstants.PROMISE_SOURCING_RULE_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.nextuple.dataupload.helper.PromiseSourcingRuleDataUploadConstants.PROMISE_SOURCING_RULE_DATA_UPLOAD_INVALID_FILE_TYPE;
import static com.nextuple.dataupload.helper.PromiseSourcingRuleDataUploadConstants.PROMISE_SOURCING_RULE_DATA_UPLOAD_LARGE_FILE_SIZE;
import static com.nextuple.dataupload.helper.PromiseSourcingRuleDataUploadConstants.PROMISE_SOURCING_RULE_DATA_UPLOAD_LARGE_ROW_SIZE;
import static com.nextuple.dataupload.helper.PromiseSourcingRuleDataUploadConstants.PROMISE_SOURCING_RULE_DATA_UPLOAD_PARTIAL_SUCCESS;
import static com.nextuple.dataupload.helper.PromiseSourcingRuleDataUploadConstants.PROMISE_SOURCING_RULE_DATA_UPLOAD_SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.util.TestUtil;
import com.nextuple.promise.sourcing.rule.api.domain.dto.PromiseSourcingRuleDto;
import com.nextuple.promise.sourcing.rule.api.domain.feign.PromiseSourcingRuleFeign;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.CreatePromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.UpdatePromiseSourcingRuleRequest;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PromiseSourcingRuleDataUploadServiceTest {
  @InjectMocks
  private PromiseSourcingRuleDataUploadService promiseSourcingRuleDataUploadUtilityService;

  @InjectMocks private TestUtil testUtil;

  @Mock private PromiseSourcingRuleFeign promiseSourcingRuleFeign;

  @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(promiseSourcingRuleDataUploadUtilityService, "basePath", "");
    ReflectionTestUtils.setField(
        promiseSourcingRuleDataUploadUtilityService, "maxSizeInKiloBytes", 10240);
    ReflectionTestUtils.setField(promiseSourcingRuleDataUploadUtilityService, "maxRows", 1000);
  }

  @Test
  void uploadPromiseSourcingRuleDataSuccessTest() throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get(
            "src", "test", "resources", "promiseSourcingRule", "promiseSourcingRule_happyPath.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    BaseResponse<PromiseSourcingRuleDto> baseResponse =
        testUtil.getSuccessfulBaseResponseForPromiseSourcingRule();
    when(promiseSourcingRuleFeign.createPromiseSourcingRule(
            any(CreatePromiseSourcingRuleRequest.class)))
        .thenReturn(baseResponse);
    when(promiseSourcingRuleFeign.updatePromiseSourcingRule(
            anyString(),
            anyString(),
            anyString(),
            anyString(),
            anyInt(),
            any(UpdatePromiseSourcingRuleRequest.class)))
        .thenReturn(baseResponse);
    when(promiseSourcingRuleFeign.deletePromiseSourcingRule(
            anyString(), anyString(), anyString(), anyString(), anyInt()))
        .thenReturn(baseResponse);
    ResponseEntity<BaseResponse<String>> response =
        promiseSourcingRuleDataUploadUtilityService.uploadPromiseSourcingRuleData(absolutePath);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(
        PROMISE_SOURCING_RULE_DATA_UPLOAD_SUCCESS,
        Objects.requireNonNull(response.getBody()).getMessage());
  }

  @Test
  void uploadPromiseSourcingRuleDataPartialSuccessTest()
      throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get(
            "src", "test", "resources", "promiseSourcingRule", "promiseSourcingRule_happyPath.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    BaseResponse<PromiseSourcingRuleDto> successfulBaseResponse =
        testUtil.getSuccessfulBaseResponseForPromiseSourcingRule();
    BaseResponse<PromiseSourcingRuleDto> failedBaseResponse =
        testUtil.getFailedBaseResponseForPromiseSourcingRule();

    when(promiseSourcingRuleFeign.createPromiseSourcingRule(
            any(CreatePromiseSourcingRuleRequest.class)))
        .thenReturn(successfulBaseResponse);
    when(promiseSourcingRuleFeign.updatePromiseSourcingRule(
            anyString(),
            anyString(),
            anyString(),
            anyString(),
            anyInt(),
            any(UpdatePromiseSourcingRuleRequest.class)))
        .thenReturn(failedBaseResponse);
    when(promiseSourcingRuleFeign.deletePromiseSourcingRule(
            anyString(), anyString(), anyString(), anyString(), anyInt()))
        .thenReturn(failedBaseResponse);
    ResponseEntity<BaseResponse<String>> response =
        promiseSourcingRuleDataUploadUtilityService.uploadPromiseSourcingRuleData(absolutePath);
    assertEquals(HttpStatus.MULTI_STATUS, response.getStatusCode());
    assertEquals(
        PROMISE_SOURCING_RULE_DATA_UPLOAD_PARTIAL_SUCCESS,
        Objects.requireNonNull(response.getBody()).getMessage());
  }

  @Test
  void uploadPromiseSourcingRuleDataFailureTest() throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get(
            "src", "test", "resources", "promiseSourcingRule", "promiseSourcingRule_happyPath.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    ResponseEntity<BaseResponse<String>> response =
        promiseSourcingRuleDataUploadUtilityService.uploadPromiseSourcingRuleData(absolutePath);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(
        PROMISE_SOURCING_RULE_DATA_UPLOAD_FAILED,
        Objects.requireNonNull(response.getBody()).getMessage());
  }

  @Test
  void uploadPromiseSourcingRuleDataFileNotFoundExceptionTest() {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "promiseSourcingRule", "promiseSourcingRule.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    assertThrows(
        IOException.class,
        () ->
            promiseSourcingRuleDataUploadUtilityService.uploadPromiseSourcingRuleData(
                absolutePath));
  }

  @Test
  void uploadPromiseSourcingRuleDataInvalidFileTypeExceptionTest() {
    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "promiseSourcingRule",
            "promiseSourcingRule_invalidFileType.html");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                promiseSourcingRuleDataUploadUtilityService.uploadPromiseSourcingRuleData(
                    absolutePath));

    assertEquals(PROMISE_SOURCING_RULE_DATA_UPLOAD_INVALID_FILE_TYPE, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadPromiseSourcingRuleDataEmptyRecordsExceptionTest() {
    Path resourceDirectory =
        Paths.get(
            "src", "test", "resources", "promiseSourcingRule", "promiseSourcingRule_noRecords.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                promiseSourcingRuleDataUploadUtilityService.uploadPromiseSourcingRuleData(
                    absolutePath));

    assertEquals(PROMISE_SOURCING_RULE_DATA_UPLOAD_FILE_EMPTY_RECORDS, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadPromiseSourcingRuleDataMaxRowsExceptionTest() {
    Path resourceDirectory =
        Paths.get(
            "src", "test", "resources", "promiseSourcingRule", "promiseSourcingRule_maxRows.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                promiseSourcingRuleDataUploadUtilityService.uploadPromiseSourcingRuleData(
                    absolutePath));

    assertEquals(PROMISE_SOURCING_RULE_DATA_UPLOAD_LARGE_ROW_SIZE, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadPromiseSourcingRuleDataInvalidHeadersExceptionTest() {
    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "promiseSourcingRule",
            "promiseSourcingRule_invalidHeaders.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                promiseSourcingRuleDataUploadUtilityService.uploadPromiseSourcingRuleData(
                    absolutePath));

    assertEquals(PROMISE_SOURCING_RULE_DATA_UPLOAD_INVALID_FILE_HEADERS, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadPromiseSourcingRuleLargeFileSizeExceptionTest() {
    ReflectionTestUtils.setField(
        promiseSourcingRuleDataUploadUtilityService, "maxSizeInKiloBytes", 10);
    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "promiseSourcingRule",
            "promiseSourcingRule_largeFileSize.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                promiseSourcingRuleDataUploadUtilityService.uploadPromiseSourcingRuleData(
                    absolutePath));

    assertEquals(PROMISE_SOURCING_RULE_DATA_UPLOAD_LARGE_FILE_SIZE, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadPromiseSourcingRuleInvalidActionExceptionTest()
      throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "promiseSourcingRule",
            "promiseSourcingRule_invalidAction.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    ResponseEntity<BaseResponse<String>> response =
        promiseSourcingRuleDataUploadUtilityService.uploadPromiseSourcingRuleData(absolutePath);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }
}
