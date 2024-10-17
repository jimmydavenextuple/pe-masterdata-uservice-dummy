/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.controller;

import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.DATA_UPLOAD_PAGINATION_URL_CONSTANT_UI;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.pojo.PageProperties;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.util.PaginationUtil;
import com.nextuple.dataupload.common.outbound.ProcessingTimeBufferResponse;
import com.nextuple.dataupload.controller.docs.GetProcessingTimeBufferDoc;
import com.nextuple.dataupload.controller.docs.GetProcessingTimeBufferDocV1;
import com.nextuple.dataupload.service.ProcessingTimeBufferService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ui/processing-time-buffer")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Processing Time Buffer APIs")
public class ProcessingTimeBufferController {

  private static final String PAGINATION_URL =
      DATA_UPLOAD_PAGINATION_URL_CONSTANT_UI
          + "/processing-time-buffer/orgId/%s?pageNo=%d&pageSize=%d";
  public static final String PAGINATION_NEXT_CONSTANT = "next";
  public static final String PAGINATION_PREVIOUS_CONSTANT = "previous";
  private final PageProperties pageProperties;

  private final ProcessingTimeBufferService processingTimeBufferService;

  @GetMapping("/orgId/{orgId}")
  @GetProcessingTimeBufferDoc
  public ResponseEntity<BaseResponse<PagePayload<ProcessingTimeBufferResponse>>>
      getProcessingTimeBufferDetails(
          @NotBlank(message = "orgId can't be empty")
              @Parameter(description = "Unique identifier of organization")
              @PathVariable
              String orgId,
          PageParams pageParams) {
    PagePayload<ProcessingTimeBufferResponse> processingTimeBufferDtoPagePayload =
        processingTimeBufferService.getProcessingTimeBuffers(orgId, null, pageParams);

    String nextUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            processingTimeBufferDtoPagePayload.getPagination().getTotalPages(),
            PAGINATION_NEXT_CONSTANT,
            PAGINATION_URL.formatted(
                orgId,
                (pageParams.getPageNo().orElse(pageProperties.getPageNo()) + 1),
                pageParams.getPageSize().orElse(pageProperties.getPageSize())));

    String previousUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            processingTimeBufferDtoPagePayload.getPagination().getTotalPages(),
            PAGINATION_PREVIOUS_CONSTANT,
            PAGINATION_URL.formatted(
                orgId,
                (pageParams.getPageNo().orElse(pageProperties.getPageNo()) - 1),
                pageParams.getPageSize().orElse(pageProperties.getPageSize())));

    processingTimeBufferDtoPagePayload.getPagination().setNext(nextUri);
    processingTimeBufferDtoPagePayload.getPagination().setPrevious(previousUri);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Processing Time Buffer list fetched successfully")
            .payload(processingTimeBufferDtoPagePayload)
            .build());
  }

  @GetMapping("/v1/orgId/{orgId}")
  @GetProcessingTimeBufferDocV1
  public ResponseEntity<BaseResponse<PagePayload<ProcessingTimeBufferResponse>>>
      getProcessingTimeBufferV1(
          @NotBlank(message = "orgId can't be empty")
              @Parameter(description = "Unique identifier of organization")
              @PathVariable
              String orgId,
          @RequestParam(required = false)
              @Parameter(
                  description =
                      "Comma separated string that contains references of the nodes to be searched for.")
              String nodeIds,
          PageParams pageParams) {
    PagePayload<ProcessingTimeBufferResponse> processingTimeBufferDtoPagePayload =
        processingTimeBufferService.getProcessingTimeBuffers(orgId, nodeIds, pageParams);

    String nextUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            processingTimeBufferDtoPagePayload.getPagination().getTotalPages(),
            PAGINATION_NEXT_CONSTANT,
            PAGINATION_URL.formatted(
                orgId,
                (pageParams.getPageNo().orElse(pageProperties.getPageNo()) + 1),
                pageParams.getPageSize().orElse(pageProperties.getPageSize())));

    String previousUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            processingTimeBufferDtoPagePayload.getPagination().getTotalPages(),
            PAGINATION_PREVIOUS_CONSTANT,
            PAGINATION_URL.formatted(
                orgId,
                (pageParams.getPageNo().orElse(pageProperties.getPageNo()) - 1),
                pageParams.getPageSize().orElse(pageProperties.getPageSize())));

    processingTimeBufferDtoPagePayload.getPagination().setNext(nextUri);
    processingTimeBufferDtoPagePayload.getPagination().setPrevious(previousUri);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Processing Time Buffer list fetched successfully")
            .payload(processingTimeBufferDtoPagePayload)
            .build());
  }
}
