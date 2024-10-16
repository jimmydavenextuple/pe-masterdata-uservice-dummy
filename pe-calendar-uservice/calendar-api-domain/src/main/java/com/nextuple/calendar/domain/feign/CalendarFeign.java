/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.calendar.domain.feign;

import com.nextuple.calendar.domain.dto.CarrierCalendarCacheKeyDto;
import com.nextuple.calendar.domain.dto.NodeCalendarCacheKeyDto;
import com.nextuple.calendar.domain.dto.NodeCarrierCalendarCacheKeyDto;
import com.nextuple.calendar.domain.inbound.CalendarRequest;
import com.nextuple.calendar.domain.inbound.CarrierServiceCalendarRequest;
import com.nextuple.calendar.domain.inbound.NodeCalendarRequest;
import com.nextuple.calendar.domain.inbound.NodeCarrierServiceCalendarRequest;
import com.nextuple.calendar.domain.outbound.CalendarResponse;
import com.nextuple.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.nextuple.calendar.domain.outbound.NodeCalendarResponse;
import com.nextuple.calendar.domain.outbound.NodeCarrierServiceCalendarResponse;
import com.nextuple.common.base.PagePayload;
import com.nextuple.common.response.BaseResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "pe-calendar-uservice",
    url = "${spring.application.dependencies.calendar:http://pe-calendar-uservice:8080/}")
public interface CalendarFeign {
  @PostMapping("/calendar")
  BaseResponse<CalendarResponse> createCalendar(
      @Valid @RequestBody CalendarRequest calendarRequest);

  @GetMapping("/calendar/{orgId}/{calendarId}")
  BaseResponse<CalendarResponse> getCalendar(
      @PathVariable String orgId, @PathVariable String calendarId);

  @PostMapping("/node-calendar")
  BaseResponse<NodeCalendarResponse> createNodeCalendar(
      @Valid @RequestBody NodeCalendarRequest nodeCalendarRequest);

  @PostMapping("/carrier-service-calendar")
  BaseResponse<CarrierServiceCalendarResponse> createCarrierServiceCalendar(
      @Valid @RequestBody CarrierServiceCalendarRequest carrierServiceCalendarRequest);

  @PostMapping("/node-carrier-service-calendar")
  BaseResponse<NodeCarrierServiceCalendarResponse> createNodeCarrierServiceCalendar(
      @Valid @RequestBody NodeCarrierServiceCalendarRequest nodeCarrierServiceCalendarRequest);

  @GetMapping("/carrier-service-calendar/{orgId}/{carrierServiceId}")
  BaseResponse<List<CarrierServiceCalendarResponse>> getCarrierServiceCalendar(
      @PathVariable String orgId, @PathVariable String carrierServiceId);

  @GetMapping("/node-calendar/get-all-cache-keys")
  BaseResponse<List<NodeCalendarCacheKeyDto>> getNodeCalendarCacheKeys(
      @NotNull @RequestParam Integer limit);

  @GetMapping("/carrier-service-calendar/get-all-cache-keys")
  BaseResponse<List<CarrierCalendarCacheKeyDto>> getCarrierCalendarCacheKeys(
      @NotNull @RequestParam Integer limit);

  @GetMapping("/node-carrier-service-calendar/get-all-cache-keys")
  BaseResponse<List<NodeCarrierCalendarCacheKeyDto>> getNodeCarrierCalendarCacheKeys(
      @NotNull @RequestParam Integer limit);

  @GetMapping("/node-calendar/get-calendar-association/{calendarId}/{orgId}")
  BaseResponse<List<NodeCalendarResponse>> getNodeCalendars(
      @PathVariable String calendarId, @PathVariable String orgId);

  @GetMapping("/carrier-service-calendar/get-calendar-association/{calendarId}/{orgId}")
  BaseResponse<List<CarrierServiceCalendarResponse>> getCarrierCalendars(
      @PathVariable String calendarId, @PathVariable String orgId);

  @GetMapping("/calendar/{orgId}")
  BaseResponse<PagePayload<CalendarResponse>> getCalendarListWithPagination(
      @PathVariable String orgId,
      @RequestParam(required = false) Integer pageNo,
      @RequestParam(required = false) Integer pageSize,
      @RequestParam(required = false) String sortBy,
      @RequestParam(required = false) String sortOrder);

  @GetMapping("/node-carrier-service-calendar/{orgId}/{nodeId}/{carrierServiceId}")
  BaseResponse<List<NodeCarrierServiceCalendarResponse>> getNodeCarrierServiceCalendar(
      @PathVariable String orgId,
      @PathVariable String nodeId,
      @PathVariable String carrierServiceId,
      @RequestParam(required = false) String serviceOption);

  @GetMapping("/node-calendar/{orgId}/{nodeId}")
  BaseResponse<List<NodeCalendarResponse>> handleGetNodeCalendar(
      @PathVariable String orgId, @PathVariable String nodeId);
}
