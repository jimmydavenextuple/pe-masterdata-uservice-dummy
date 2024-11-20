/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.masterdata.calendar.controller;

import com.nextuple.calendar.domain.dto.CarrierCalendarCacheKeyDto;
import com.nextuple.calendar.domain.inbound.CarrierServiceCalendarRequest;
import com.nextuple.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.nextuple.calendar.persistence.exception.CalendarDomainException;
import com.nextuple.calendar.persistence.exception.CalenderServiceException;
import com.nextuple.calendar.persistence.exception.DateException;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.pe.masterdata.calendar.controller.docs.GetCarrierCalendarCacheKeysDoc;
import com.nextuple.pe.masterdata.calendar.controller.docs.GetCarrierCalendarsDoc;
import com.nextuple.pe.masterdata.calendar.controller.docs.HandleCreateCarrierServiceCalendarDoc;
import com.nextuple.pe.masterdata.calendar.controller.docs.HandleGetCarrierServiceCalenderDoc;
import com.nextuple.pe.masterdata.calendar.service.CarrierServiceCalendarService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@Tag(name = "Carrier Service Calendar APIs")
@RequestMapping("/carrier-service-calendar")
@RequiredArgsConstructor
public class CarrierServiceCalendarController {

  private static final Logger logger =
      LoggerFactory.getLogger(CarrierServiceCalendarController.class);
  private final CarrierServiceCalendarService carrierServiceCalendarService;

  @HandleCreateCarrierServiceCalendarDoc
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

  @HandleGetCarrierServiceCalenderDoc
  @GetMapping("/{orgId}/{carrierServiceId}")
  public ResponseEntity<BaseResponse<List<CarrierServiceCalendarResponse>>>
      handleGetCarrierServiceCalendar(
          @NotBlank(message = "orgId can't be empty")
              @PathVariable
              @Parameter(
                  description = "Unique identifier of the organization",
                  example = "NEXTUPLE")
              String orgId,
          @NotBlank(message = "carrierServiceId can't be empty")
              @PathVariable
              @Parameter(
                  description = "Unique identifier of the carrier service",
                  example = "ALL-SDND")
              String carrierServiceId,
          @RequestParam @Parameter(description = "Service option of the carrier")
              Optional<String> serviceOption,
          @RequestParam
              @Parameter(
                  description =
                      "Shipping stage of the carrier service. Shipping stage can be PICKUP, TRANSIT, DELIVERY, RECEIVING or ALL",
                  example = "PICKUP")
              Optional<String> shippingStage)
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

  @GetCarrierCalendarCacheKeysDoc
  @GetMapping("/get-all-cache-keys")
  public ResponseEntity<BaseResponse<List<CarrierCalendarCacheKeyDto>>> getCarrierCalendarCacheKeys(
      @RequestParam @Parameter(description = "Limit of cache keys", example = "20") Integer limit)
      throws CalendarDomainException {
    logger.debug("Processing get Carrier Calendar Cache Keys");

    var response = carrierServiceCalendarService.getAllCarrierCalendarCacheKeys(limit);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Carrier Calendar Cache Keys fetched successfully")
            .payload(response)
            .build());
  }

  @GetCarrierCalendarsDoc
  @GetMapping("/get-calendar-association/{calendarId}/{orgId}")
  public ResponseEntity<BaseResponse<List<CarrierServiceCalendarResponse>>> getCarrierCalendars(
      @NotBlank(message = "calendarId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier of the calendar", example = "CALENDAR2023")
          String calendarId,
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier of the organization", example = "NEXTUPLE")
          String orgId)
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
