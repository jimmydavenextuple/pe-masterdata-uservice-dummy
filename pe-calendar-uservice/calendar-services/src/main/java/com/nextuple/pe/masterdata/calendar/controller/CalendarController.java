/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.masterdata.calendar.controller;

import static com.nextuple.common.constants.CommonConstants.CALENDAR_DEFAULT_SORT_BY;
import static com.nextuple.common.constants.CommonConstants.CARRIER_DEFAULT_SORT_BY;
import static com.nextuple.common.constants.CommonConstants.DEFAULT_SORT_ORDER;

import com.nextuple.calendar.domain.CalendarDaysStatusInfo;
import com.nextuple.calendar.domain.inbound.CalendarRequest;
import com.nextuple.calendar.domain.outbound.CalendarResponse;
import com.nextuple.calendar.persistence.exception.CalendarDomainException;
import com.nextuple.calendar.persistence.exception.CalenderServiceException;
import com.nextuple.calendar.persistence.exception.DateException;
import com.nextuple.common.base.PagePayload;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.pojo.PageProperties;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.pe.masterdata.calendar.controller.docs.GetCalendarListWithPaginationDoc;
import com.nextuple.pe.masterdata.calendar.controller.docs.HandleCreateCalendarDoc;
import com.nextuple.pe.masterdata.calendar.controller.docs.HandleGetCalendarDoc;
import com.nextuple.pe.masterdata.calendar.controller.docs.HandleGetUpcomingDaysCalendarStatusDoc;
import com.nextuple.pe.masterdata.calendar.domain.NodeCarrierServiceCalendarDto;
import com.nextuple.pe.masterdata.calendar.service.CalendarService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@Tag(name = "Calendar APIs")
@RequestMapping("/calendar")
@RequiredArgsConstructor
public class CalendarController {

  private static final Logger logger = LoggerFactory.getLogger(CalendarController.class);

  private final CalendarService calendarService;

  private final PageProperties pageProperties;

  @HandleCreateCalendarDoc
  @PostMapping
  public ResponseEntity<BaseResponse<CalendarResponse>> handleCreateCalendar(
      @Valid @RequestBody CalendarRequest calendarRequest)
      throws CalendarDomainException, DateException, CommonServiceException {
    logger.debug("Inside handleCreateCalendar() for calendarRequest: {}", calendarRequest);
    try {
      var calendarResponse = calendarService.processCreateCalendar(calendarRequest);
      logger.info("Response after creation of calendar :{}", calendarResponse);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Calendar created successfully!")
              .payload(calendarResponse)
              .build());
    } catch (Exception e) {
      logger.error("Error in handleCreateCalendar()");
      throw e;
    }
  }

  @HandleGetCalendarDoc
  @GetMapping("/{orgId}/{calendarId}")
  public ResponseEntity<BaseResponse<CalendarResponse>> handleGetCalendar(
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier of the organization", example = "NEXTUPLE")
          String orgId,
      @NotBlank(message = "calenderId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier of the calendar", example = "CALENDAR2023")
          String calendarId)
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
  @HandleGetUpcomingDaysCalendarStatusDoc
  public ResponseEntity<BaseResponse<List<CalendarDaysStatusInfo>>>
      handleGetUpcomingDaysCalendarStatus(
          @NotBlank(message = "orgId can't be empty")
              @PathVariable
              @Parameter(
                  description = "Unique identifier of the organization",
                  example = "NEXTUPLE")
              String orgId,
          @RequestParam
              @Parameter(description = "Unique identifier of the node", example = "NODE01")
              Optional<String> nodeId,
          @RequestParam
              @Parameter(
                  description = "Unique identifier of the carrier service",
                  example = "ALL-SDND")
              Optional<String> carrierServiceId,
          @RequestParam @Parameter(description = "Service option of the carrier", example = "SDND")
              Optional<String> serviceOption,
          @RequestParam
              @Parameter(
                  description = "Number of days in the future for which the status is required.",
                  example = "7")
              Optional<Integer> numberOfDaysInFuture,
          @RequestParam
              @Parameter(
                  description =
                      "Shipping stage of the carrier service. Shipping stage can be PICKUP, TRANSIT, DELIVERY, RECEIVING or ALL",
                  example = "PICKUP")
              Optional<String> shippingStage,
          @RequestParam
              @Parameter(
                  description = "The Date from which to get status of upcoming days of  calendar",
                  example = "2024-01-01",
                  hidden = true)
              Optional<String> fromDate)
          throws CalendarDomainException, CommonServiceException, CalenderServiceException {
    try {
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Calendar status for upcoming days fetched successfully!")
              .payload(
                  calendarService.processGetUpcomingDaysCalendarStatus(
                      NodeCarrierServiceCalendarDto.builder()
                          .orgId(orgId)
                          .nodeId(nodeId)
                          .carrierServiceId(carrierServiceId)
                          .serviceOption(serviceOption)
                          .numberOfDaysInFuture(numberOfDaysInFuture)
                          .shippingStage(shippingStage)
                          .fromDate(fromDate)
                          .build()))
              .build());
    } catch (Exception e) {
      logger.error("Error in handleGetUpcomingDaysCalendarStatus()");
      throw e;
    }
  }

  @GetCalendarListWithPaginationDoc
  @GetMapping("/{orgId}")
  public ResponseEntity<BaseResponse<PagePayload<CalendarResponse>>> getCalendarListWithPagination(
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier of the organization", example = "NEXTUPLE")
          String orgId,
      @Parameter(description = "Page parameters of the response") PageParams pageParams)
      throws CalendarDomainException, CommonServiceException {
    logger.debug("Processing get calendar list by orgId");

    Page<CalendarResponse> calendarResponsePage =
        calendarService.getCalendarList(
            orgId,
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            pageParams.getPageSize().orElse(pageProperties.getPageSize()),
            pageParams.getSortBy().orElse(CALENDAR_DEFAULT_SORT_BY),
            pageParams.getSortOrder().orElse(DEFAULT_SORT_ORDER));

    PagePayload<CalendarResponse> pagePayload =
        setCalendarPagePayload(calendarResponsePage, pageParams);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Calendar list fetched successfully!")
            .payload(pagePayload)
            .build());
  }

  private PagePayload<CalendarResponse> setCalendarPagePayload(
      Page<CalendarResponse> calendarResponsePage, PageParams pageParams) {
    PagePayload<CalendarResponse> pagePayload = new PagePayload<>();
    var pagination = new PagePayload.Pagination();
    pagination.setTotalRecords((int) calendarResponsePage.getTotalElements());
    pagination.setTotalPages(calendarResponsePage.getTotalPages());
    pagination.setCurrentPage(pageParams.getPageNo().orElse(pageProperties.getPageNo()));
    pagination.setSortOrder(pageParams.getSortOrder().orElse(DEFAULT_SORT_ORDER));
    pagination.setSortBy(pageParams.getSortBy().orElse(CARRIER_DEFAULT_SORT_BY));
    pagePayload.setPagination(pagination);
    pagePayload.setData(calendarResponsePage.getContent());

    return pagePayload;
  }
}
