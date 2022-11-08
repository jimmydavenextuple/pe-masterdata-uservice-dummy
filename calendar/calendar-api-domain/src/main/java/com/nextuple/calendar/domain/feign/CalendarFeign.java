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
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "pe-config-calendar",
    url = "${spring.application.dependencies.calendar:http://pe-config-calendar:8080/}")
public interface CalendarFeign {
  @PostMapping("/calendar")
  BaseResponse<CalendarResponse> createCalendar(
      @Valid @RequestBody CalendarRequest calendarRequest);

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
