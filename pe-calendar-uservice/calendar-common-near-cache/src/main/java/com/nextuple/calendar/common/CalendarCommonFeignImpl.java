/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.calendar.common;

import com.nextuple.calendar.domain.CalendarDaysStatusInfo;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.service.GenericFeignService;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "pe-calendar-uservice",
    url = "${spring.application.dependencies.calendar:http://pe-calendar-uservice:8080/}")
public interface CalendarCommonFeignImpl
    extends GenericFeignService<String, BaseResponse<List<CalendarDaysStatusInfo>>> {

  @GetMapping("/{orgId}")
  BaseResponse<List<CalendarDaysStatusInfo>> get(@PathVariable String orgId);

  @GetMapping("/calendar/status/{orgId}")
  BaseResponse<List<CalendarDaysStatusInfo>> getNodeCalendar(
      @PathVariable String orgId, @RequestParam String nodeId, @RequestParam String fromDate);

  @GetMapping("/calendar/status/{orgId}")
  BaseResponse<List<CalendarDaysStatusInfo>> getCarrierServiceCalendar(
      @PathVariable String orgId,
      @RequestParam String carrierServiceId,
      @RequestParam String serviceOption,
      @RequestParam String fromDate);

  @GetMapping("calendar/status/{orgId}")
  BaseResponse<List<CalendarDaysStatusInfo>> getNodeCarrierCalendar(
      @PathVariable String orgId,
      @RequestParam String nodeId,
      @RequestParam String carrierServiceId,
      @RequestParam String serviceOption,
      @RequestParam String fromDate);
}
