/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.webhook.service;

import static org.mockito.ArgumentMatchers.any;

import com.nextuple.common.enums.ActionEnum;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.pe.webhook.domain.dtos.MasterDataIngestionDto;
import com.nextuple.pe.webhook.domain.inbound.FeedRequest;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class MasterDataIngestionServiceTest {
  @Mock CarrierFeedHandlingService carrierFeedHandlingService;
  @Mock NodeFeedHandlingService nodeFeedHandlingService;
  @Mock CalendarFeedHandlingService calendarFeedHandlingService;
  @Mock NodeCalendarFeedHandlingService nodeCalendarFeedHandlingService;
  @Mock CarrierServiceCalendarFeedHandlingService carrierServiceCalendarFeedHandlingService;
  @Mock PickupCalendarFeedHandlingService pickupCalendarFeedHandlingService;
  @Mock TransitFeedHandlingService transitFeedHandlingService;
  @Mock TransitBufferFeedHandlingService transitBufferFeedHandlingService;
  @Mock NodeCarrierFeedHandlingService nodeCarrierFeedHandlingService;
  @Mock NodeServiceOptionFeedHandlingService nodeServiceOptionFeedHandlingService;
  @Mock NodeServiceOptionBufferFeedHandlingService nodeServiceOptionBufferFeedHandlingService;
  @Mock TransferScheduleFeedHandlingService transferScheduleFeedHandlingService;
  @Mock VendorFeedHandlingService vendorFeedHandlingService;
  @InjectMocks MasterDataIngestionService masterDataIngestionService;
  @InjectMocks TestUtil testUtil;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void ingestNodeFeedTest() throws CommonServiceException {
    FeedRequest<MasterDataIngestionDto<?>> batchRequest =
        testUtil.getNodeFeedIngestionRequest(ActionEnum.CREATE);
    Mockito.doNothing().when(nodeFeedHandlingService).publishRecords(any(), any());

    masterDataIngestionService.processMasterDataIngestionData(
        "nodes", batchRequest, TestUtil.ORG_ID);
    Mockito.verify(nodeFeedHandlingService, Mockito.times(1)).publishRecords(any(), any());
  }

  @Test
  void ingestCarrierFeedTest() throws CommonServiceException {
    FeedRequest<MasterDataIngestionDto<?>> batchRequest =
        testUtil.getCarrierFeedIngestionRequest(ActionEnum.CREATE);
    Mockito.doNothing().when(carrierFeedHandlingService).publishRecords(any(), any());

    masterDataIngestionService.processMasterDataIngestionData(
        "carrier-service", batchRequest, TestUtil.ORG_ID);
    Mockito.verify(carrierFeedHandlingService, Mockito.times(1)).publishRecords(any(), any());
  }

  @Test
  void ingestCalendarFeedTest() throws CommonServiceException {
    FeedRequest<MasterDataIngestionDto<?>> batchRequest =
        testUtil.getCalendarFeedIngestionRequest(ActionEnum.CREATE);
    Mockito.doNothing().when(calendarFeedHandlingService).publishRecords(any(), any());

    masterDataIngestionService.processMasterDataIngestionData(
        "calendar", batchRequest, TestUtil.ORG_ID);
    Mockito.verify(calendarFeedHandlingService, Mockito.times(1)).publishRecords(any(), any());
  }

  @Test
  void ingestCarrierServiceCalendarFeedTest() throws CommonServiceException {
    FeedRequest<MasterDataIngestionDto<?>> batchRequest =
        testUtil.getCarrierServiceCalendarFeedIngestionRequest(ActionEnum.CREATE);
    Mockito.doNothing()
        .when(carrierServiceCalendarFeedHandlingService)
        .publishRecords(any(), any());

    masterDataIngestionService.processMasterDataIngestionData(
        "carrier-service-calendar", batchRequest, TestUtil.ORG_ID);
    Mockito.verify(carrierServiceCalendarFeedHandlingService, Mockito.times(1))
        .publishRecords(any(), any());
  }

  @Test
  void ingestNodeCalendarFeedTest() throws CommonServiceException {
    FeedRequest<MasterDataIngestionDto<?>> batchRequest =
        testUtil.getNodeCalendarFeedIngestionRequest(ActionEnum.CREATE);
    Mockito.doNothing().when(nodeCalendarFeedHandlingService).publishRecords(any(), any());

    masterDataIngestionService.processMasterDataIngestionData(
        "node-calendar", batchRequest, TestUtil.ORG_ID);
    Mockito.verify(nodeCalendarFeedHandlingService, Mockito.times(1)).publishRecords(any(), any());
  }

  @Test
  void ingestPickupCalendarFeedTest() throws CommonServiceException {
    FeedRequest<MasterDataIngestionDto<?>> batchRequest =
        testUtil.getPickupCalendarFeedIngestionRequest(ActionEnum.CREATE);
    Mockito.doNothing().when(pickupCalendarFeedHandlingService).publishRecords(any(), any());

    masterDataIngestionService.processMasterDataIngestionData(
        "pickup-calendar", batchRequest, TestUtil.ORG_ID);
    Mockito.verify(pickupCalendarFeedHandlingService, Mockito.times(1))
        .publishRecords(any(), any());
  }

  @Test
  void ingestTransitFeedTest() throws CommonServiceException {
    FeedRequest<MasterDataIngestionDto<?>> batchRequest =
        testUtil.getTransitFeedIngestionRequest(ActionEnum.CREATE);
    Mockito.doNothing().when(transitFeedHandlingService).publishRecords(any(), any());

    masterDataIngestionService.processMasterDataIngestionData(
        "transit", batchRequest, TestUtil.ORG_ID);
    Mockito.verify(transitFeedHandlingService, Mockito.times(1)).publishRecords(any(), any());
  }

  @Test
  void ingestTransitBufferFeedTest() throws CommonServiceException {
    FeedRequest<MasterDataIngestionDto<?>> batchRequest =
        testUtil.getTransitBufferFeedIngestionRequest(ActionEnum.CREATE);
    Mockito.doNothing().when(transitBufferFeedHandlingService).publishRecords(any(), any());

    masterDataIngestionService.processMasterDataIngestionData(
        "transit-buffer", batchRequest, TestUtil.ORG_ID);
    Mockito.verify(transitBufferFeedHandlingService, Mockito.times(1)).publishRecords(any(), any());
  }

  @Test
  void ingestNodeCarrierFeedTest() throws CommonServiceException {
    FeedRequest<MasterDataIngestionDto<?>> batchRequest =
        testUtil.getNodeCarrierFeedIngestionRequest(ActionEnum.CREATE);
    Mockito.doNothing().when(nodeCarrierFeedHandlingService).publishRecords(any(), any());

    masterDataIngestionService.processMasterDataIngestionData(
        "node-carrier", batchRequest, TestUtil.ORG_ID);
    Mockito.verify(nodeCarrierFeedHandlingService, Mockito.times(1)).publishRecords(any(), any());
  }

  @Test
  void ingestNodeServiceOptionFeedTest() throws CommonServiceException {
    FeedRequest<MasterDataIngestionDto<?>> batchRequest =
        testUtil.getNodeServiceOptionFeedIngestionRequest(ActionEnum.CREATE);
    Mockito.doNothing().when(nodeServiceOptionFeedHandlingService).publishRecords(any(), any());

    masterDataIngestionService.processMasterDataIngestionData(
        "processing-lead-times", batchRequest, TestUtil.ORG_ID);
    Mockito.verify(nodeServiceOptionFeedHandlingService, Mockito.times(1))
        .publishRecords(any(), any());
  }

  @Test
  void ingestNodeServiceOptionBufferFeedTest() throws CommonServiceException {
    FeedRequest<MasterDataIngestionDto<?>> batchRequest =
        testUtil.getNodeServiceOptionBufferFeedIngestionRequest(ActionEnum.CREATE);
    Mockito.doNothing()
        .when(nodeServiceOptionBufferFeedHandlingService)
        .publishRecords(any(), any());

    masterDataIngestionService.processMasterDataIngestionData(
        "node-service-option-buffer", batchRequest, TestUtil.ORG_ID);
    Mockito.verify(nodeServiceOptionBufferFeedHandlingService, Mockito.times(1))
        .publishRecords(any(), any());
  }

  @Test
  void invalidModuleNamePassedWhileIngestionTest() {
    FeedRequest<MasterDataIngestionDto<?>> batchRequest =
        testUtil.getCarrierFeedIngestionRequest(ActionEnum.CREATE);
    Mockito.doNothing().when(carrierFeedHandlingService).publishRecords(any(), any());

    Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () ->
                masterDataIngestionService.processMasterDataIngestionData(
                    "zones", batchRequest, TestUtil.ORG_ID));
    Assertions.assertEquals("Invalid module name : zones", exception.getMessage());
    Mockito.verify(carrierFeedHandlingService, Mockito.times(0)).publishRecords(any(), any());
  }

  @Test
  void ingestTransferScheduledFeedTest() throws CommonServiceException {
    FeedRequest<MasterDataIngestionDto<?>> batchRequest =
        testUtil.getNodeCalendarFeedIngestionRequest(ActionEnum.CREATE);
    Mockito.doNothing().when(transferScheduleFeedHandlingService).publishRecords(any(), any());

    masterDataIngestionService.processMasterDataIngestionData(
        "transfer-schedules", batchRequest, TestUtil.ORG_ID);
    Mockito.verify(transferScheduleFeedHandlingService, Mockito.times(1))
        .publishRecords(any(), any());
  }

  @Test
  @DisplayName("Ingest Vendor Feed Test")
  void ingestVendorFeedTest() throws CommonServiceException {
    FeedRequest<MasterDataIngestionDto<?>> batchRequest =
        testUtil.getVendorFeedIngestionRequest(ActionEnum.CREATE);
    Mockito.doNothing().when(vendorFeedHandlingService).publishRecords(any(), any());

    masterDataIngestionService.processMasterDataIngestionData(
        "vendor", batchRequest, TestUtil.ORG_ID);
    Mockito.verify(vendorFeedHandlingService, Mockito.times(1)).publishRecords(any(), any());
  }
}
