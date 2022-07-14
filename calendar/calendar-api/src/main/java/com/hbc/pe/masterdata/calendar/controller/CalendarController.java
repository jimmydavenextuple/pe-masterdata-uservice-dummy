package com.hbc.pe.masterdata.calendar.controller;

import com.hbc.calendar.domain.CalendarDaysStatusInfo;
import com.hbc.calendar.domain.inbound.CalendarRequest;
import com.hbc.calendar.domain.outbound.CalendarResponse;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.pe.masterdata.calendar.exception.CalendarDomainException;
import com.hbc.pe.masterdata.calendar.service.CalendarService;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/calendar")
@RequiredArgsConstructor
public class CalendarController {

  private static final Logger logger = LoggerFactory.getLogger(CalendarController.class);

  @Autowired CalendarService calendarService;

  @PostMapping
  public ResponseEntity<BaseResponse<CalendarResponse>> handleCreateCalendar(
      @Valid @RequestBody CalendarRequest calendarRequest) throws CalendarDomainException {
    logger.debug("Inside handleCreateCalendar() for calendarRequest: {}", calendarRequest);
    try {
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Calendar created successfully!")
              .payload(calendarService.processCreateCalendar(calendarRequest))
              .build());
    } catch (Exception e) {
      logger.error("Error in handleCreateCalendar()");
      throw e;
    }
  }

  @GetMapping("/{orgId}/{calendarId}")
  public ResponseEntity<BaseResponse<CalendarResponse>> handleGetCalendar(
      @PathVariable String orgId, @PathVariable String calendarId)
      throws CalendarDomainException, CommonServiceException {
    try {
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Calendar details fetched successfully!")
              .payload(calendarService.processGetCalendar(orgId, calendarId))
              .build());
    } catch (Exception e) {
      logger.error("Error in handleGetCalendar()");
      throw e;
    }
  }

  @GetMapping("/status/{orgId}")
  public ResponseEntity<BaseResponse<List<CalendarDaysStatusInfo>>>
      handleGetUpcomingDaysCalendarStatus(
          @PathVariable String orgId,
          @RequestParam Optional<String> nodeId,
          @RequestParam Optional<String> carrierServiceId,
          @RequestParam Optional<String> serviceOption,
          @RequestParam Optional<Integer> numberOfDaysInFuture,
          @RequestParam Optional<String> shippingStage)
          throws CalendarDomainException, CommonServiceException {
    try {
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Calendar status for upcoming days fetched successfully!")
              .payload(
                  calendarService.processGetUpcomingDaysCalendarStatus(
                      orgId,
                      nodeId,
                      carrierServiceId,
                      serviceOption,
                      numberOfDaysInFuture,
                      shippingStage))
              .build());
    } catch (Exception e) {
      logger.error("Error in handleGetUpcomingDaysCalendarStatus()");
      throw e;
    }
  }
}
