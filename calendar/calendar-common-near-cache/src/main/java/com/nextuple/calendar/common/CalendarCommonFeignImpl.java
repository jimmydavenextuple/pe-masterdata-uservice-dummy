package com.nextuple.calendar.common;

import com.hbc.calendar.domain.CalendarDaysStatusInfo;
import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.service.GenericFeignService;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "pe-config-calendar",
    url = "${spring.application.dependencies.calendar:http://pe-config-calendar:8080/}")
public interface CalendarCommonFeignImpl
    extends GenericFeignService<String, BaseResponse<List<CalendarDaysStatusInfo>>> {

  @GetMapping("/{orgId}")
  BaseResponse<List<CalendarDaysStatusInfo>> get(@PathVariable String orgId);

  @GetMapping("/calendar/status/{orgId}")
  BaseResponse<List<CalendarDaysStatusInfo>> getNodeCalendar(
      @PathVariable("orgId") String orgId, @RequestParam("nodeId") String nodeId);

  @GetMapping("/calendar/status/{orgId}")
  BaseResponse<List<CalendarDaysStatusInfo>> getCarrierServiceCalendar(
      @PathVariable("orgId") String orgId,
      @RequestParam("carrierServiceId") String carrierServiceId,
      @RequestParam("serviceOption") String serviceOption);

  @GetMapping("calendar/status/{orgId}")
  BaseResponse<List<CalendarDaysStatusInfo>> getNodeCarrierCalendar(
      @PathVariable("orgId") String orgId,
      @PathVariable("nodeId") String nodeId,
      @RequestParam("carrierServiceId") String carrierServiceId,
      @RequestParam("serviceOption") String serviceOption);
}
