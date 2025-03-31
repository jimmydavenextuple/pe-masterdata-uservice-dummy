/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.controller;

import static com.nextuple.common.constants.CommonConstants.DEFAULT_SORT_ORDER;
import static com.nextuple.common.constants.CommonConstants.NODE_DEFAULT_SORT_BY;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.pojo.PageProperties;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.util.PaginationUtil;
import com.nextuple.node.controller.docs.CreateNodeDoc;
import com.nextuple.node.controller.docs.DeleteNodeDoc;
import com.nextuple.node.controller.docs.GetAllNodeList;
import com.nextuple.node.controller.docs.GetNodeCacheKeysDoc;
import com.nextuple.node.controller.docs.GetNodeDetailsDoc;
import com.nextuple.node.controller.docs.GetNodeListDoc;
import com.nextuple.node.controller.docs.GetNodeListV1Doc;
import com.nextuple.node.controller.docs.GetNodeListV2Doc;
import com.nextuple.node.controller.docs.GetNodeTypesDoc;
import com.nextuple.node.controller.docs.UpdateNodeDetailsDoc;
import com.nextuple.node.controller.docs.CheckNodesExistDoc;
import com.nextuple.node.domain.NodeConstants;
import com.nextuple.node.domain.dto.NodeCacheKeyDto;
import com.nextuple.node.domain.dto.NodeDto;
import com.nextuple.node.domain.inbound.NodeRequest;
import com.nextuple.node.domain.inbound.NodeUpdationRequest;
import com.nextuple.node.domain.outbound.NodeResponse;
import com.nextuple.node.domain.outbound.NodeTypesResponse;
import com.nextuple.node.persistence.exception.NodeDomainException;
import com.nextuple.node.service.NodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@Tag(name = "Node APIs")
@RequestMapping("/node")
@RequiredArgsConstructor
public class NodeController {

  private static final Logger logger = LoggerFactory.getLogger(NodeController.class);
  private static final String PAGINATION_URL = "/%s?pageNo=%d&pageSize=%d";
  private static final String PAGINATION_URL_ALL_NODES = "?pageNo=%d&pageSize=%d";
  private final NodeService nodeService;
  private final PageProperties pageProperties;

