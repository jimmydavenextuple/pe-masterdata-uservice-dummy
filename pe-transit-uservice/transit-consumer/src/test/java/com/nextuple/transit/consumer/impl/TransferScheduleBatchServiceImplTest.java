/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */


package com.nextuple.transit.consumer.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

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

import com.nextuple.transit.consumer.TestUtil;
import com.nextuple.transit.consumer.dto.TransferScheduleDto;
import com.nextuple.transit.consumer.dto.TransitFeedDto;
import com.nextuple.transit.domain.feign.TransitFeign;
import com.nextuple.transit.persistence.domain.TransitDomainDto;
import com.nextuple.transit.persistence.exception.TransitDomainException;
import com.nextuple.transit.persistence.service.impl.TransitPersistenceServiceImpl;
import jakarta.validation.constraints.NotNull;
import org.joda.time.DateTime;
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
import com.nextuple.transit.domain.feign.TransferScheduleFeign;

@RunWith(MockitoJUnitRunner.class)
public class TransferScheduleBatchServiceImplTest {
    @InjectMocks private TransferScheduleBatchServiceImpl transferScheduleBatchService;
    @InjectMocks private TestUtil testUtil;
    @Mock private TransferScheduleFeign transferScheduleFeign;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("When transit feed with create action is processed successfully")
    void processBatchRecordsTestWhenActionIsCreate() {
        List<BatchRequest<TransferScheduleDto>> transitFeedRequests =
                List.of(testUtil.getTransferScheduleFeedRequest(ActionEnum.CREATE));
        Mockito.when(transferScheduleFeign.createTransferSchedule(any()))
                .thenReturn(testUtil.getBaseResponseOfTransferScheduleFeed("Transfer Schedule created successfully"));
        ResponseDto responseDto = testUtil.createResponseDto(1, 200, "Transit created successfully");
        responseDto.setMessage(
                testUtil.getBaseResponseOfTransferScheduleFeed("Transfer Schedule created successfully").getMessage());
        List<ResponseDto> responseDtoList = List.of(responseDto);
        BatchResponse batchResponse = testUtil.getTransferScheduleBatchResponse(1, 1, 0);
        batchResponse.setResponses(responseDtoList);

        Assertions.assertEquals(
                batchResponse, transferScheduleBatchService.processRecordsWithRetry(transitFeedRequests));
        verify(transferScheduleFeign, times(1)).createTransferSchedule(any());
    }

//    @Test
//    @DisplayName("When the batch records are processed successfully")
//    void processBatchRecordsTestWithNoRetry() {
//        List<BatchRequest<TransitFeedDto>> transitFeedRequests =
//                List.of(testUtil.getTransitFeedRequest(ActionEnum.CREATE));
//        Mockito.when(transitFeign.addTransitData(any()))
//                .thenReturn(testUtil.getBaseResponseOfTransitFeed("Transit created successfully"));
//        ResponseDto responseDto = testUtil.createResponseDto(1, 200, "Transit created successfully");
//        responseDto.setMessage(
//                testUtil.getBaseResponseOfTransitFeed("Transit created successfully").getMessage());
//        List<ResponseDto> responseDtoList = List.of(responseDto);
//        BatchResponse batchResponse = testUtil.getTransitBatchResponse(1, 1, 0);
//        batchResponse.setResponses(responseDtoList);
//        Assertions.assertEquals(
//                batchResponse, transitBatchService.processRecordsWithRetry(transitFeedRequests));
//        verify(transitFeign, times(1)).addTransitData(any());
//    }
//
//    @Test
//    @DisplayName("When the batch records are processed for the update action")
//    void processBatchRecordsTestWhenActionIsUpdate() {
//        List<BatchRequest<TransitFeedDto>> transitFeedRequests =
//                List.of(testUtil.getTransitFeedRequest(ActionEnum.UPDATE));
//        Mockito.when(transitFeign.updateTransitData(any(), any(), any(), any(), any()))
//                .thenReturn(testUtil.getBaseResponseOfTransitFeed("Transit details updated successfully"));
//        ResponseDto responseDto =
//                testUtil.createResponseDto(1, 200, "Transit details updated successfully");
//        responseDto.setMessage(
//                testUtil.getBaseResponseOfTransitFeed("Transit details updated successfully").getMessage());
//        List<ResponseDto> responseDtoList = List.of(responseDto);
//        BatchResponse batchResponse = testUtil.getTransitBatchResponse(1, 1, 0);
//        batchResponse.setResponses(responseDtoList);
//        Mockito.doNothing()
//                .when(errorHandlingService)
//                .handleSuccessResponse(any(), any(), any(), any());
//        Assertions.assertEquals(
//                batchResponse, transitBatchService.processRecordsWithRetry(transitFeedRequests));
//        verify(transitFeign, times(1)).updateTransitData(any(), any(), any(), any(), any());
//    }
//
//    @Test
//    @DisplayName("When the batch records are processed for the delete action")
//    void processBatchRecordsTestWhenActionIsDelete() {
//        List<BatchRequest<TransitFeedDto>> transitFeedRequests =
//                List.of(testUtil.getTransitFeedRequest(ActionEnum.DELETE));
//        Mockito.when(transitFeign.deleteTransitDetails(any(), any(), any(), any()))
//                .thenReturn(testUtil.getBaseResponseOfTransitFeed("Transit deleted successfully"));
//        ResponseDto responseDto = testUtil.createResponseDto(1, 200, "Transit deleted successfully");
//        responseDto.setMessage(
//                testUtil.getBaseResponseOfTransitFeed("Transit deleted successfully").getMessage());
//        List<ResponseDto> responseDtoList = List.of(responseDto);
//        BatchResponse batchResponse = testUtil.getTransitBatchResponse(1, 1, 0);
//        batchResponse.setResponses(responseDtoList);
//        Mockito.doNothing()
//                .when(errorHandlingService)
//                .handleSuccessResponse(any(), any(), any(), any());
//        Assertions.assertEquals(
//                batchResponse, transitBatchService.processRecordsWithRetry(transitFeedRequests));
//        verify(transitFeign, times(1)).deleteTransitDetails(any(), any(), any(), any());
//    }
//
//    @Test
//    @DisplayName("When the batch records cannot be processed as they are outdated")
//    void processBatchRecordsTestForOutdatedRecords() throws TransitDomainException {
//        BatchRequest<TransitFeedDto> batchRequest = testUtil.getTransitFeedRequest(ActionEnum.UPDATE);
//        batchRequest.setReceivedTimestamp(new Date());
//        List<BatchRequest<TransitFeedDto>> transitFeedRequests = List.of(batchRequest);
//        ResponseDto responseDto =
//                testUtil.createResponseDto(
//                        1, HttpStatus.BAD_REQUEST.value(), "Can't process the record as it's outdated");
//        List<ResponseDto> responseDtoList = List.of(responseDto);
//        BatchResponse batchResponse = testUtil.getTransitBatchResponse(1, 0, 1);
//        batchResponse.setResponses(responseDtoList);
//        Mockito.doNothing()
//                .when(errorHandlingService)
//                .handleExceptions(anyInt(), any(), any(), any(), any(), any());
//        TransitDomainDto transitDomainDto = testUtil.getTransitDomainDto();
//        transitDomainDto.setLastModifiedDate(DateUtil.addDaysToDate(new Date(), 1));
//        when(transitPersistenceService.findTransitDetails(
//                anyString(), anyString(), anyString(), anyString()))
//                .thenReturn(Optional.of(transitDomainDto));
//        Assertions.assertEquals(
//                batchResponse, transitBatchService.processRecordsWithRetry(transitFeedRequests));
//        verify(transitFeign, times(0)).addTransitData(any());
//    }
//
//    @Test
//    @DisplayName("When the batch records are retried")
//    void handleTransitRetryTest() throws CommonServiceException {
//        BatchRequest<?> inputFeedRequest = testUtil.getTransitFeedRequest(ActionEnum.CREATE);
//        Mockito.when(transitFeign.addTransitData(any()))
//                .thenReturn(testUtil.getBaseResponseOfTransitFeed("Transit created successfully"));
//        String responseMessage = transitBatchService.handleRetry(inputFeedRequest);
//
//        Assertions.assertEquals("Transit created successfully", responseMessage);
//
//        verify(transitFeign, times(1)).addTransitData(any());
//    }
}
