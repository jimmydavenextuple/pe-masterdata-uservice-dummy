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
import com.nextuple.dataupload.common.outbound.NodeCarrierServiceAndServiceOptionResponse;
import com.nextuple.dataupload.controller.docs.GetNodeCarrierServiceAndServiceOptionDoc;
import com.nextuple.dataupload.domain.pojo.NodeCarrierServicePageProperties;
import com.nextuple.dataupload.service.NodeCarrierServiceAndServiceOptionService;
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
 * Controller for fetching Node Carrier Service and Service Option details.
 *
 * <p>This controller provides APIs to interact with the system to retrieve details related to node
 * carrier services and their corresponding service options for a specified organization. The data
 * is paginated and supports sorting based on the provided parameters.
 *
 * <p>The controller is tagged with "Node Carrier Service and Service Option APIs" for easy
 * categorization in API documentation.
 */
@RestController
@RequestMapping("/ui/node-carrier-service-option")
@RequiredArgsConstructor
@Tag(name = "Node Carrier Service and Service Option APIs")
public class NodeCarrierServiceAndServiceOptionController {

  private final NodeCarrierServicePageProperties pageProperties;
  private final DefaultPageProperties defaultPageProperties;
  private final NodeCarrierServiceAndServiceOptionService nodeCarrierServiceAndServiceOptionService;
  private final Logger logger =
      LoggerFactory.getLogger(NodeCarrierServiceAndServiceOptionController.class);
  private static final String PAGINATION_URL =
      DATA_UPLOAD_PAGINATION_URL_CONSTANT_UI
          + "/node-carrier-service-option/%s?pageNo=%d&pageSize=%d&sortBy=%s&sortOrder=%s";

  /**
   * Retrieves a paginated list of node carrier service and service option details for the specified
   * organization.
   *
   * <p>This method processes a GET request to fetch a paginated list of node carrier service and
   * service option details based on the provided organization ID. Pagination and sorting are
   * supported through the `pageParams` parameter.
   *
   * @param orgId The unique identifier for the organization (e.g., "NEXTUPLE_GR").
   * @param pageParams The pagination parameters, including page number, page size, sorting
   *     criteria, and sort order.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a paginated list of
   *     node carrier service and service option details.
   */
  @GetMapping("/{orgId}")
  @GetNodeCarrierServiceAndServiceOptionDoc
  public ResponseEntity<BaseResponse<PagePayload<NodeCarrierServiceAndServiceOptionResponse>>>
      getListOfNodeCarrierServiceAndServiceOptionDetails(
          @NotBlank @PathVariable @Parameter(description = "Unique identifier for organization ID.")
              String orgId,
          PageParams pageParams) {

    logger.debug("Processing get Node + Carrier Service + Service Option details");

    PagePayload<NodeCarrierServiceAndServiceOptionResponse> nodeCarrierServiceResponse =
        nodeCarrierServiceAndServiceOptionService
            .getListOfNodeCarrierServiceAndServiceOptionDetails(
                orgId,
                pageParams.getPageNo().orElse(defaultPageProperties.getPageNo()),
                pageParams.getPageSize().orElse(defaultPageProperties.getPageSize()),
                pageParams.getSortBy().orElse(pageProperties.getSortBy()),
                pageParams.getSortOrder().orElse(pageProperties.getSortOrder()));

    updatePaginationDetails(nodeCarrierServiceResponse, orgId, pageParams);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("List of node + carrier service + service option details fetched successfully")
            .payload(nodeCarrierServiceResponse)
            .build());
  }

  private void updatePaginationDetails(
      PagePayload<NodeCarrierServiceAndServiceOptionResponse> nodeCarrierServiceResponse,
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
