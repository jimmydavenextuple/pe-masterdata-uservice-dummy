package com.hbc.dataupload.controller;

import com.hbc.carrier.domain.pojo.PageParams;
import com.hbc.common.base.PagePayload;
import com.hbc.common.response.BaseResponse;
import com.hbc.common.util.PaginationUtil;
import com.hbc.dataupload.domain.dto.CarrierTransitDto;
import com.hbc.dataupload.service.CarrierTransitTimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ui/carrier-transit-time")
@RequiredArgsConstructor
@Slf4j
public class CarrierTransitTimeController {

  @Value("${pagination.pageNo}")
  private Integer defaultPageNo;

  @Value("${pagination.pageSize}")
  private Integer defaultPageSize;

  @Value("${pagination.sortBy}")
  private String defaultSortBy;

  @Value("${pagination.sortOrder}")
  private String defaultSortOrder;

  private final CarrierTransitTimeService carrierTransitTimeService;

  @GetMapping("/orgId/{orgId}")
  public ResponseEntity<BaseResponse<PagePayload<CarrierTransitDto>>> getCarrierTransitTimeList(
      @PathVariable String orgId, PageParams pageParams) {
    PagePayload<CarrierTransitDto> carrierTransitDto =
        carrierTransitTimeService.getCarrierTransitTimeList(
            orgId,
            pageParams.getPageNo().orElse(defaultPageNo),
            pageParams.getPageSize().orElse(defaultPageSize),
            pageParams.getSortBy().orElse(defaultSortBy),
            pageParams.getSortOrder().orElse(defaultSortOrder));

    String nextUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(defaultPageNo),
            carrierTransitDto.getPagination().getTotalPages(),
            "next",
            String.format(
                "/ui/carrier-transit-time/orgId/{orgId}?pageNo=%d&pageSize=%d",
                (pageParams.getPageNo().orElse(defaultPageNo) + 1),
                pageParams.getPageSize().orElse(defaultPageSize)));

    String previousUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(defaultPageNo),
            carrierTransitDto.getPagination().getTotalPages(),
            "previous",
            String.format(
                "/ui/carrier-transit-time/orgId/{orgId}?pageNo=%d&pageSize=%d",
                (pageParams.getPageNo().orElse(defaultPageNo) - 1),
                pageParams.getPageSize().orElse(defaultPageSize)));

    carrierTransitDto.getPagination().setNext(nextUri);
    carrierTransitDto.getPagination().setPrevious(previousUri);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Carrier Transit Time list fetched successfully")
            .payload(carrierTransitDto)
            .build());
  }
}
