/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.vendor.consumer.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.enums.ActionEnum;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.util.DateUtil;
import com.nextuple.master.data.integration.dto.ResponseDto;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.outbound.BatchResponse;
import com.nextuple.master.data.integration.service.ErrorHandlingService;
import com.nextuple.vendor.consumer.TestUtil;
import com.nextuple.vendor.consumer.dto.VendorFeedDto;
import com.nextuple.vendor.domain.feign.VendorFeign;
import com.nextuple.vendor.persistence.domain.VendorDomainDto;
import com.nextuple.vendor.persistence.service.VendorPersistenceService;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
class VendorBatchServiceImplTest {

  @InjectMocks private VendorBatchServiceImpl vendorBatchService;
  @InjectMocks private TestUtil testUtil;
  @Mock private VendorFeign vendorFeign;
  @Mock private ErrorHandlingService errorHandlingService;
  @Mock private VendorPersistenceService vendorPersistenceService;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);

    ReflectionTestUtils.setField(vendorBatchService, "errorHandlingService", errorHandlingService);
  }

  @Test
  @DisplayName("When vendor feed with create action is processed successfully")
  void processBatchRecordsTestWhenActionIsCreate() {
    List<BatchRequest<VendorFeedDto>> vendorFeedRequests =
        List.of(testUtil.getVendorFeedRequest(ActionEnum.CREATE));
    Mockito.when(vendorFeign.createVendor(any()))
        .thenReturn(testUtil.getBaseResponseOfVendorFeed("Vendor created successfully"));
    ResponseDto responseDto = testUtil.createResponseDto(1, 200, "Vendor created successfully");
    responseDto.setMessage(
        testUtil.getBaseResponseOfVendorFeed("Vendor created successfully").getMessage());
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getVendorBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    Mockito.doNothing()
        .when(errorHandlingService)
        .handleSuccessResponse(any(), any(), any(), any());
    Assertions.assertEquals(
        batchResponse, vendorBatchService.processRecordsWithRetry(vendorFeedRequests));
    verify(vendorFeign, times(1)).createVendor(any());
  }

  @Test
  @DisplayName("When vendor feed with create action is processed successfully - no retry")
  void processBatchRecordsTestWithNoRetry() {
    List<BatchRequest<VendorFeedDto>> vendorFeedRequests =
        List.of(testUtil.getVendorFeedRequest(ActionEnum.CREATE));
    Mockito.when(vendorFeign.createVendor(any()))
        .thenReturn(testUtil.getBaseResponseOfVendorFeed("Vendor created successfully"));
    ResponseDto responseDto = testUtil.createResponseDto(1, 200, "Vendor created successfully");
    responseDto.setMessage(
        testUtil.getBaseResponseOfVendorFeed("Vendor created successfully").getMessage());
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getVendorBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    Assertions.assertEquals(
        batchResponse, vendorBatchService.processRecordsWithRetry(vendorFeedRequests));
    verify(vendorFeign, times(1)).createVendor(any());
  }

  @Test
  @DisplayName("When vendor feed with update action is processed successfully")
  void processBatchRecordsTestWhenActionIsUpdate() {
    List<BatchRequest<VendorFeedDto>> vendorFeedRequests =
        List.of(testUtil.getVendorFeedRequest(ActionEnum.UPDATE));
    Mockito.when(vendorFeign.updateVendorDetails(any(), any(), any()))
        .thenReturn(testUtil.getBaseResponseOfVendorFeed("Vendor details updated successfully"));
    ResponseDto responseDto =
        testUtil.createResponseDto(1, 200, "Vendor details updated successfully");
    responseDto.setMessage(
        testUtil.getBaseResponseOfVendorFeed("Vendor details updated successfully").getMessage());
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getVendorBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    Mockito.doNothing()
        .when(errorHandlingService)
        .handleSuccessResponse(any(), any(), any(), any());
    Assertions.assertEquals(
        batchResponse, vendorBatchService.processRecordsWithRetry(vendorFeedRequests));
    verify(vendorFeign, times(1)).updateVendorDetails(any(), any(), any());
  }

  @Test
  @DisplayName("When vendor feed with delete action is processed successfully")
  void processBatchRecordsTestWhenActionIsDelete() {
    List<BatchRequest<VendorFeedDto>> vendorFeedRequests =
        List.of(testUtil.getVendorFeedRequest(ActionEnum.DELETE));
    Mockito.when(vendorFeign.deleteVendor(any(), any()))
        .thenReturn(testUtil.getBaseResponseOfVendorFeed("Vendor deleted successfully"));
    ResponseDto responseDto = testUtil.createResponseDto(1, 200, "Vendor deleted successfully");
    responseDto.setMessage(
        testUtil.getBaseResponseOfVendorFeed("Vendor deleted successfully").getMessage());
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getVendorBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    Mockito.doNothing()
        .when(errorHandlingService)
        .handleSuccessResponse(any(), any(), any(), any());
    Assertions.assertEquals(
        batchResponse, vendorBatchService.processRecordsWithRetry(vendorFeedRequests));
    verify(vendorFeign, times(1)).deleteVendor(any(), any());
  }

  @Test
  @DisplayName("When vendor feed for outdated records")
  void processBatchRecordsTestForOutdatedRecords() {
    BatchRequest<VendorFeedDto> batchRequest = testUtil.getVendorFeedRequest(ActionEnum.UPDATE);
    batchRequest.setReceivedTimestamp(new Date());
    List<BatchRequest<VendorFeedDto>> vendorFeedRequests = List.of(batchRequest);
    ResponseDto responseDto =
        testUtil.createResponseDto(1, HttpStatus.OK.value(), "Vendor details updated successfully");
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getVendorBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    Mockito.doNothing()
        .when(errorHandlingService)
        .handleExceptions(anyInt(), any(), any(), any(), any(), any());
    VendorDomainDto vendorDomainDto = testUtil.getVendorDomainDto();
    vendorDomainDto.setLastModifiedDate(DateUtil.addDaysToDate(new Date(), 1));
    when(vendorPersistenceService.findVendorByVendorIdAndOrgId(anyString(), anyString()))
        .thenReturn(Optional.of(vendorDomainDto));
    when(vendorFeign.updateVendorDetails(any(), any(), any()))
        .thenReturn(testUtil.getBaseResponseOfVendorFeed("Vendor details updated successfully"));
    Assertions.assertEquals(
        batchResponse, vendorBatchService.processRecordsWithRetry(vendorFeedRequests));
    verify(vendorFeign, times(0)).createVendor(any());
  }

  @Test
  @DisplayName("Test Retry capability for vendor feed")
  void handleVendorRetryTest() {
    BatchRequest<?> inputFeedRequest = testUtil.getVendorFeedRequest(ActionEnum.CREATE);
    Mockito.when(vendorFeign.createVendor(any()))
        .thenReturn(testUtil.getBaseResponseOfVendorFeed("Vendor created successfully"));
    String responseMessage = vendorBatchService.handleRetry(inputFeedRequest);

    Assertions.assertEquals("Vendor created successfully", responseMessage);

    verify(vendorFeign, times(1)).createVendor(any());
  }

  @Test
  @DisplayName("When vendor feed for outdated records - outdated vendors present")
  void checkForOutdatedRecordTestWhenRecordIsOutdated() {
    BatchRequest<VendorFeedDto> batchRequest = testUtil.getVendorFeedRequest(ActionEnum.UPDATE);
    batchRequest.setReceivedTimestamp(new Date());
    VendorDomainDto vendorDomainDto = testUtil.getVendorDomainDto();
    vendorDomainDto.setLastModifiedDate(DateUtil.addDaysToDate(new Date(), 1));
    when(vendorPersistenceService.findVendorByVendorIdAndOrgId(anyString(), anyString()))
        .thenReturn(Optional.of(vendorDomainDto));
    Assertions.assertDoesNotThrow(() -> vendorBatchService.checkForOutdatedRecord(batchRequest));
  }

  @Test
  @DisplayName("When vendor feed for outdated records - no outdated vendors present")
  void checkForOutdatedRecordTestWhenRecordIsNotOutdated() {
    BatchRequest<VendorFeedDto> batchRequest = testUtil.getVendorFeedRequest(ActionEnum.UPDATE);
    batchRequest.setReceivedTimestamp(new Date());
    VendorDomainDto vendorDomainDto = testUtil.getVendorDomainDto();
    vendorDomainDto.setLastModifiedDate(DateUtil.addDaysToDate(new Date(), -1));
    when(vendorPersistenceService.findVendorByVendorIdAndOrgId(anyString(), anyString()))
        .thenReturn(Optional.of(vendorDomainDto));
    Assertions.assertDoesNotThrow(() -> vendorBatchService.checkForOutdatedRecord(batchRequest));
  }

  @Test
  @DisplayName("When vendor feed for outdated records - no vendor found")
  void checkForOutdatedRecordTestWhenVendorDoesNotExist()
      throws CommonServiceException, PromiseEngineException {
    BatchRequest<VendorFeedDto> batchRequest = testUtil.getVendorFeedRequest(ActionEnum.UPDATE);
    batchRequest.setReceivedTimestamp(new Date());
    when(vendorPersistenceService.findVendorByVendorIdAndOrgId(anyString(), anyString()))
        .thenReturn(Optional.empty());
    Assertions.assertDoesNotThrow(() -> vendorBatchService.checkForOutdatedRecord(batchRequest));
  }

  @Test
  @DisplayName("When vendor feed for outdated records - exception case")
  void checkForOutdatedRecordTestWhenVendorDomainExceptionIsThrown() {
    BatchRequest<VendorFeedDto> batchRequest = testUtil.getVendorFeedRequest(ActionEnum.UPDATE);
    batchRequest.setReceivedTimestamp(new Date());
    when(vendorPersistenceService.findVendorByVendorIdAndOrgId(anyString(), anyString()))
        .thenThrow(new RuntimeException("Something went wrong"));
    Assertions.assertDoesNotThrow(() -> vendorBatchService.checkForOutdatedRecord(batchRequest));
  }
}
