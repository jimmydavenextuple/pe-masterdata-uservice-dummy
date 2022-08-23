package com.hbc.dataupload.controller;

import com.hbc.carrier.domain.pojo.PageProperties;
import com.hbc.common.base.PagePayload;
import com.hbc.common.pojo.PageParams;
import com.hbc.common.response.BaseResponse;
import com.hbc.common.util.PaginationUtil;
import com.hbc.dataupload.domain.dto.CarrierTransitDto;
import com.hbc.dataupload.service.CarrierTransitTimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

  private static final String DEFAULT_SORT_BY = "carrierId";
  private static final String DEFAULT_SORT_ORDER = "ASC";

  private final PageProperties pageProperties;

  private final CarrierTransitTimeService carrierTransitTimeService;

  @GetMapping("/orgId/{orgId}")
  public ResponseEntity<BaseResponse<PagePayload<CarrierTransitDto>>> getCarrierTransitTimeList(
      @PathVariable String orgId, PageParams pageParams) {
    PagePayload<CarrierTransitDto> carrierTransitDto =
        carrierTransitTimeService.getCarrierTransitTimeList(
            orgId,
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            pageParams.getPageSize().orElse(pageProperties.getPageSize()),
            pageParams.getSortBy().orElse(DEFAULT_SORT_BY),
            pageParams.getSortOrder().orElse(DEFAULT_SORT_ORDER));

    String nextUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            carrierTransitDto.getPagination().getTotalPages(),
            "next",
            String.format(
                "/ui/carrier-transit-time/orgId/{orgId}?pageNo=%d&pageSize=%d",
                (pageParams.getPageNo().orElse(pageProperties.getPageNo()) + 1),
                pageParams.getPageSize().orElse(pageProperties.getPageSize())));

    String previousUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            carrierTransitDto.getPagination().getTotalPages(),
            "previous",
            String.format(
                "/ui/carrier-transit-time/orgId/{orgId}?pageNo=%d&pageSize=%d",
                (pageParams.getPageNo().orElse(pageProperties.getPageNo()) - 1),
                pageParams.getPageSize().orElse(pageProperties.getPageSize())));

    carrierTransitDto.getPagination().setNext(nextUri);
    carrierTransitDto.getPagination().setPrevious(previousUri);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Carrier Transit Time list fetched successfully")
            .payload(carrierTransitDto)
            .build());
  }
}
