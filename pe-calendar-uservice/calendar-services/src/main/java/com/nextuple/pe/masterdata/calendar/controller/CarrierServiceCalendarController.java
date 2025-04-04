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

/**
 * Controller for managing Carrier Service Calendar configurations and related operations.
 *
 * <p>This class provides endpoints for creating, retrieving, and managing carrier service
 * calendars, including fetching calendar cache keys and retrieving calendar associations with
 * carrier services. It supports operations such as creating a carrier service calendar, getting
 * details by organization and carrier service IDs, and handling pagination for calendar cache keys.
 *
 * <p>The controller is tagged with "Carrier Service Calendar APIs" for easy categorization in API
 * documentation.
 */
@Validated
@RestController
@Tag(name = "Carrier Service Calendar APIs")
@RequestMapping("/carrier-service-calendar")
@RequiredArgsConstructor
public class CarrierServiceCalendarController {

  private static final Logger logger =
      LoggerFactory.getLogger(CarrierServiceCalendarController.class);
  private final CarrierServiceCalendarService carrierServiceCalendarService;

  /**
   * Creates a new carrier service calendar based on the provided request.
   *
   * <p>This method processes a POST request to create a new carrier service calendar. The {@link
   * CarrierServiceCalendarRequest} contains the necessary data to create the calendar, and the
   * response includes the {@link CarrierServiceCalendarResponse} after the calendar has been
   * successfully created.
   *
   * @param carrierServiceCalendarRequest The request object containing the details for the new
   *     carrier service calendar.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the {@link
   *     CarrierServiceCalendarResponse} object representing the created carrier service calendar.
   * @throws CalendarDomainException If there is an error related to the calendar creation domain.
   * @throws DateException If there is an error related to date validation or formatting.
   * @throws CommonServiceException If there is a generic error during the process.
   */
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

  /**
   * Retrieves the carrier service calendar details based on the provided organization and carrier
   * service IDs.
   *
   * <p>This method processes a GET request to fetch the calendar details for a specific carrier
   * service. The {@link CarrierServiceCalendarResponse} will contain the relevant information for
   * the calendar, based on the provided organization ID and the carrier service ID.
   *
   * @param orgId The unique identifier of the organization.
   * @param carrierServiceId The unique identifier of the carrier service.
   * @param serviceOption An optional parameter specifying the service option of the carrier.
   * @param shippingStage An optional parameter specifying the shipping stage of the carrier
   *     service. The valid stages are PICKUP, TRANSIT, DELIVERY, RECEIVING, or ALL.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a list of {@link
   *     CarrierServiceCalendarResponse} representing the details of the carrier service calendar.
   * @throws CalendarDomainException If there is an error related to the calendar domain.
   * @throws CalenderServiceException If there is an error processing the carrier service calendar.
   */
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

  /**
   * Retrieves the carrier calendar cache keys based on the specified limit.
   *
   * <p>This method processes a GET request to fetch the list of carrier calendar cache keys, with
   * an optional limit parameter to restrict the number of keys returned. The {@link
   * CarrierCalendarCacheKeyDto} will contain the relevant data for each cache key.
   *
   * @param limit The maximum number of cache keys to be retrieved. This parameter limits the number
   *     of results returned, for example, "20".
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a list of {@link
   *     CarrierCalendarCacheKeyDto} representing the carrier calendar cache keys.
   * @throws CalendarDomainException If there is an error related to the calendar domain.
   */
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

  /**
   * Retrieves the list of carrier service calendars associated with the specified calendar ID and
   * organization ID.
   *
   * <p>This method processes a GET request to fetch the list of carrier service calendars that are
   * associated with a given calendar and organization. The response contains a list of {@link
   * CarrierServiceCalendarResponse} that provide details about the association between the carrier
   * service and the calendar.
   *
   * @param calendarId The unique identifier of the calendar. For example, "CALENDAR2023".
   * @param orgId The unique identifier of the organization. For example, "NEXTUPLE".
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a list of {@link
   *     CarrierServiceCalendarResponse} representing the carrier service calendars associated with
   *     the specified calendar and organization.
   * @throws CalendarDomainException If there is an error related to the calendar domain.
   */
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
