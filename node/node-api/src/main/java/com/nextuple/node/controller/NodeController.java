package com.nextuple.node.controller;

import static com.nextuple.common.constants.CommonConstants.DEFAULT_SORT_ORDER;
import static com.nextuple.common.constants.CommonConstants.NODE_DEFAULT_SORT_BY;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.pojo.PageProperties;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.util.PaginationUtil;
import com.nextuple.node.domain.dto.NodeCacheKeyDto;
import com.nextuple.node.domain.dto.NodeDto;
import com.nextuple.node.domain.inbound.NodeRequest;
import com.nextuple.node.domain.inbound.NodeUpdationRequest;
import com.nextuple.node.domain.outbound.NodeResponse;
import com.nextuple.node.exception.NodeDomainException;
import com.nextuple.node.service.NodeService;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
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
@RequestMapping("/node")
@RequiredArgsConstructor
public class NodeController {

  private static final Logger logger = LoggerFactory.getLogger(NodeController.class);
  private static final String PAGINATION_URL = "/%s?pageNo=%d&pageSize=%d";
  private static final String PAGINATION_URL_ALL_NODES = "?pageNo=%d&pageSize=%d";
  private final NodeService nodeService;
  private final PageProperties pageProperties;

  @PostMapping
  public ResponseEntity<BaseResponse<NodeResponse>> createNode(
      @Valid @RequestBody NodeRequest nodeRequest)
      throws NodeDomainException, CommonServiceException {
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

  @PutMapping("/{nodeId}/{orgId}")
  public ResponseEntity<BaseResponse<NodeResponse>> updateNodeDetails(
      @NotBlank(message = "nodeId can't be empty") @PathVariable String nodeId,
      @NotBlank(message = "orgId can't be empty") @PathVariable String orgId,
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
  public ResponseEntity<BaseResponse<NodeResponse>> getNodeDetails(
      @NotBlank(message = "nodeId can't be empty") @PathVariable String nodeId,
      @NotBlank(message = "orgId can't be empty") @PathVariable String orgId)
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

  @DeleteMapping("/{nodeId}/{orgId}")
  public ResponseEntity<BaseResponse<NodeResponse>> deleteNode(
      @NotBlank(message = "nodeId can't be empty") @PathVariable String nodeId,
      @NotBlank(message = "orgId can't be empty") @PathVariable String orgId)
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

  @GetMapping("/{orgId}")
  public ResponseEntity<BaseResponse<PagePayload<NodeDto>>> getNodeList(
      @NotBlank(message = "orgId can't be empty") @PathVariable String orgId, PageParams pageParams)
      throws NodeDomainException, CommonServiceException {
    logger.debug("Processing get node list for an orgId");

    Page<NodeDto> nodeDtoPage =
        nodeService.getNodeListByOrgId(
            orgId,
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            pageParams.getPageSize().orElse(pageProperties.getPageSize()),
            pageParams.getSortBy().orElse(NODE_DEFAULT_SORT_BY),
            pageParams.getSortOrder().orElse(DEFAULT_SORT_ORDER));

    PagePayload<NodeDto> pagePayload = setNodePagePayload(nodeDtoPage, pageParams, orgId);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node List fetched successfully")
            .payload(pagePayload)
            .build());
  }

  @GetMapping("/all-nodes")
  public ResponseEntity<BaseResponse<PagePayload<NodeResponse>>> getAllNodesList(
      PageParams pageParams) throws NodeDomainException {
    logger.debug("Processing get node list for an orgId");

    Page<NodeResponse> nodeResponsePage =
        nodeService.getAllNodes(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            pageParams.getPageSize().orElse(pageProperties.getPageSize()),
            pageParams.getSortBy().orElse(NODE_DEFAULT_SORT_BY),
            pageParams.getSortOrder().orElse(DEFAULT_SORT_ORDER));

    PagePayload<NodeResponse> pagePayload = setNodePagePayload(nodeResponsePage, pageParams);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node List fetched successfully")
            .payload(pagePayload)
            .build());
  }

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
            String.format(
                PAGINATION_URL,
                orgId,
                (pageParams.getPageNo().orElse(pageProperties.getPageNo()) + 1),
                pageParams.getPageSize().orElse(pageProperties.getPageSize())));
    String previousUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            nodeDtoPage.getTotalPages(),
            "previous",
            String.format(
                PAGINATION_URL,
                orgId,
                (pageParams.getPageNo().orElse(pageProperties.getPageNo()) - 1),
                pageParams.getPageSize().orElse(pageProperties.getPageSize())));
    pagination.setNext(nextUri);
    pagination.setPrevious(previousUri);
    pagePayload.setPagination(pagination);
    pagePayload.setData(nodeDtoPage.getContent());

    return pagePayload;
  }

  private PagePayload<NodeResponse> setNodePagePayload(
      Page<NodeResponse> nodeResponsesPage, PageParams pageParams) {
    PagePayload<NodeResponse> pagePayload = new PagePayload<>();
    var pagination = new PagePayload.Pagination();
    pagination.setTotalRecords((int) nodeResponsesPage.getTotalElements());
    pagination.setTotalPages(nodeResponsesPage.getTotalPages());
    pagination.setCurrentPage(pageParams.getPageNo().orElse(pageProperties.getPageNo()));
    pagination.setSortOrder(pageParams.getSortOrder().orElse(DEFAULT_SORT_ORDER));
    pagination.setSortBy(pageParams.getSortBy().orElse(NODE_DEFAULT_SORT_BY));

    String nextUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            nodeResponsesPage.getTotalPages(),
            "next",
            String.format(
                PAGINATION_URL_ALL_NODES,
                (pageParams.getPageNo().orElse(pageProperties.getPageNo()) + 1),
                pageParams.getPageSize().orElse(pageProperties.getPageSize())));
    String previousUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            nodeResponsesPage.getTotalPages(),
            "previous",
            String.format(
                PAGINATION_URL_ALL_NODES,
                (pageParams.getPageNo().orElse(pageProperties.getPageNo()) - 1),
                pageParams.getPageSize().orElse(pageProperties.getPageSize())));
    pagination.setNext(nextUri);
    pagination.setPrevious(previousUri);
    pagePayload.setPagination(pagination);
    pagePayload.setData(nodeResponsesPage.getContent());

    return pagePayload;
  }
}
