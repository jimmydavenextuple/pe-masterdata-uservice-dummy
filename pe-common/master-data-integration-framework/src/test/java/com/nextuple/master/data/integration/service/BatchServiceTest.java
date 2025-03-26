/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.master.data.integration.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.ConfigException;
import com.nextuple.common.response.error.ErrorPayload;
import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.common.util.DateUtil;
import com.nextuple.jobs.framework.common.utils.ExceptionUtils;
import com.nextuple.master.data.integration.TestUtil;
import com.nextuple.master.data.integration.enums.ActionEnum;
import com.nextuple.master.data.integration.enums.TaskInformation;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.mocks.MockDto;
import com.nextuple.master.data.integration.mocks.MockExceptionService;
import com.nextuple.master.data.integration.mocks.MockService;
import com.nextuple.master.data.integration.outbound.BatchResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BatchServiceTest {

  @InjectMocks MockService mockService;
  @Mock private ErrorHandlingService errorHandlingService;
  @InjectMocks MockExceptionService mockExceptionService;
  private MockedStatic<ExceptionUtils> exceptionUtils;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(mockService, "errorHandlingService", errorHandlingService);
    ReflectionTestUtils.setField(
        mockExceptionService, "errorHandlingService", errorHandlingService);
  }

  @AfterEach
  void close() {
    if (exceptionUtils != null && !exceptionUtils.isClosed()) {
      exceptionUtils.close();
    }
  }

  @Test
  @DisplayName("When master data feed with create action is processed successfully")
  void masterDataFeedWithCreateActionTest() {
    MockDto mockDto = new MockDto();
    mockDto.setOrgId(TestUtil.TENANT);

    BatchRequest<MockDto> batchRequest1 = new BatchRequest<>();
    batchRequest1.setAction(ActionEnum.CREATE);
    batchRequest1.setRecordNo(1);
    batchRequest1.setProducedTimestamp(DateUtil.addDaysToDate(new Date(), 1));
    batchRequest1.setPayload(mockDto);

    BatchRequest<MockDto> batchRequest2 = new BatchRequest<>();
    batchRequest2.setAction(ActionEnum.CREATE);
    batchRequest2.setRecordNo(2);
    batchRequest2.setPayload(mockDto);

    doNothing().when(errorHandlingService).handleSuccessResponse(any(), any(), any(), anyString());
    BatchResponse result =
        mockService.processRecordsWithRetry(List.of(batchRequest1, batchRequest2));

    Assertions.assertEquals(HttpStatus.OK.value(), result.getResponses().get(0).getStatusCode());
    Assertions.assertEquals(2, result.getTotalRecords());
    Assertions.assertEquals(2, result.getSuccessfulRecords());
  }

  @Test
  @DisplayName("When master data feed with update action is processed successfully")
  void masterDataFeedWithUpdateActionTest() {

    MockDto mockDto = new MockDto();
    mockDto.setOrgId(TestUtil.TENANT);

    BatchRequest<MockDto> batchRequest1 = new BatchRequest<>();
    batchRequest1.setAction(ActionEnum.UPDATE);
    batchRequest1.setRecordNo(1);
    batchRequest1.setProducedTimestamp(DateUtil.addDaysToDate(new Date(), 1));
    batchRequest1.setPayload(mockDto);

    BatchRequest<MockDto> batchRequest2 = new BatchRequest<>();
    batchRequest2.setAction(ActionEnum.UPDATE);
    batchRequest2.setRecordNo(2);
    batchRequest2.setPayload(mockDto);

    doNothing().when(errorHandlingService).handleSuccessResponse(any(), any(), any(), anyString());

    BatchResponse result =
        mockService.processRecordsWithRetry(List.of(batchRequest1, batchRequest2));

    Assertions.assertEquals(HttpStatus.OK.value(), result.getResponses().get(0).getStatusCode());
    Assertions.assertEquals(2, result.getTotalRecords());
    Assertions.assertEquals(2, result.getSuccessfulRecords());
  }

  @Test
  @DisplayName("When master data feed with delete action is processed successfully")
  void masterDataFeedWithDeleteActionTest() {
    MockDto mockDto = new MockDto();
    mockDto.setOrgId(TestUtil.TENANT);

    BatchRequest<MockDto> batchRequest1 = new BatchRequest<>();
    batchRequest1.setAction(ActionEnum.DELETE);
    batchRequest1.setRecordNo(1);
    batchRequest1.setProducedTimestamp(DateUtil.addDaysToDate(new Date(), 1));
    batchRequest1.setPayload(mockDto);

    BatchRequest<MockDto> batchRequest2 = new BatchRequest<>();
    batchRequest2.setAction(ActionEnum.DELETE);
    batchRequest2.setRecordNo(2);
    batchRequest2.setPayload(mockDto);

    doNothing().when(errorHandlingService).handleSuccessResponse(any(), any(), any(), anyString());

    BatchResponse result =
        mockService.processRecordsWithRetry(List.of(batchRequest1, batchRequest2));

    Assertions.assertEquals(HttpStatus.OK.value(), result.getResponses().get(0).getStatusCode());
    Assertions.assertEquals(2, result.getTotalRecords());
    Assertions.assertEquals(2, result.getSuccessfulRecords());
  }

  @Test
  @DisplayName("When master data feed is sorted acc to the produced timestamp")
  void testProcessRecordsWithProducedTimestampAdded() {
    MockDto mockDto = new MockDto();
    mockDto.setOrgId(TestUtil.TENANT);

    BatchRequest<MockDto> batchRequest1 = new BatchRequest<>();
    batchRequest1.setAction(ActionEnum.CREATE);
    batchRequest1.setRecordNo(1);
    batchRequest1.setProducedTimestamp(DateUtil.addDaysToDate(new Date(), 1));
    batchRequest1.setPayload(mockDto);

    BatchRequest<MockDto> batchRequest2 = new BatchRequest<>();
    batchRequest2.setAction(ActionEnum.CREATE);
    batchRequest2.setRecordNo(2);
    batchRequest2.setProducedTimestamp(new Date());
    batchRequest2.setPayload(mockDto);

    doNothing().when(errorHandlingService).handleSuccessResponse(any(), any(), any(), anyString());

    BatchResponse result =
        mockService.processRecordsWithRetry(List.of(batchRequest1, batchRequest2));

    Assertions.assertEquals(HttpStatus.OK.value(), result.getResponses().get(0).getStatusCode());
    Assertions.assertEquals(2, result.getTotalRecords());
    Assertions.assertEquals(2, result.getSuccessfulRecords());
  }

  @Test
  @DisplayName("When master data feed is processed without invoking the retry framework")
  void processRecordsWithoutRetryTest() {
    BatchService<MockDto> batchService =
        new BatchService<MockDto>() {
          @Override
          public TypeReference<BatchRequest<MockDto>> getTypeReference() {
            return new TypeReference<BatchRequest<MockDto>>() {};
          }

          @Override
          public String createRecordImpl(MockDto payload) throws CommonServiceException {
            return "Record created successfully";
          }

          @Override
          public String updateRecordImpl(MockDto payload) throws CommonServiceException {
            return null;
          }

          @Override
          public String deleteRecordImpl(MockDto payload) throws CommonServiceException {
            return null;
          }

          @Override
          public void checkForOutdatedRecord(BatchRequest<MockDto> batchRequest)
              throws CommonServiceException {}

          @Override
          public TaskInformation getTaskInformation() {
            return null;
          }
        };
    MockDto mockDto = new MockDto();
    mockDto.setOrgId(TestUtil.TENANT);
    BatchRequest<MockDto> batchRequest = new BatchRequest<>();
    batchRequest.setAction(ActionEnum.CREATE);
    batchRequest.setPayload(mockDto);

    BatchResponse result = batchService.processRecordsWithoutRetry(List.of(batchRequest));

    Assertions.assertEquals(HttpStatus.OK.value(), result.getResponses().get(0).getStatusCode());
    Assertions.assertEquals(1, result.getTotalRecords());
    Assertions.assertEquals(1, result.getSuccessfulRecords());
  }

  @Test
  @DisplayName(
      "When feign exception is thrown with some field error on processing master data feed")
  void processRecordsFeignExceptionTestForFieldErrors() {
    MockDto mockDto = new MockDto();
    mockDto.setOrgId(TestUtil.TENANT);
    BatchRequest<MockDto> batchRequest = new BatchRequest<>();
    batchRequest.setAction(ActionEnum.CREATE);
    batchRequest.setRecordNo(1);
    batchRequest.setPayload(mockDto);
    doNothing()
        .when(errorHandlingService)
        .handleExceptions(anyInt(), any(), any(), any(), any(), any());

    FieldError fieldError = new FieldError();
    fieldError.setErrorMessage("nodeType can't be empty");
    ErrorPayload errorPayload = new ErrorPayload();
    errorPayload.setFields(Map.of("nodeId", fieldError));
    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setPayload(errorPayload);
    try (MockedStatic<ExceptionUtils> mockedStatic = Mockito.mockStatic(ExceptionUtils.class)) {
      mockedStatic.when(() -> ExceptionUtils.parseFeignException(any())).thenReturn(errorResponse);
      BatchResponse result = mockExceptionService.processRecordsWithRetry(List.of(batchRequest));
      Assertions.assertEquals(
          HttpStatus.BAD_REQUEST.value(), result.getResponses().get(0).getStatusCode());
      Assertions.assertEquals(1, result.getTotalRecords());
      Assertions.assertEquals(1, result.getFailedRecords());
      Assertions.assertNotNull(result.getResponses().get(0).getMessage());
      Assertions.assertEquals("Upstream error : null", result.getResponses().get(0).getMessage());
    }
  }

  @Test
  @DisplayName(
      "When feign exception is thrown with field error map as empty on processing master data feed")
  void processRecordsFeignExceptionTestWhenFieldErrorMapIsEmpty() {
    MockDto mockDto = new MockDto();
    mockDto.setOrgId(TestUtil.TENANT);
    BatchRequest<MockDto> batchRequest = new BatchRequest<>();
    batchRequest.setAction(ActionEnum.CREATE);
    batchRequest.setRecordNo(1);
    batchRequest.setPayload(mockDto);

    ErrorPayload errorPayload = new ErrorPayload();
    errorPayload.setFields(Map.of());
    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setPayload(errorPayload);
    errorResponse.setMessage("Upstream error");
    exceptionUtils = Mockito.mockStatic(ExceptionUtils.class);
    exceptionUtils.when(() -> ExceptionUtils.parseFeignException(any())).thenReturn(errorResponse);
    doNothing()
        .when(errorHandlingService)
        .handleExceptions(anyInt(), any(), any(), any(TaskInformation.class), any(), any());

    BatchResponse result = mockExceptionService.processRecordsWithRetry(List.of(batchRequest));

    Assertions.assertEquals(
        HttpStatus.BAD_REQUEST.value(), result.getResponses().get(0).getStatusCode());
    Assertions.assertEquals(1, result.getTotalRecords());
    Assertions.assertEquals(1, result.getFailedRecords());
    Assertions.assertNotNull(result.getResponses().get(0).getMessage());
    exceptionUtils.close();
  }

  @Test
  @DisplayName(
      "When feign exception is thrown with some error message in the payload on processing master data feed")
  void processRecordsFeignExceptionTestForNoFieldErrorsButPayloadHasErrorMessage() {

    MockDto mockDto = new MockDto();
    mockDto.setOrgId(TestUtil.TENANT);
    BatchRequest<MockDto> batchRequest = new BatchRequest<>();
    batchRequest.setAction(ActionEnum.CREATE);
    batchRequest.setRecordNo(1);
    batchRequest.setPayload(mockDto);

    FieldError fieldError = new FieldError();
    ErrorPayload errorPayload = new ErrorPayload();
    errorPayload.setFields(Map.of("nodeId", fieldError));
    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setPayload(errorPayload);
    errorResponse.setMessage("Upstream error");
    exceptionUtils = Mockito.mockStatic(ExceptionUtils.class);
    exceptionUtils.when(() -> ExceptionUtils.parseFeignException(any())).thenReturn(errorResponse);
    BatchResponse result = mockExceptionService.processRecordsWithRetry(List.of(batchRequest));

    Assertions.assertEquals(
        HttpStatus.BAD_REQUEST.value(), result.getResponses().get(0).getStatusCode());
    Assertions.assertEquals(1, result.getTotalRecords());
    Assertions.assertEquals(1, result.getFailedRecords());
    Assertions.assertNotNull(result.getResponses().get(0).getMessage());
    Assertions.assertEquals("Upstream error : null", result.getResponses().get(0).getMessage());
    exceptionUtils.close();
  }

  @Test
  @DisplayName("When feign exception is thrown due to upstream error")
  void processRecordsFeignExceptionTestForNullPayload() {

    MockDto mockDto = new MockDto();
    mockDto.setOrgId(TestUtil.TENANT);
    BatchRequest<MockDto> batchRequest = new BatchRequest<>();
    batchRequest.setAction(ActionEnum.CREATE);
    batchRequest.setRecordNo(1);
    batchRequest.setPayload(mockDto);

    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setMessage("Upstream error");
    exceptionUtils = Mockito.mockStatic(ExceptionUtils.class);
    exceptionUtils.when(() -> ExceptionUtils.parseFeignException(any())).thenReturn(errorResponse);
    BatchResponse result = mockExceptionService.processRecordsWithRetry(List.of(batchRequest));

    Assertions.assertEquals(
        HttpStatus.BAD_REQUEST.value(), result.getResponses().get(0).getStatusCode());
    Assertions.assertEquals(1, result.getTotalRecords());
    Assertions.assertEquals(1, result.getFailedRecords());
    Assertions.assertNotNull(result.getResponses().get(0).getMessage());
    Assertions.assertEquals("Upstream error : null", result.getResponses().get(0).getMessage());
    exceptionUtils.close();
  }

  @Test
  @DisplayName("When a generic exception is thrown on processing master data feed")
  void processRecordsGenericExceptionTest() {
    BatchService<MockDto> batchService =
        new BatchService<>() {
          @Override
          public TypeReference<BatchRequest<MockDto>> getTypeReference() {
            return new TypeReference<BatchRequest<MockDto>>() {};
          }

          @Override
          public String createRecordImpl(MockDto payload) throws CommonServiceException {
            throw new RuntimeException("Generic exception");
          }

          @Override
          public String updateRecordImpl(MockDto payload) throws CommonServiceException {
            return null;
          }

          @Override
          public String deleteRecordImpl(MockDto payload) throws CommonServiceException {
            return null;
          }

          @Override
          public void checkForOutdatedRecord(BatchRequest<MockDto> batchRequest)
              throws CommonServiceException {}

          @Override
          public void addTaskForSuccessResponse(BatchRequest<MockDto> batchRequest) {}

          @Override
          public void addTaskForException(
              int statusCode, String errorMessage, BatchRequest<MockDto> batchRequest) {}

          @Override
          public TaskInformation getTaskInformation() {
            return null;
          }
        };
    MockDto mockDto = new MockDto();
    mockDto.setOrgId(TestUtil.TENANT);
    BatchRequest<MockDto> batchRequest = new BatchRequest<>();
    batchRequest.setAction(ActionEnum.CREATE);
    batchRequest.setRecordNo(1);
    batchRequest.setPayload(mockDto);
    BatchResponse result = batchService.processRecordsWithRetry(List.of(batchRequest));

    Assertions.assertEquals(
        HttpStatus.BAD_REQUEST.value(), result.getResponses().get(0).getStatusCode());
    Assertions.assertEquals(1, result.getTotalRecords());
    Assertions.assertEquals(1, result.getFailedRecords());
  }

  @Test
  @DisplayName("When a given record is retried")
  void handledRetryTest() {
    BatchService<MockDto> batchService =
        new BatchService<>() {
          @Override
          public TypeReference<BatchRequest<MockDto>> getTypeReference() {
            return new TypeReference<BatchRequest<MockDto>>() {};
          }

          @Override
          public String createRecordImpl(MockDto payload) throws CommonServiceException {
            return "Record created successfully";
          }

          @Override
          public String updateRecordImpl(MockDto payload) throws CommonServiceException {
            return "Record updated successfully";
          }

          @Override
          public String deleteRecordImpl(MockDto payload) {
            return "Record deleted successfully";
          }

          @Override
          public void checkForOutdatedRecord(BatchRequest<MockDto> batchRequest) {}

          @Override
          public void addTaskForSuccessResponse(BatchRequest<MockDto> batchRequest) {}

          @Override
          public void addTaskForException(
              int statusCode, String errorMessage, BatchRequest<MockDto> batchRequest) {}

          @Override
          public TaskInformation getTaskInformation() {
            return TaskInformation.NODE_FEED;
          }
        };

    MockDto mockDto = new MockDto();
    mockDto.setOrgId(TestUtil.TENANT);

    BatchRequest<MockDto> batchRequest = new BatchRequest<>();
    batchRequest.setAction(ActionEnum.CREATE);
    batchRequest.setRecordNo(1);
    batchRequest.setProducedTimestamp(DateUtil.addDaysToDate(new Date(), 1));
    batchRequest.setPayload(mockDto);

    Assertions.assertDoesNotThrow(() -> batchService.handleRetry(batchRequest));
  }

  @Test
  @DisplayName("When there is an exception while handling retry")
  void handledRetryExceptionTest() {
    BatchService<MockDto> batchService =
        new BatchService<>() {
          @Override
          public TypeReference<BatchRequest<MockDto>> getTypeReference() {
            return new TypeReference<BatchRequest<MockDto>>() {};
          }

          @Override
          public String createRecordImpl(MockDto payload) throws CommonServiceException {
            throw new CommonServiceException(
                "Outdated records", HttpStatus.BAD_REQUEST, null, null);
          }

          @Override
          public String updateRecordImpl(MockDto payload) throws CommonServiceException {
            return "Record updated successfully";
          }

          @Override
          public String deleteRecordImpl(MockDto payload) {
            return "Record deleted successfully";
          }

          @Override
          public void checkForOutdatedRecord(BatchRequest<MockDto> batchRequest)
              throws CommonServiceException {}

          @Override
          public void addTaskForSuccessResponse(BatchRequest<MockDto> batchRequest) {}

          @Override
          public void addTaskForException(
              int statusCode, String errorMessage, BatchRequest<MockDto> batchRequest) {}

          @Override
          public TaskInformation getTaskInformation() {
            return TaskInformation.NODE_FEED;
          }
        };

    MockDto mockDto = new MockDto();
    mockDto.setOrgId(TestUtil.TENANT);

    BatchRequest<MockDto> batchRequest = new BatchRequest<>();
    batchRequest.setAction(ActionEnum.CREATE);
    batchRequest.setRecordNo(1);
    batchRequest.setProducedTimestamp(DateUtil.addDaysToDate(new Date(), 1));
    batchRequest.setPayload(mockDto);
    ConfigException exception =
        Assertions.assertThrows(
            ConfigException.class, () -> batchService.handleRetry(batchRequest));

    Assertions.assertEquals("Outdated records", exception.getMessage());
  }
}
