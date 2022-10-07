package com.hbc.dataupload.controller;

import static com.hbc.common.constants.CommonConstants.CALENDAR_DEFAULT_SORT_BY;
import static com.hbc.common.constants.CommonConstants.DEFAULT_SORT_ORDER;

import com.hbc.common.base.PagePayload;
import com.hbc.common.pojo.PageParams;
import com.hbc.common.pojo.PageProperties;
import com.hbc.common.response.BaseResponse;
import com.hbc.common.util.PaginationUtil;
import com.hbc.dataupload.domain.dto.CalendarDto;
import com.hbc.dataupload.service.CalendarDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ui/calendar")
@RequiredArgsConstructor
@Slf4j
public class CalendarDataController {
  private static final String PAGINATION_URL = "/data-upload/ui/calendar/%s?pageNo=%d&pageSize=%d";
  private final PageProperties pageProperties;

  private final CalendarDataService calendarDataService;

  @GetMapping("/{orgId}")
  public ResponseEntity<BaseResponse<PagePayload<CalendarDto>>> getCalendarList(
      @PathVariable String orgId, PageParams pageParams) {
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
            String.format(
                PAGINATION_URL,
                orgId,
                (pageParams.getPageNo().orElse(pageProperties.getPageNo()) + 1),
                pageParams.getPageSize().orElse(pageProperties.getPageSize())));

    String previousUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            calendarDtoPagePayload.getPagination().getTotalPages(),
            "previous",
            String.format(
                PAGINATION_URL,
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
