/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.carrier.consumer.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.carrier.consumer.TestUtil;
import com.nextuple.carrier.consumer.dto.CarrierFeedDto;
import com.nextuple.carrier.domain.feign.CarrierFeign;
import com.nextuple.carrier.persistence.domain.CarrierServiceDomainDto;
import com.nextuple.carrier.persistence.exception.CarrierServiceDomainException;
import com.nextuple.carrier.persistence.service.impl.CarrierServicePersistenceServiceImpl;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.util.DateUtil;
import com.nextuple.master.data.integration.dto.ResponseDto;
import com.nextuple.master.data.integration.enums.ActionEnum;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.outbound.BatchResponse;
import com.nextuple.master.data.integration.service.ErrorHandlingService;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
class CarrierBatchServiceImplTest {

  @InjectMocks private CarrierBatchServiceImpl carrierBatchService;
  @InjectMocks private TestUtil testUtil;
  @Mock private CarrierFeign carrierFeign;
  @Mock private ErrorHandlingService errorHandlingService;
  @Mock private CarrierServicePersistenceServiceImpl carrierServicePersistenceService;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);

    ReflectionTestUtils.setField(carrierBatchService, "errorHandlingService", errorHandlingService);
  }

  @Test
  @DisplayName("When carrier feed with create action is processed successfully")
  void processBatchRecordsTestWhenActionIsCreate() {
    List<BatchRequest<CarrierFeedDto>> carrierFeedRequests =
        List.of(testUtil.getCarrierFeedRequest(ActionEnum.CREATE));
    Mockito.when(carrierFeign.createCarrierService(any()))
        .thenReturn(testUtil.getBaseResponseOfCarrierFeed("Carrier service created successfully"));
    ResponseDto responseDto =
        testUtil.createResponseDto(1, 200, "Carrier service created successfully");
    responseDto.setMessage(
        testUtil.getBaseResponseOfCarrierFeed("Carrier service created successfully").getMessage());
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getCarrierBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    Mockito.doNothing()
        .when(errorHandlingService)
        .handleSuccessResponse(any(), any(), any(), any());
    assertEquals(batchResponse, carrierBatchService.processRecordsWithRetry(carrierFeedRequests));
    verify(carrierFeign, times(1)).createCarrierService(any());
  }

  @Test
  @DisplayName("When the batch records are processed successfully")
  void processBatchRecordsTestWithNoRetry() {
    List<BatchRequest<CarrierFeedDto>> carrierFeedRequests =
        List.of(testUtil.getCarrierFeedRequest(ActionEnum.CREATE));
    Mockito.when(carrierFeign.createCarrierService(any()))
        .thenReturn(testUtil.getBaseResponseOfCarrierFeed("Carrier service created successfully"));
    ResponseDto responseDto =
        testUtil.createResponseDto(1, 200, "Carrier service created successfully");
    responseDto.setMessage(
        testUtil.getBaseResponseOfCarrierFeed("Carrier service created successfully").getMessage());
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getCarrierBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    assertEquals(batchResponse, carrierBatchService.processRecordsWithRetry(carrierFeedRequests));
    verify(carrierFeign, times(1)).createCarrierService(any());
  }

  @Test
  @DisplayName("When the batch records are processed for the update action")
  void processBatchRecordsTestWhenActionIsUpdate() {
    List<BatchRequest<CarrierFeedDto>> carrierFeedRequests =
        List.of(testUtil.getCarrierFeedRequest(ActionEnum.UPDATE));
    Mockito.when(carrierFeign.updateCarrierServiceDetails(any(), any(), any(), any()))
        .thenReturn(
            testUtil.getBaseResponseOfCarrierFeed("Carrier service details updated successfully"));
    ResponseDto responseDto =
        testUtil.createResponseDto(1, 200, "Carrier service details updated successfully");
    responseDto.setMessage(
        testUtil
            .getBaseResponseOfCarrierFeed("Carrier service details updated successfully")
            .getMessage());
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getCarrierBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    Mockito.doNothing()
        .when(errorHandlingService)
        .handleSuccessResponse(any(), any(), any(), any());
    assertEquals(batchResponse, carrierBatchService.processRecordsWithRetry(carrierFeedRequests));
    verify(carrierFeign, times(1)).updateCarrierServiceDetails(any(), any(), any(), any());
  }

  @Test
  @DisplayName("When the batch records are processed for the delete action")
  void processBatchRecordsTestWhenActionIsDelete() {
    List<BatchRequest<CarrierFeedDto>> carrierFeedRequests =
        List.of(testUtil.getCarrierFeedRequest(ActionEnum.DELETE));
    Mockito.when(carrierFeign.deleteCarrierService(any(), any(), any()))
        .thenReturn(testUtil.getBaseResponseOfCarrierFeed("Carrier service deleted successfully"));
    ResponseDto responseDto =
        testUtil.createResponseDto(1, 200, "Carrier service deleted successfully");
    responseDto.setMessage(
        testUtil.getBaseResponseOfCarrierFeed("Carrier service deleted successfully").getMessage());
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getCarrierBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    Mockito.doNothing()
        .when(errorHandlingService)
        .handleSuccessResponse(any(), any(), any(), any());
    assertEquals(batchResponse, carrierBatchService.processRecordsWithRetry(carrierFeedRequests));
    verify(carrierFeign, times(1)).deleteCarrierService(any(), any(), any());
  }

  @Test
  @DisplayName("When the batch records cannot be processed as they are outdated")
  void processBatchRecordsTestForOutdatedRecords() throws CarrierServiceDomainException {
    BatchRequest<CarrierFeedDto> batchRequest = testUtil.getCarrierFeedRequest(ActionEnum.UPDATE);
    batchRequest.setReceivedTimestamp(new Date());
    List<BatchRequest<CarrierFeedDto>> carrierFeedRequests = List.of(batchRequest);
    ResponseDto responseDto =
        testUtil.createResponseDto(
            1, HttpStatus.BAD_REQUEST.value(), "Can't process the record as it's outdated");
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getCarrierBatchResponse(1, 0, 1);
    batchResponse.setResponses(responseDtoList);
    Mockito.doNothing()
        .when(errorHandlingService)
        .handleExceptions(anyInt(), any(), any(), any(), any(), any());
    CarrierServiceDomainDto carrierServiceEntity = testUtil.getCarrierServiceDomainDto();
    carrierServiceEntity.setLastModifiedDate(DateUtil.addDaysToDate(new Date(), 1));
    when(carrierServicePersistenceService.findCarrierServiceByCarrierIdAndServiceIdAndOrgId(
            anyString(), anyString(), anyString()))
        .thenReturn(Optional.of(carrierServiceEntity));
    assertEquals(batchResponse, carrierBatchService.processRecordsWithRetry(carrierFeedRequests));
    verify(carrierFeign, times(0)).createCarrierService(any());
  }

  @Test
  @DisplayName("When the batch records are retried")
  void handleCarrierRetryTest() throws CommonServiceException {
    BatchRequest<?> inputFeedRequest = testUtil.getCarrierFeedRequest(ActionEnum.CREATE);
    Mockito.when(carrierFeign.createCarrierService(any()))
        .thenReturn(testUtil.getBaseResponseOfCarrierFeed("Carrier service created successfully"));
    String responseMessage = carrierBatchService.handleRetry(inputFeedRequest);

    assertEquals("Carrier service created successfully", responseMessage);

    verify(carrierFeign, times(1)).createCarrierService(any());
  }
}
