/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.webhook.service;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.jobs.framework.common.enums.ModuleEnum;
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
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MasterDataIngestionService {

  private static final Logger logger = LoggerFactory.getLogger(MasterDataIngestionService.class);
  private final NodeFeedHandlingService nodeFeedHandlingService;
  private final CarrierFeedHandlingService carrierFeedHandlingService;
  private final CalendarFeedHandlingService calendarFeedHandlingService;
  private final NodeCalendarFeedHandlingService nodeCalendarFeedHandlingService;
  private final CarrierServiceCalendarFeedHandlingService carrierServiceCalendarFeedHandlingService;
  private final PickupCalendarFeedHandlingService pickupCalendarFeedHandlingService;
  private final TransitFeedHandlingService transitFeedHandlingService;
  private final TransitBufferFeedHandlingService transitBufferFeedHandlingService;
  private final NodeCarrierFeedHandlingService nodeCarrierFeedHandlingService;
  private final NodeServiceOptionFeedHandlingService nodeServiceOptionFeedHandlingService;
  private final NodeServiceOptionBufferFeedHandlingService
      nodeServiceOptionBufferFeedHandlingService;
  private final TransferScheduleFeedHandlingService transferScheduleFeedHandlingService;
  private final VendorFeedHandlingService vendorFeedHandlingService;

  public void processMasterDataIngestionData(
      String moduleName,
      FeedRequest<MasterDataIngestionDto<?>> masterDataIngestionFeedRequest,
      String orgId)
      throws CommonServiceException {
    ModuleEnum moduleEnum = ModuleEnum.getModuleEnum(moduleName);
    switch (moduleEnum) {
      case NODES:
        nodeFeedHandlingService.publishRecords(masterDataIngestionFeedRequest, orgId);
        break;
      case CARRIER_SERVICE:
        carrierFeedHandlingService.publishRecords(masterDataIngestionFeedRequest, orgId);
        break;
      case CALENDAR:
        calendarFeedHandlingService.publishRecords(masterDataIngestionFeedRequest, orgId);
        break;
      case NODE_CALENDAR:
        nodeCalendarFeedHandlingService.publishRecords(masterDataIngestionFeedRequest, orgId);
        break;
      case CARRIER_SERVICE_CALENDAR:
        carrierServiceCalendarFeedHandlingService.publishRecords(
            masterDataIngestionFeedRequest, orgId);
        break;
      case PICKUP_CALENDAR:
        pickupCalendarFeedHandlingService.publishRecords(masterDataIngestionFeedRequest, orgId);
        break;
      case TRANSIT:
        transitFeedHandlingService.publishRecords(masterDataIngestionFeedRequest, orgId);
        break;
      case TRANSIT_BUFFER:
        transitBufferFeedHandlingService.publishRecords(masterDataIngestionFeedRequest, orgId);
        break;
      case NODE_CARRIER:
        nodeCarrierFeedHandlingService.publishRecords(masterDataIngestionFeedRequest, orgId);
        break;
      case PROCESSING_LEAD_TIMES:
        nodeServiceOptionFeedHandlingService.publishRecords(masterDataIngestionFeedRequest, orgId);
        break;
      case NODE_SERVICE_OPTION_BUFFER:
        nodeServiceOptionBufferFeedHandlingService.publishRecords(
            masterDataIngestionFeedRequest, orgId);
        break;
      case TRANSFER_SCHEDULES:
        transferScheduleFeedHandlingService.publishRecords(masterDataIngestionFeedRequest, orgId);
        break;
      case VENDOR:
        vendorFeedHandlingService.publishRecords(masterDataIngestionFeedRequest, orgId);
        break;
      default:
        throw new CommonServiceException(
            "Invalid module name : " + moduleName, HttpStatus.BAD_REQUEST, 0x1771, null);
    }
  }
}
