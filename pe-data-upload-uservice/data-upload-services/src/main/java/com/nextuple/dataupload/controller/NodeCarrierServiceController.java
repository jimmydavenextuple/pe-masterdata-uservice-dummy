/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.controller;

import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.DATA_UPLOAD_PAGINATION_URL_CONSTANT_UI;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.util.PaginationUtil;
import com.nextuple.dataupload.controller.docs.GetNodeCarrierServiceDoc;
import com.nextuple.dataupload.domain.dto.NodeCarrierServiceResponse;
import com.nextuple.dataupload.domain.pojo.NodeCarrierServicePageProperties;
import com.nextuple.dataupload.service.NodeCarrierServiceDetailsService;
import com.nextuple.jobs.framework.common.domain.pojo.DefaultPageProperties;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing Node Carrier Service details.
 *
 * <p>This controller provides APIs to retrieve node and carrier service details associated with a
 * specified organization. The data is paginated and supports sorting based on the provided
 * parameters.
 *
 * <p>The controller is tagged with "Node Carrier Service APIs" for categorization in API
 * documentation.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ui/node-carrier-service")
@Tag(name = "Node Carrier Service APIs")
public class NodeCarrierServiceController {

  private final Logger logger = LoggerFactory.getLogger(NodeCarrierServiceController.class);
  private final NodeCarrierServiceDetailsService nodeCarrierServiceDetailsService;
  private final NodeCarrierServicePageProperties pageProperties;
  private final DefaultPageProperties defaultPageProperties;
  private static final String PAGINATION_URL =
      DATA_UPLOAD_PAGINATION_URL_CONSTANT_UI
          + "/node-carrier-service/%s?pageNo=%d&pageSize=%d&sortBy=%s&sortOrder=%s";

  /**
   * Retrieves a paginated list of node and carrier service details for the specified organization.
   *
   * <p>This method processes a GET request to fetch a paginated list of node and carrier service
   * details based on the provided organization ID. Pagination and sorting are supported through the
   * `pageParams` parameter.
   *
   * @param orgId The unique identifier for the organization (e.g., "NEXTUPLE_GR").
   * @param pageParams The pagination parameters, such as page number, page size, sorting criteria,
   *     and sort order.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a paginated list of
   *     node and carrier service details.
   */
  @GetMapping("/{orgId}")
  @GetNodeCarrierServiceDoc
  public ResponseEntity<BaseResponse<PagePayload<NodeCarrierServiceResponse>>>
      getNodeCarrierServiceDetails(
          @PathVariable @NotBlank @Parameter(description = "Unique identifier for organization ID.")
              String orgId,
          PageParams pageParams) {
    logger.debug("Processing get list of nodes and carrier service details");

    PagePayload<NodeCarrierServiceResponse> nodeCarrierServiceResponse =
        nodeCarrierServiceDetailsService.getNodeCarrierServiceDetails(
            orgId,
            pageParams.getPageNo().orElse(defaultPageProperties.getPageNo()),
            pageParams.getPageSize().orElse(defaultPageProperties.getPageSize()),
            pageParams.getSortBy().orElse(pageProperties.getSortBy()),
            pageParams.getSortOrder().orElse(pageProperties.getSortOrder()));

    updatePaginationDetails(nodeCarrierServiceResponse, orgId, pageParams);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node and Carrier Service List fetched successfully")
            .payload(nodeCarrierServiceResponse)
            .build());
  }

  private void updatePaginationDetails(
      PagePayload<NodeCarrierServiceResponse> nodeCarrierServiceResponse,
      String orgId,
      PageParams pageParams) {
    int currentPage = nodeCarrierServiceResponse.getPagination().getCurrentPage();
    String nextUri =
        PaginationUtil.buildUriForPagination(
            currentPage,
            nodeCarrierServiceResponse.getPagination().getTotalPages(),
            "next",
            PAGINATION_URL.formatted(
                orgId,
                currentPage + 1,
                pageParams.getPageSize().orElse(defaultPageProperties.getPageSize()),
                nodeCarrierServiceResponse.getPagination().getSortBy(),
                nodeCarrierServiceResponse.getPagination().getSortOrder()));

    String previousUri =
        PaginationUtil.buildUriForPagination(
            currentPage,
            nodeCarrierServiceResponse.getPagination().getTotalPages(),
            "previous",
            PAGINATION_URL.formatted(
                orgId,
                currentPage - 1,
                pageParams.getPageSize().orElse(defaultPageProperties.getPageSize()),
                nodeCarrierServiceResponse.getPagination().getSortBy(),
                nodeCarrierServiceResponse.getPagination().getSortOrder()));

    nodeCarrierServiceResponse.getPagination().setNext(nextUri);
    nodeCarrierServiceResponse.getPagination().setPrevious(previousUri);
  }
}
