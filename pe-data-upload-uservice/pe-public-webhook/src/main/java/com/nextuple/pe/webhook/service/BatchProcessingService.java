/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.webhook.service;

import static com.nextuple.master.data.integration.constants.TaskConstants.*;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.jobs.framework.common.enums.ModuleEnum;
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
import com.nextuple.pe.webhook.service.impl.TransitBufferFeedHandlingService;
import com.nextuple.pe.webhook.service.impl.TransitFeedHandlingService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BatchProcessingService {

  private static final Logger logger = LoggerFactory.getLogger(BatchProcessingService.class);
  private final CalendarFeedHandlingService calendarFeedHandlingService;
  private final NodeServiceOptionFeedHandlingService nodeServiceOptionFeedHandlingService;
  private final NodeServiceOptionBufferFeedHandlingService
      nodeServiceOptionBufferFeedHandlingService;
  private final NodeFeedHandlingService nodeFeedHandlingService;
  private final CarrierFeedHandlingService carrierFeedHandlingService;
  private final TransitFeedHandlingService transitFeedHandlingService;
  private final TransitBufferFeedHandlingService transitBufferFeedHandlingService;
  private final NodeCarrierFeedHandlingService nodeCarrierFeedHandlingService;
  private final NodeCalendarFeedHandlingService nodeCalendarFeedHandlingService;
  private final CarrierServiceCalendarFeedHandlingService carrierServiceCalendarFeedHandlingService;
  private final PickupCalendarFeedHandlingService pickupCalendarFeedHandlingService;

  public BatchResponse processRecords(
      String moduleName, List<BatchRequest<?>> batchFeed, String orgId)
      throws CommonServiceException {
    ModuleEnum moduleEnum = ModuleEnum.getModuleEnum(moduleName);
    switch (moduleEnum) {
      case NODES:
        return nodeFeedHandlingService.processRecords(batchFeed, orgId);
      case CARRIER_SERVICE:
        return carrierFeedHandlingService.processRecords(batchFeed, orgId);
      case CALENDAR:
        return calendarFeedHandlingService.processRecords(batchFeed, orgId);
      case NODE_CALENDAR:
        return nodeCalendarFeedHandlingService.processRecords(batchFeed, orgId);
      case CARRIER_SERVICE_CALENDAR:
        return carrierServiceCalendarFeedHandlingService.processRecords(batchFeed, orgId);
      case PICKUP_CALENDAR:
        return pickupCalendarFeedHandlingService.processRecords(batchFeed, orgId);
      case TRANSIT:
        return transitFeedHandlingService.processRecords(batchFeed, orgId);
      case TRANSIT_BUFFER:
        return transitBufferFeedHandlingService.processRecords(batchFeed, orgId);
      case NODE_CARRIER:
        return nodeCarrierFeedHandlingService.processRecords(batchFeed, orgId);
      case PROCESSING_LEAD_TIMES:
        return nodeServiceOptionFeedHandlingService.processRecords(batchFeed, orgId);
      case NODE_SERVICE_OPTION_BUFFER:
        return nodeServiceOptionBufferFeedHandlingService.processRecords(batchFeed, orgId);
      default:
        throw new CommonServiceException(
            "Invalid module name : " + moduleName, HttpStatus.BAD_REQUEST, 0x1771, null);
    }
  }
}
