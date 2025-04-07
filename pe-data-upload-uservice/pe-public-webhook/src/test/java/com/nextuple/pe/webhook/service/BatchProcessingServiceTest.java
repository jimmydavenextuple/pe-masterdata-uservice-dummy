/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.webhook.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import com.nextuple.common.enums.ActionEnum;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.master.data.integration.dto.ResponseDto;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.outbound.BatchResponse;
import com.nextuple.pe.webhook.service.impl.CalendarFeedHandlingService;
import com.nextuple.pe.webhook.service.impl.CarrierFeedHandlingService;
import com.nextuple.pe.webhook.service.impl.CarrierServiceCalendarFeedHandlingService;
import com.nextuple.pe.webhook.service.impl.NodeCalendarFeedHandlingService;
import com.nextuple.pe.webhook.service.impl.NodeCarrierFeedHandlingService;
import com.nextuple.pe.webhook.service.impl.NodeFeedHandlingService;
import com.nextuple.pe.webhook.service.impl.NodeServiceOptionBufferFeedHandlingService;
import com.nextuple.pe.webhook.service.impl.NodeServiceOptionFeedHandlingService;
import com.nextuple.pe.webhook.service.impl.PickupCalendarFeedHandlingService;
import com.nextuple.pe.webhook.service.impl.TransferScheduleFeedHandlingService;
import com.nextuple.pe.webhook.service.impl.TransitBufferFeedHandlingService;
import com.nextuple.pe.webhook.service.impl.TransitFeedHandlingService;
import com.nextuple.pe.webhook.service.impl.VendorFeedHandlingService;
import com.nextuple.pe.webhook.util.TestUtil;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class BatchProcessingServiceTest {
  @Mock NodeFeedHandlingService nodeFeedHandlingService;
  @Mock CarrierFeedHandlingService carrierFeedHandlingService;
  @Mock NodeCarrierFeedHandlingService nodeCarrierFeedHandlingService;
  @Mock NodeServiceOptionFeedHandlingService nodeServiceOptionFeedHandlingService;
  @Mock NodeServiceOptionBufferFeedHandlingService nodeServiceOptionBufferFeedHandlingService;
  @Mock CalendarFeedHandlingService calendarFeedHandlingService;
  @Mock NodeCalendarFeedHandlingService nodeCalendarFeedHandlingService;
  @Mock CarrierServiceCalendarFeedHandlingService carrierServiceCalendarFeedHandlingService;
  @Mock PickupCalendarFeedHandlingService pickupCalendarFeedHandlingService;
  @Mock TransitFeedHandlingService transitFeedHandlingService;
  @Mock TransitBufferFeedHandlingService transitBufferFeedHandlingService;
  @Mock TransferScheduleFeedHandlingService transferScheduleFeedHandlingService;
  @Mock VendorFeedHandlingService vendorFeedHandlingService;
  @InjectMocks BatchProcessingService batchProcessingService;
  @InjectMocks TestUtil testUtil;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testProcessRecordsWithNodes() throws CommonServiceException {
    ResponseDto responseDto = testUtil.createResponseDto(1, 200, "Node created successfully");
    responseDto.setMessage("Node created successfully");
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    BatchRequest<?> batchRequest = testUtil.getNodeFeedRequest(ActionEnum.CREATE);
    List<BatchRequest<?>> batchFeed = Collections.singletonList(batchRequest);

    Mockito.when(nodeFeedHandlingService.processRecords(any(), any())).thenReturn(batchResponse);

    BatchResponse result =
        batchProcessingService.processRecords("nodes", batchFeed, TestUtil.ORG_ID);
    assertEquals(batchResponse, result);

    Mockito.verify(nodeFeedHandlingService).processRecords(any(), any());
  }

  @Test
  void testProcessRecordsWithCarriers() throws CommonServiceException {
    ResponseDto responseDto = testUtil.createResponseDto(1, 200, "Carrier created successfully");
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    BatchRequest<?> batchRequest = testUtil.getCarrierFeedRequest(ActionEnum.CREATE);
    List<BatchRequest<?>> batchFeed = Collections.singletonList(batchRequest);

    Mockito.when(carrierFeedHandlingService.processRecords(any(), any())).thenReturn(batchResponse);

    BatchResponse result =
        batchProcessingService.processRecords("carrier-service", batchFeed, TestUtil.ORG_ID);
    assertEquals(batchResponse, result);

    Mockito.verify(carrierFeedHandlingService).processRecords(any(), any());
  }

  @Test
  void testProcessRecordsWithCalendars() throws CommonServiceException {
    ResponseDto responseDto = testUtil.createResponseDto(1, 200, "Calendar created successfully");
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    BatchRequest<?> batchRequest = testUtil.getCalendarFeedRequest(ActionEnum.CREATE);
    List<BatchRequest<?>> batchFeed = Collections.singletonList(batchRequest);

    Mockito.when(calendarFeedHandlingService.processRecords(any(), any()))
        .thenReturn(batchResponse);

    BatchResponse result =
        batchProcessingService.processRecords("calendar", batchFeed, TestUtil.ORG_ID);
    assertEquals(batchResponse, result);

    Mockito.verify(calendarFeedHandlingService).processRecords(any(), any());
  }

  @Test
  void testProcessRecordsWithNodeCalendars() throws CommonServiceException {
    ResponseDto responseDto =
        testUtil.createResponseDto(1, 200, "Node Calendar created successfully");
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    BatchRequest<?> batchRequest = testUtil.getNodeCalendarFeedRequest(ActionEnum.CREATE);
    List<BatchRequest<?>> batchFeed = Collections.singletonList(batchRequest);

    Mockito.when(nodeCalendarFeedHandlingService.processRecords(any(), any()))
        .thenReturn(batchResponse);

    BatchResponse result =
        batchProcessingService.processRecords("node-calendar", batchFeed, TestUtil.ORG_ID);
    assertEquals(batchResponse, result);

    Mockito.verify(nodeCalendarFeedHandlingService).processRecords(any(), any());
  }

  @Test
  void testProcessRecordsWithCarrierServiceCalendars() throws CommonServiceException {
    ResponseDto responseDto =
        testUtil.createResponseDto(1, 200, "Carrier Service Calendar created successfully");
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    BatchRequest<?> batchRequest = testUtil.getCarrierServiceCalendarFeedRequest(ActionEnum.CREATE);
    List<BatchRequest<?>> batchFeed = Collections.singletonList(batchRequest);

    Mockito.when(carrierServiceCalendarFeedHandlingService.processRecords(any(), any()))
        .thenReturn(batchResponse);

    BatchResponse result =
        batchProcessingService.processRecords(
            "carrier-service-calendar", batchFeed, TestUtil.ORG_ID);
    assertEquals(batchResponse, result);

    Mockito.verify(carrierServiceCalendarFeedHandlingService).processRecords(any(), any());
  }

  @Test
  void testProcessRecordsWithPickupCalendars() throws CommonServiceException {
    ResponseDto responseDto =
        testUtil.createResponseDto(1, 200, "Pickup Calendar created successfully");
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    BatchRequest<?> batchRequest = testUtil.getPickupCalendarFeedRequest(ActionEnum.CREATE);
    List<BatchRequest<?>> batchFeed = Collections.singletonList(batchRequest);

    Mockito.when(pickupCalendarFeedHandlingService.processRecords(any(), any()))
        .thenReturn(batchResponse);

    BatchResponse result =
        batchProcessingService.processRecords("pickup-calendar", batchFeed, TestUtil.ORG_ID);
    assertEquals(batchResponse, result);

    Mockito.verify(pickupCalendarFeedHandlingService).processRecords(any(), any());
  }

  @Test
  void testProcessRecordsWithTransits() throws CommonServiceException {
    ResponseDto responseDto = testUtil.createResponseDto(1, 200, "Transit created successfully");
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    BatchRequest<?> batchRequest = testUtil.getTransitFeedRequest(ActionEnum.CREATE);
    List<BatchRequest<?>> batchFeed = Collections.singletonList(batchRequest);

    Mockito.when(transitFeedHandlingService.processRecords(any(), any())).thenReturn(batchResponse);

    BatchResponse result =
        batchProcessingService.processRecords("transit", batchFeed, TestUtil.ORG_ID);
    assertEquals(batchResponse, result);

    Mockito.verify(transitFeedHandlingService).processRecords(any(), any());
  }

  @Test
  void testProcessRecordsWithTransitBuffers() throws CommonServiceException {
    ResponseDto responseDto =
        testUtil.createResponseDto(1, 200, "Transit Buffer created successfully");
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    BatchRequest<?> batchRequest = testUtil.getTransitBufferFeedRequest(ActionEnum.CREATE);
    List<BatchRequest<?>> batchFeed = Collections.singletonList(batchRequest);

    Mockito.when(transitBufferFeedHandlingService.processRecords(any(), any()))
        .thenReturn(batchResponse);

    BatchResponse result =
        batchProcessingService.processRecords("transit-buffer", batchFeed, TestUtil.ORG_ID);
    assertEquals(batchResponse, result);

    Mockito.verify(transitBufferFeedHandlingService).processRecords(any(), any());
  }

  @Test
  void testProcessRecordsWithNodeCarriers() throws CommonServiceException {
    ResponseDto responseDto =
        testUtil.createResponseDto(1, 200, "Node carrier created successfully");
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    BatchRequest<?> batchRequest = testUtil.getNodeCarrierFeedRequest(ActionEnum.CREATE);
    List<BatchRequest<?>> batchFeed = Collections.singletonList(batchRequest);

    Mockito.when(nodeCarrierFeedHandlingService.processRecords(any(), any()))
        .thenReturn(batchResponse);

    BatchResponse result =
        batchProcessingService.processRecords("node-carrier", batchFeed, TestUtil.ORG_ID);
    assertEquals(batchResponse, result);

    Mockito.verify(nodeCarrierFeedHandlingService).processRecords(any(), any());
  }

  @Test
  void testProcessRecordsWithNodeServiceOptions() throws CommonServiceException {
    ResponseDto responseDto =
        testUtil.createResponseDto(1, 200, "Node service option created successfully");
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    BatchRequest<?> batchRequest = testUtil.getProcessingLeadTimeFeedRequest(ActionEnum.CREATE);
    List<BatchRequest<?>> batchFeed = Collections.singletonList(batchRequest);

    Mockito.when(nodeServiceOptionFeedHandlingService.processRecords(any(), any()))
        .thenReturn(batchResponse);

    BatchResponse result =
        batchProcessingService.processRecords("processing-lead-times", batchFeed, TestUtil.ORG_ID);
    assertEquals(batchResponse, result);

    Mockito.verify(nodeServiceOptionFeedHandlingService).processRecords(any(), any());
  }

  @Test
  void testProcessRecordsWithNodeServiceOptionBuffers() throws CommonServiceException {
    ResponseDto responseDto =
        testUtil.createResponseDto(1, 200, "Node service option buffer created successfully");
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    BatchRequest<?> batchRequest =
        testUtil.getNodeServiceOptionBufferFeedRequest(ActionEnum.CREATE);
    List<BatchRequest<?>> batchFeed = Collections.singletonList(batchRequest);

    Mockito.when(nodeServiceOptionBufferFeedHandlingService.processRecords(any(), any()))
        .thenReturn(batchResponse);

    BatchResponse result =
        batchProcessingService.processRecords(
            "node-service-option-buffer", batchFeed, TestUtil.ORG_ID);
    assertEquals(batchResponse, result);

    Mockito.verify(nodeServiceOptionBufferFeedHandlingService).processRecords(any(), any());
  }

  @Test
  void testProcessRecordsWithTransferSchedules() throws CommonServiceException {
    ResponseDto responseDto =
        testUtil.createResponseDto(1, 200, "Transfer Schedules created successfully");
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    BatchRequest<?> batchRequest = testUtil.getTransferScheduleFeedRequest(ActionEnum.CREATE);
    List<BatchRequest<?>> batchFeed = Collections.singletonList(batchRequest);

    Mockito.when(transferScheduleFeedHandlingService.processRecords(any(), any()))
        .thenReturn(batchResponse);

    BatchResponse result =
        batchProcessingService.processRecords("transfer-schedules", batchFeed, TestUtil.ORG_ID);
    assertEquals(batchResponse, result);

    Mockito.verify(transferScheduleFeedHandlingService).processRecords(any(), any());
  }

  @Test
  @DisplayName("Process records with vendor test")
  void testProcessRecordsWithVendor() throws CommonServiceException {
    ResponseDto responseDto = testUtil.createResponseDto(1, 200, "Vendor created successfully");
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    BatchRequest<?> batchRequest = testUtil.getVendorFeedRequest(ActionEnum.CREATE);
    List<BatchRequest<?>> batchFeed = Collections.singletonList(batchRequest);

    Mockito.when(vendorFeedHandlingService.processRecords(any(), any())).thenReturn(batchResponse);

    BatchResponse result =
        batchProcessingService.processRecords("vendor", batchFeed, TestUtil.ORG_ID);
    assertEquals(batchResponse, result);

    Mockito.verify(vendorFeedHandlingService).processRecords(any(), any());
  }

  @Test
  void testProcessRecordsWithInvalidModuleName() throws CommonServiceException {
    List<BatchRequest<?>> batchFeed = Collections.singletonList(new BatchRequest<>());
    Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () -> batchProcessingService.processRecords("zones", batchFeed, TestUtil.ORG_ID));
    Assertions.assertEquals("Invalid module name : zones", exception.getMessage());
  }
}
