package com.hbc.pe.masterdata.calendar.controller;

import com.hbc.calendar.domain.dto.CarrierCalendarCacheKeyDto;
import com.hbc.calendar.domain.inbound.CarrierServiceCalendarRequest;
import com.hbc.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.pe.masterdata.calendar.exception.CalendarDomainException;
import com.hbc.pe.masterdata.calendar.exception.CalenderServiceException;
import com.hbc.pe.masterdata.calendar.exception.DateException;
import com.hbc.pe.masterdata.calendar.service.CarrierServiceCalendarService;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
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
          throws CalendarDomainException, CommonServiceException, DateException {

    logger.debug(
        "Inside  handleCreateCarrierServiceCalendar() for carrierServiceCalendarRequest: {}",
        carrierServiceCalendarRequest);
    try {
      var calendarResponse =
          carrierServiceCalendarService.processCreateCarrierServiceCalendar(
              carrierServiceCalendarRequest);
      logger.info("Response after creation of carrier service calendar:{}", calendarResponse);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Carrier service calendar created successfully!")
              .payload(calendarResponse)
              .build());
    } catch (Exception e) {
      logger.error("Error in handleCreateCarrierServiceCalendar()");
      throw e;
    }
  }

  @GetMapping("/{orgId}/{carrierServiceId}")
  public ResponseEntity<BaseResponse<List<CarrierServiceCalendarResponse>>>
      handleGetCarrierServiceCalendar(
          @NotBlank(message = "orgId can't be empty") @PathVariable String orgId,
          @NotBlank(message = "carrierServiceId can't be empty") @PathVariable
              String carrierServiceId,
          @RequestParam Optional<String> serviceOption,
          @RequestParam Optional<String> shippingStage)
          throws CalendarDomainException, CalenderServiceException {
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

  @GetMapping("/get-all-cache-keys")
  public ResponseEntity<BaseResponse<List<CarrierCalendarCacheKeyDto>>> getCarrierCalendarCacheKeys(
      @RequestParam Integer limit) throws CalendarDomainException {
    logger.debug("Processing get Carrier Calendar Cache Keys");

    var response = carrierServiceCalendarService.getAllCarrierCalendarCacheKeys(limit);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Carrier Calendar Cache Keys fetched successfully")
            .payload(response)
            .build());
  }

  @GetMapping("/get-calendar-association/{calendarId}/{orgId}")
  public ResponseEntity<BaseResponse<List<CarrierServiceCalendarResponse>>> getCarrierCalendars(
      @NotBlank(message = "calendarId can't be empty") @PathVariable String calendarId,
      @NotBlank(message = "orgId can't be empty") @PathVariable String orgId)
      throws CalendarDomainException {
    logger.debug("Processing get Carrier Calendars by orgId and calendarId");

    var response =
        carrierServiceCalendarService.getCarrierServiceAssociationWithCalendar(calendarId, orgId);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Carrier Calendar List fetched successfully")
            .payload(response)
            .build());
  }
}
