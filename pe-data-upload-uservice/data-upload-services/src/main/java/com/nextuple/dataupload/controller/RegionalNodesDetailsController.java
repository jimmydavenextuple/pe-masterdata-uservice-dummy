/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.controller;

import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.DATA_UPLOAD_PAGINATION_URL_CONSTANT_UI;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.pojo.PageProperties;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.util.PaginationUtil;
import com.nextuple.dataupload.controller.docs.GetRegionalNodeDetailsDoc;
import com.nextuple.dataupload.domain.dto.NodeListDto;
import com.nextuple.dataupload.service.RegionalNodesDetailsService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for Regional Node Details APIs.
 *
 * <p>This controller provides APIs to retrieve and manage regional node details for a given
 * organization. It supports fetching a paginated list of nodes with optional filters such as node
 * IDs and node types.
 *
 * <p>The controller is tagged with "Regional Node Details APIs" for easy categorization in API
 * documentation.
 */
@Validated
@RestController
@RequestMapping("/ui/regions-nodes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Regional Node Details APIs")
public class RegionalNodesDetailsController {
  private static final String PAGINATION_URL =
      DATA_UPLOAD_PAGINATION_URL_CONSTANT_UI
          + "/regions-nodes/nodes/orgId/%s?pageNo=%d&pageSize=%d";
  private final PageProperties pageProperties;

  private final RegionalNodesDetailsService regionalNodesDetailsService;

  /**
   * Retrieves a paginated list of nodes for a given organization, with optional filters for node
   * IDs and types.
   *
   * <p>This method processes a GET request to fetch a list of nodes associated with a specific
   * organization, with optional filters for node IDs and node types. The results are paginated
   * based on the provided page parameters, including the current page and page size.
   *
   * @param orgId The unique identifier for the organization (e.g., "NEXTUPLE_GR").
   * @param nodeIds A comma-separated list of node IDs to filter the nodes (optional).
   * @param nodeType The type of node to filter by (optional).
   * @param pageParams The pagination details, including page number, page size, sort order, and
   *     sort by fields.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the paginated list of
   *     node details.
   * @throws CommonServiceException If there is an error during the retrieval of the node list.
   */
  @GetMapping("/nodes/orgId/{orgId}")
  @GetRegionalNodeDetailsDoc
  public ResponseEntity<BaseResponse<PagePayload<NodeListDto>>> getNodesList(
      @NotBlank(message = "OrgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier for organization ID.")
          String orgId,
      @RequestParam(required = false)
          @Parameter(
              description =
                  "Comma separated string that contains references of the nodes to be searched for.")
          String nodeIds,
      @RequestParam(required = false) @Parameter(description = "Identifier for type of node.")
          String nodeType,
      PageParams pageParams)
      throws CommonServiceException {
    PagePayload<NodeListDto> nodeListDto =
        regionalNodesDetailsService.getNodesList(orgId, nodeIds, nodeType, pageParams);

    String nextUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            nodeListDto.getPagination().getTotalPages(),
            "next",
            PAGINATION_URL.formatted(
                orgId,
                (pageParams.getPageNo().orElse(pageProperties.getPageNo()) + 1),
                pageParams.getPageSize().orElse(pageProperties.getPageSize())));

    String previousUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            nodeListDto.getPagination().getTotalPages(),
            "previous",
            PAGINATION_URL.formatted(
                orgId,
                (pageParams.getPageNo().orElse(pageProperties.getPageNo()) - 1),
                pageParams.getPageSize().orElse(pageProperties.getPageSize())));

    nodeListDto.getPagination().setNext(nextUri);
    nodeListDto.getPagination().setPrevious(previousUri);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node list fetched successfully")
            .payload(nodeListDto)
            .build());
  }
}
