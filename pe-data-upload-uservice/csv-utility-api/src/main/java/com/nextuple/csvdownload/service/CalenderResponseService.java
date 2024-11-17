/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.service;

import com.nextuple.calendar.domain.feign.CalendarFeign;
import com.nextuple.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.nextuple.calendar.domain.outbound.NodeCalendarResponse;
import com.nextuple.calendar.domain.outbound.NodeCarrierServiceCalendarResponse;
import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.csvdownload.exception.CarrierServiceException;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class CalenderResponseService {

  private final CalendarFeign calendarFeign;

  private final Logger logger = LoggerFactory.getLogger(CalenderResponseService.class);

  public List<CarrierServiceCalendarResponse> getCarrierServiceCalender(
      String orgId, String carrierServiceId) throws CarrierServiceException {
    logger.debug("Processing get Carrier service by orgId");

    BaseResponse<List<CarrierServiceCalendarResponse>> response =
        calendarFeign.getCarrierServiceCalendar(orgId, carrierServiceId);
    if (response != null && !CollectionUtils.isEmpty(response.getPayload())) {
      return response.getPayload();
    } else {
      logger.error("Carrier Service Calender does not exist for given orgId");
      throw new CarrierServiceException(
          "Carrier Service Calender does not exist for given orgId", orgId);
    }
  }

  public List<NodeCarrierServiceCalendarResponse> getNodeCarrierServiceCalender(String orgId)
      throws CarrierServiceException {
    logger.debug("Processing get Carrier service by orgId");
    BaseResponse<List<NodeCarrierServiceCalendarResponse>> response =
        calendarFeign.getAllNodeCarrierServiceCalendar(orgId);
    if (response != null && !CollectionUtils.isEmpty(response.getPayload())) {
      return response.getPayload();
    } else {
      return Collections.emptyList();
    }
  }

  public List<NodeCalendarResponse> getNodeCalendar(String orgId, String nodeId) {
    logger.debug("Processing get node calendar by orgId and nodeId");

    BaseResponse<List<NodeCalendarResponse>> response =
        calendarFeign.handleGetNodeCalendar(orgId, nodeId);

    return response.getPayload();
  }
}
