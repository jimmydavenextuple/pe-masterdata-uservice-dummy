/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.controller;

import static com.nextuple.common.constants.CommonConstants.CALENDAR_DEFAULT_SORT_BY;
import static com.nextuple.common.constants.CommonConstants.DEFAULT_SORT_ORDER;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.DATA_UPLOAD_PAGINATION_URL_CONSTANT_UI;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.pojo.PageProperties;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.util.PaginationUtil;
import com.nextuple.dataupload.controller.docs.GetCalenderDataDoc;
import com.nextuple.dataupload.domain.dto.CalendarDto;
import com.nextuple.dataupload.service.CalendarDataService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for calendar data APIs.
 *
 * <p>This controller provides APIs to fetch calendar data for organizations, with support for
 * pagination and sorting of the calendar entries.
 *
 * <p>The controller is tagged with "Calender Data APIs" for easy categorization in API
 * documentation.
 */
@RestController
@RequestMapping("/ui/calendar")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Calender Data APIs")
public class CalendarDataController {
  private static final String PAGINATION_URL =
      DATA_UPLOAD_PAGINATION_URL_CONSTANT_UI + "/calendar/%s?pageNo=%d&pageSize=%d";
  private final PageProperties pageProperties;

  private final CalendarDataService calendarDataService;

  /**
   * Retrieves a paginated list of calendar data for the specified organization.
   *
   * <p>This method processes a GET request to fetch calendar data for a specific organization, with
   * support for pagination and sorting. It returns a paginated response containing calendar
   * entries.
   *
   * @param orgId The unique identifier for the organization (e.g., "NEXTUPLE_GR").
   * @param pageParams The pagination parameters, such as page number, page size, sorting criteria,
   *     and sort order.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a paginated list of
   *     calendar data.
   */
  @GetMapping("/{orgId}")
  @GetCalenderDataDoc
  public ResponseEntity<BaseResponse<PagePayload<CalendarDto>>> getCalendarList(
      @PathVariable @Parameter(description = "Unique identifier for organization ID.") String orgId,
      PageParams pageParams) {
    PagePayload<CalendarDto> calendarDtoPagePayload =
        calendarDataService.getCalendarList(
            orgId,
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            pageParams.getPageSize().orElse(pageProperties.getPageSize()),
            pageParams.getSortBy().orElse(CALENDAR_DEFAULT_SORT_BY),
            pageParams.getSortOrder().orElse(DEFAULT_SORT_ORDER));

    String nextUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            calendarDtoPagePayload.getPagination().getTotalPages(),
            "next",
            PAGINATION_URL.formatted(
                orgId,
                (pageParams.getPageNo().orElse(pageProperties.getPageNo()) + 1),
                pageParams.getPageSize().orElse(pageProperties.getPageSize())));

    String previousUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            calendarDtoPagePayload.getPagination().getTotalPages(),
            "previous",
            PAGINATION_URL.formatted(
                orgId,
                (pageParams.getPageNo().orElse(pageProperties.getPageNo()) - 1),
                pageParams.getPageSize().orElse(pageProperties.getPageSize())));

    calendarDtoPagePayload.getPagination().setNext(nextUri);
    calendarDtoPagePayload.getPagination().setPrevious(previousUri);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Calendar list fetched successfully")
            .payload(calendarDtoPagePayload)
            .build());
  }
}
