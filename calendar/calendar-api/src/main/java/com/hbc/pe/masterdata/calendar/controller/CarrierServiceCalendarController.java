package com.hbc.pe.masterdata.calendar.controller;

import com.hbc.calendar.domain.inbound.CarrierServiceCalendarRequest;
import com.hbc.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.hbc.common.response.BaseResponse;
import com.hbc.pe.masterdata.calendar.exception.CalendarDomainException;
import com.hbc.pe.masterdata.calendar.exception.DateException;
import com.hbc.pe.masterdata.calendar.service.CarrierServiceCalendarService;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carrier-service-calendar")
@RequiredArgsConstructor
public class CarrierServiceCalendarController {

  private static final Logger logger =
      LoggerFactory.getLogger(CarrierServiceCalendarController.class);
  private final CarrierServiceCalendarService carrierServiceCalendarService;

  @PostMapping
  public ResponseEntity<BaseResponse<CarrierServiceCalendarResponse>>
      handleCreateCarrierServiceCalendar(
          @Valid @RequestBody CarrierServiceCalendarRequest carrierServiceCalendarRequest)
          throws CalendarDomainException, DateException {
    logger.debug(
        "Inside handleCreateCarrierServiceCalendar() for carrierServiceCalendarRequest: {}",
        carrierServiceCalendarRequest);
    try {
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Carrier service calendar created successfully!")
              .payload(
                  carrierServiceCalendarService.processCreateCarrierServiceCalendar(
                      carrierServiceCalendarRequest))
              .build());
    } catch (Exception e) {
      logger.error("Error in handleCreateCarrierServiceCalendar()");
      throw e;
    }
  }

  @GetMapping("/{orgId}/{carrierServiceId}")
  public ResponseEntity<BaseResponse<List<CarrierServiceCalendarResponse>>>
      handleGetCarrierServiceCalendar(
          @PathVariable String orgId,
          @PathVariable String carrierServiceId,
          @RequestParam Optional<String> serviceOption,
          @RequestParam Optional<String> shippingStage)
          throws CalendarDomainException {
    try {
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Carrier&Service calendar details fetched successfully!")
              .payload(
                  carrierServiceCalendarService.processGetCarrierServiceCalendar(
                      orgId, carrierServiceId, serviceOption, shippingStage))
              .build());
    } catch (Exception e) {
      logger.error("Error in handleGetCarrierServiceCalendar()");
      throw e;
    }
  }
}
