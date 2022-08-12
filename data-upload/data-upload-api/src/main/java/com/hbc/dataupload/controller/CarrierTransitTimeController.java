package com.hbc.dataupload.controller;

import com.hbc.common.base.PagePayload;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.domain.dto.CarrierTransitDto;
import com.hbc.dataupload.service.CarrierTransitTimeService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

  private final CarrierTransitTimeService carrierTransitTimeService;

  @GetMapping("/orgId/{orgId}")
  public ResponseEntity<BaseResponse<PagePayload<CarrierTransitDto>>> getCarrierTransitTimeList(
      @PathVariable String orgId,
      @RequestParam(required = false) Optional<Integer> pageNo,
      @RequestParam(required = false) Optional<Integer> pageSize,
      @RequestParam(required = false) Optional<String> sortBy,
      @RequestParam(required = false) Optional<String> sortOrder) {
    try {
      PagePayload<CarrierTransitDto> carrierTransitDto =
          carrierTransitTimeService.getCarrierTransitTimeList(
              orgId,
              pageNo.orElse(defaultPageNo),
              pageSize.orElse(defaultPageSize),
              sortBy.orElse(defaultSortBy),
              sortOrder);

      String nextUri =
          buildUriForPagination(
              pageNo.orElse(defaultPageNo),
              pageSize.orElse(defaultPageSize),
              carrierTransitDto.getPagination().getTotalPages(),
              "next");
      String previousUri =
          buildUriForPagination(
              pageNo.orElse(defaultPageNo),
              pageSize.orElse(defaultPageSize),
              carrierTransitDto.getPagination().getTotalPages(),
              "previous");
      carrierTransitDto.getPagination().setNext(nextUri);
      carrierTransitDto.getPagination().setPrevious(previousUri);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Carrier Transit Time list fetched successfully")
              .payload(carrierTransitDto)
              .build());
    } catch (Exception e) {
      log.error("Error in fetching carrier transit time list");
      throw e;
    }
  }

  private String buildUriForPagination(
      int currentPageNo, int pageSize, int totalPages, String uriType) {
    if (uriType.equalsIgnoreCase("next")) {
      if (currentPageNo >= totalPages) {
        return null;
      }
      return "/ui/carrier-transit-time/orgId/{orgId}?pageNo="
          + (currentPageNo + 1)
          + "&pageSize="
          + pageSize;
    } else {
      if (currentPageNo == 1) {
        return null;
      }
      return "/ui/carrier-transit-time/orgId/{orgId}?pageNo="
          + (currentPageNo - 1)
          + "&pageSize="
          + pageSize;
    }
  }
}
