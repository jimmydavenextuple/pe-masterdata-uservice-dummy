/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.controller;

import static com.nextuple.common.constants.CommonConstants.DEFAULT_SORT_ORDER;
import static com.nextuple.common.constants.CommonConstants.NODE_DEFAULT_SORT_BY;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.DATA_UPLOAD_PAGINATION_URL_CONSTANT_UI;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.pojo.PageProperties;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.util.PaginationUtil;
import com.nextuple.dataupload.domain.dto.NodeServiceOptionDto;
import com.nextuple.dataupload.service.NodeServiceOptionService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Validated
@Controller
@RequestMapping("/ui/node-service-option")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Node Service Option APIs")
public class NodeServiceOptionController {
  private static final String PAGINATION_URL =
      DATA_UPLOAD_PAGINATION_URL_CONSTANT_UI
          + "/node-service-option/orgId/%s?pageNo=%d&pageSize=%d";
  private final NodeServiceOptionService nodeServiceOptionService;
  private final PageProperties pageProperties;

  @GetMapping("/orgId/{orgId}")
  public ResponseEntity<BaseResponse<PagePayload<NodeServiceOptionDto>>> getNodeServiceOption(
      @PathVariable
          @Parameter(description = "Unique identifier for organization ID.")
          @NotBlank(message = "orgId can't be empty")
          String orgId,
      PageParams pageParams) {

    PagePayload<NodeServiceOptionDto> nodeServiceOptionDto =
        nodeServiceOptionService.getNodeServiceOption(
            orgId,
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            pageParams.getPageSize().orElse(pageProperties.getPageSize()),
            pageParams.getSortBy().orElse(NODE_DEFAULT_SORT_BY),
            pageParams.getSortOrder().orElse(DEFAULT_SORT_ORDER));

    String nextUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            nodeServiceOptionDto.getPagination().getTotalPages(),
            "next",
            PAGINATION_URL.formatted(
                orgId,
                (pageParams.getPageNo().orElse(pageProperties.getPageNo()) + 1),
                pageParams.getPageSize().orElse(pageProperties.getPageSize())));

    String previousUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            nodeServiceOptionDto.getPagination().getTotalPages(),
            "previous",
            PAGINATION_URL.formatted(
                orgId,
                (pageParams.getPageNo().orElse(pageProperties.getPageNo()) - 1),
                pageParams.getPageSize().orElse(pageProperties.getPageSize())));

    nodeServiceOptionDto.getPagination().setNext(nextUri);
    nodeServiceOptionDto.getPagination().setPrevious(previousUri);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Service Option List fetched successfully")
            .payload(nodeServiceOptionDto)
            .build());
  }
}
