/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.controller;

import static com.nextuple.common.constants.CommonConstants.CARRIER_DEFAULT_SORT_BY;
import static com.nextuple.common.constants.CommonConstants.DEFAULT_SORT_ORDER;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.DATA_UPLOAD_PAGINATION_URL_CONSTANT_UI;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.pojo.PageProperties;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.util.PaginationUtil;
import com.nextuple.dataupload.controller.docs.GetCarrierTransitTimeDoc;
import com.nextuple.dataupload.domain.dto.CarrierTransitDto;
import com.nextuple.dataupload.service.CarrierTransitTimeService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Carrier Transit Time APIs")
public class CarrierTransitTimeController {
  private static final String PAGINATION_URL =
      DATA_UPLOAD_PAGINATION_URL_CONSTANT_UI
          + "/carrier-transit-time/orgId/%s?pageNo=%d&pageSize=%d";
  private final PageProperties pageProperties;

  private final CarrierTransitTimeService carrierTransitTimeService;

  @GetMapping("/orgId/{orgId}")
  @GetCarrierTransitTimeDoc
  public ResponseEntity<BaseResponse<PagePayload<CarrierTransitDto>>> getCarrierTransitTimeList(
      @PathVariable @Parameter(description = "Unique identifier for organization ID.") String orgId,
      PageParams pageParams) {
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
            PAGINATION_URL.formatted(
                orgId,
                (pageParams.getPageNo().orElse(pageProperties.getPageNo()) + 1),
                pageParams.getPageSize().orElse(pageProperties.getPageSize())));

    String previousUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            carrierTransitDto.getPagination().getTotalPages(),
            "previous",
            PAGINATION_URL.formatted(
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