  @CreateNodeDoc
  @PostMapping
  public ResponseEntity<BaseResponse<NodeResponse>> createNode(
      @Valid @RequestBody NodeRequest nodeRequest)
      throws CommonServiceException, NodeDomainException {
    logger.debug("Processing node creation request");
    try {
      var nodeResponse = nodeService.createNode(nodeRequest);
      logger.info("Response after creation of node :{}", nodeResponse);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node successfully created")
              .payload(nodeResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to create node");
      throw e;
    }
  }

  @UpdateNodeDetailsDoc
  @PutMapping("/{nodeId}/{orgId}")
  public ResponseEntity<BaseResponse<NodeResponse>> updateNodeDetails(
      @NotBlank(message = "nodeId can't be empty")
          @Parameter(description = NodeConstants.NODE_ID, example = NodeConstants.NODE_ID_EXAMPLE)
          @PathVariable
          String nodeId,
      @NotBlank(message = "orgId can't be empty")
          @Parameter(description = NodeConstants.ORG_ID, example = NodeConstants.ORG_ID_EXAMPLE)
          @PathVariable
          String orgId,
      @Valid @RequestBody NodeUpdationRequest nodeUpdationRequest)
      throws NodeDomainException, CommonServiceException {
    logger.debug("Processing update node details");
    try {

      var nodeResponse = nodeService.updateNodeDetails(nodeId, orgId, nodeUpdationRequest);
      logger.info("Response after updation of node :{}", nodeResponse);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node details updated successfully")
              .payload(nodeResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to update node details");
      throw e;
    }
  }

  @GetMapping("/{nodeId}/{orgId}")
  @GetNodeDetailsDoc
  public ResponseEntity<BaseResponse<NodeResponse>> getNodeDetails(
      @NotBlank(message = "nodeId can't be empty")
          @Parameter(description = NodeConstants.NODE_ID, example = NodeConstants.NODE_ID_EXAMPLE)
          @PathVariable
          String nodeId,
      @NotBlank(message = "orgId can't be empty")
          @Parameter(description = NodeConstants.ORG_ID, example = NodeConstants.ORG_ID_EXAMPLE)
          @PathVariable
          String orgId)
      throws NodeDomainException, CommonServiceException {
    logger.debug("Processing get node details");
    try {

      var nodeResponse = nodeService.getNodeDetails(nodeId, orgId);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node details fetched successfully")
              .payload(nodeResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to fetch node details");
      throw e;
    }
  }

  @Operation(summary = "Delete Node", description = NodeConstants.DELETE_NODE_DESC)
  @ApiResponse(responseCode = "200", description = NodeConstants.DELETE_NODE_SUCCESS)
  @DeleteNodeDoc
  @DeleteMapping("/{nodeId}/{orgId}")
  public ResponseEntity<BaseResponse<NodeResponse>> deleteNode(
      @NotBlank(message = "nodeId can't be empty")
          @Parameter(description = NodeConstants.NODE_ID, example = NodeConstants.NODE_ID_EXAMPLE)
          @PathVariable
          String nodeId,
      @NotBlank(message = "orgId can't be empty")
          @Parameter(description = NodeConstants.ORG_ID, example = NodeConstants.ORG_ID_EXAMPLE)
          @PathVariable
          String orgId)
      throws NodeDomainException, CommonServiceException {
    logger.debug("Processing delete node");
    try {

      var nodeResponse = nodeService.deleteNode(nodeId, orgId);
      logger.info("Response after deletion of node :{}", nodeResponse);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node deleted successfully")
              .payload(nodeResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to delete node");
      throw e;
    }
  }

  @GetNodeListDoc
  @GetMapping("/{orgId}")
  public ResponseEntity<BaseResponse<PagePayload<NodeDto>>> getNodeList(
      @NotBlank(message = "orgId can't be empty")
          @Parameter(description = NodeConstants.ORG_ID, example = NodeConstants.ORG_ID_EXAMPLE)
          @PathVariable
          String orgId,
      PageParams pageParams)
      throws CommonServiceException, NodeDomainException {
    logger.debug("Processing get node list for an orgId");

    Page<NodeDto> nodeDtoPage = nodeService.getNodeListByOrgId(orgId, null, null, pageParams);

    PagePayload<NodeDto> pagePayload = setNodePagePayload(nodeDtoPage, pageParams, orgId);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node List fetched successfully")
            .payload(pagePayload)
            .build());
  }

  @GetNodeListV2Doc
  @GetMapping("/{orgId}/v2")
  public ResponseEntity<BaseResponse<PagePayload<NodeDto>>> getNodeListV2(
      @NotBlank(message = "orgId can't be empty")
          @Parameter(description = NodeConstants.ORG_ID, example = NodeConstants.ORG_ID_EXAMPLE)
          @PathVariable
          String orgId,
      @RequestParam(required = false) String nodeIds,
      @RequestParam(required = false) String nodeType,
      PageParams pageParams)
      throws CommonServiceException, NodeDomainException {
    logger.debug("Processing get node list v2 for an orgId");

    Page<NodeDto> nodeDtoPage =
        nodeService.getNodeListByOrgId(orgId, nodeIds, nodeType, pageParams);

    PagePayload<NodeDto> pagePayload = setNodePagePayload(nodeDtoPage, pageParams, orgId);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node List fetched successfully")
            .payload(pagePayload)
            .build());
  }

  @GetAllNodeList
  @GetMapping("/all-nodes")
  public ResponseEntity<BaseResponse<PagePayload<NodeDto>>> getAllNodesList(PageParams pageParams)
      throws NodeDomainException {
    logger.debug("Processing get node list for an orgId");

    Page<NodeDto> nodeDtoPage =
        nodeService.getAllNodes(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            pageParams.getPageSize().orElse(pageProperties.getPageSize()),
            pageParams.getSortBy().orElse(NODE_DEFAULT_SORT_BY),
            pageParams.getSortOrder().orElse(DEFAULT_SORT_ORDER));

    PagePayload<NodeDto> pagePayload = setNodePagePayload(nodeDtoPage, pageParams);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node List fetched successfully")
            .payload(pagePayload)
            .build());
  }

  @GetNodeCacheKeysDoc
  @GetMapping("/get-all-cache-keys")
  public ResponseEntity<BaseResponse<List<NodeCacheKeyDto>>> getNodeCacheKeys(
      @RequestParam Integer limit) throws NodeDomainException {
    logger.debug("Processing get Node Cache Keys");

    var response = nodeService.getAllNodeCacheKeys(limit);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Cache Keys fetched successfully")
            .payload(response)
            .build());
  }

