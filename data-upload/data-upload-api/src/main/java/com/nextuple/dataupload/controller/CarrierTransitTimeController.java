package com.nextuple.dataupload.controller;

import static com.nextuple.common.constants.CommonConstants.CARRIER_DEFAULT_SORT_BY;
import static com.nextuple.common.constants.CommonConstants.DEFAULT_SORT_ORDER;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.pojo.PageProperties;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.util.PaginationUtil;
import com.nextuple.dataupload.domain.dto.CarrierTransitDto;
import com.nextuple.dataupload.service.CarrierTransitTimeService;
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
  private static final String PAGINATION_URL =
      "/data-upload/ui/carrier-transit-time/orgId/%s?pageNo=%d&pageSize=%d";
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
            pageParams.getSortBy().orElse(CARRIER_DEFAULT_SORT_BY),
            pageParams.getSortOrder().orElse(DEFAULT_SORT_ORDER));

    String nextUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            carrierTransitDto.getPagination().getTotalPages(),
            "next",
            String.format(
                PAGINATION_URL,
                orgId,
                (pageParams.getPageNo().orElse(pageProperties.getPageNo()) + 1),
                pageParams.getPageSize().orElse(pageProperties.getPageSize())));

    String previousUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            carrierTransitDto.getPagination().getTotalPages(),
            "previous",
            String.format(
                PAGINATION_URL,
                orgId,
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
