package com.hbc.calendar.domain.feign;

import com.hbc.calendar.domain.inbound.CalendarRequest;
import com.hbc.calendar.domain.inbound.CarrierServiceCalendarRequest;
import com.hbc.calendar.domain.inbound.NodeCalendarRequest;
import com.hbc.calendar.domain.inbound.NodeCarrierServiceCalendarRequest;
import com.hbc.calendar.domain.outbound.CalendarResponse;
import com.hbc.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.hbc.calendar.domain.outbound.NodeCalendarResponse;
import com.hbc.calendar.domain.outbound.NodeCarrierServiceCalendarResponse;
import com.hbc.common.response.BaseResponse;
import javax.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "pe-config-calendar",
    url = "${spring.application.dependencies.calendar:http://pe-config-calendar:8080/}")
public interface CalendarFeign {
  @PostMapping("/calendar")
  BaseResponse<CalendarResponse> handleCreateCalendar(
      @Valid @RequestBody CalendarRequest calendarRequest);

  @PostMapping("/node-calendar")
  BaseResponse<NodeCalendarResponse> handleCreateNodeCalendar(
      @Valid @RequestBody NodeCalendarRequest nodeCalendarRequest);

  @PostMapping("/carrier-service-calendar")
  BaseResponse<CarrierServiceCalendarResponse> handleCreateCarrierServiceCalendar(
      @Valid @RequestBody CarrierServiceCalendarRequest carrierServiceCalendarRequest);

  @PostMapping("/node-carrier-service-calendar")
  BaseResponse<NodeCarrierServiceCalendarResponse> handleCreateNodeCarrierServiceCalendar(
      @Valid @RequestBody NodeCarrierServiceCalendarRequest nodeCarrierServiceCalendarRequest);
}
