package com.hbc.pe.masterdata.calendar.controller;

import static com.hbc.common.constants.CommonConstants.CALENDAR_DEFAULT_SORT_BY;
import static com.hbc.common.constants.CommonConstants.CARRIER_DEFAULT_SORT_BY;
import static com.hbc.common.constants.CommonConstants.DEFAULT_SORT_ORDER;

import com.hbc.calendar.domain.CalendarDaysStatusInfo;
import com.hbc.calendar.domain.inbound.CalendarRequest;
import com.hbc.calendar.domain.outbound.CalendarResponse;
import com.hbc.common.base.PagePayload;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.pojo.PageParams;
import com.hbc.common.pojo.PageProperties;
import com.hbc.common.response.BaseResponse;
import com.hbc.pe.masterdata.calendar.exception.CalendarDomainException;
import com.hbc.pe.masterdata.calendar.exception.CalenderServiceException;
import com.hbc.pe.masterdata.calendar.exception.DateException;
import com.hbc.pe.masterdata.calendar.service.CalendarService;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/calendar")
@RequiredArgsConstructor
public class CalendarController {

  private static final Logger logger = LoggerFactory.getLogger(CalendarController.class);

  private final CalendarService calendarService;

  private final PageProperties pageProperties;

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

  @GetMapping("/{orgId}/{calendarId}")
  public ResponseEntity<BaseResponse<CalendarResponse>> handleGetCalendar(
      @NotBlank(message = "orgId can't be empty") @PathVariable String orgId,
      @NotBlank(message = "calenderId can't be empty") @PathVariable String calendarId)
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
          @NotBlank(message = "orgId can't be empty") @PathVariable String orgId,
          @RequestParam Optional<String> nodeId,
          @RequestParam Optional<String> carrierServiceId,
          @RequestParam Optional<String> serviceOption,
          @RequestParam Optional<Integer> numberOfDaysInFuture,
          @RequestParam Optional<String> shippingStage)
          throws CalendarDomainException, CommonServiceException, CalenderServiceException {
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

  @GetMapping("/{orgId}")
  public ResponseEntity<BaseResponse<PagePayload<CalendarResponse>>> getCalendarListWithPagination(
      @NotBlank(message = "orgId can't be empty") @PathVariable String orgId, PageParams pageParams)
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