  private PagePayload<NodeDto> setNodePagePayload(
      Page<NodeDto> nodeDtoPage, PageParams pageParams, @NotBlank String orgId) {
    PagePayload<NodeDto> pagePayload = new PagePayload<>();
    var pagination = new PagePayload.Pagination();
    pagination.setTotalRecords((int) nodeDtoPage.getTotalElements());
    pagination.setTotalPages(nodeDtoPage.getTotalPages());
    pagination.setCurrentPage(pageParams.getPageNo().orElse(pageProperties.getPageNo()));
    pagination.setSortOrder(pageParams.getSortOrder().orElse(DEFAULT_SORT_ORDER));
    pagination.setSortBy(pageParams.getSortBy().orElse(NODE_DEFAULT_SORT_BY));

    String nextUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            nodeDtoPage.getTotalPages(),
            "next",
            PAGINATION_URL.formatted(
                orgId,
                (pageParams.getPageNo().orElse(pageProperties.getPageNo()) + 1),
                pageParams.getPageSize().orElse(pageProperties.getPageSize())));
    String previousUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            nodeDtoPage.getTotalPages(),
            "previous",
            PAGINATION_URL.formatted(
                orgId,
                (pageParams.getPageNo().orElse(pageProperties.getPageNo()) - 1),
                pageParams.getPageSize().orElse(pageProperties.getPageSize())));
    pagination.setNext(nextUri);
    pagination.setPrevious(previousUri);
    pagePayload.setPagination(pagination);
    pagePayload.setData(nodeDtoPage.getContent());

    return pagePayload;
  }

  private PagePayload<NodeDto> setNodePagePayload(
      Page<NodeDto> nodeDtoPage, PageParams pageParams) {
    PagePayload<NodeDto> pagePayload = new PagePayload<>();
    var pagination = new PagePayload.Pagination();
    pagination.setTotalRecords((int) nodeDtoPage.getTotalElements());
    pagination.setTotalPages(nodeDtoPage.getTotalPages());
    pagination.setCurrentPage(pageParams.getPageNo().orElse(pageProperties.getPageNo()));
    pagination.setSortOrder(pageParams.getSortOrder().orElse(DEFAULT_SORT_ORDER));
    pagination.setSortBy(pageParams.getSortBy().orElse(NODE_DEFAULT_SORT_BY));

    String nextUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            nodeDtoPage.getTotalPages(),
            "next",
            PAGINATION_URL_ALL_NODES.formatted(
                (pageParams.getPageNo().orElse(pageProperties.getPageNo()) + 1),
                pageParams.getPageSize().orElse(pageProperties.getPageSize())));
    String previousUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            nodeDtoPage.getTotalPages(),
            "previous",
            PAGINATION_URL_ALL_NODES.formatted(
                (pageParams.getPageNo().orElse(pageProperties.getPageNo()) - 1),
                pageParams.getPageSize().orElse(pageProperties.getPageSize())));
    pagination.setNext(nextUri);
    pagination.setPrevious(previousUri);
    pagePayload.setPagination(pagination);
    pagePayload.setData(nodeDtoPage.getContent());

    return pagePayload;
  }

  @GetNodeListV1Doc
  @GetMapping("/{orgId}/v1")
  public ResponseEntity<BaseResponse<PagePayload<NodeDto>>> getNodeListV1(
      @NotBlank(message = "orgId can't be empty")
          @Parameter(description = NodeConstants.ORG_ID, example = NodeConstants.ORG_ID_EXAMPLE)
          @PathVariable
          String orgId,
      PageParams pageParams)
      throws NodeDomainException, CommonServiceException {
    logger.debug("Processing get node list for an orgId");

    Page<NodeDto> nodeDtoPage = nodeService.getNodeListByOrgIdV1(orgId, pageParams);

    PagePayload<NodeDto> pagePayload = setNodePagePayload(nodeDtoPage, pageParams, orgId);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node List fetched successfully")
            .payload(pagePayload)
            .build());
  }

  @GetMapping("/node-types/{orgId}")
  @GetNodeTypesDoc
  public ResponseEntity<BaseResponse<NodeTypesResponse>> getAllNodeTypes(@PathVariable String orgId)
      throws CommonServiceException {
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node types fetched successfully")
            .payload(nodeService.getAllNodeTypesByOrgId(orgId))
            .build());
  }

  @GetMapping("/check-nodes-exist/orgId/{orgId}")
  @CheckNodesExistDoc
  public ResponseEntity<BaseResponse<List<String>>> checkNodesExistByNodeIdsAndOrgId(
      @RequestBody List<String> nodeIds, @PathVariable String orgId) {
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node ids fetched successfully")
            .payload(nodeService.checkNodesExistByNodeIdsAndOrgId(nodeIds, orgId))
            .build());
  }
}
