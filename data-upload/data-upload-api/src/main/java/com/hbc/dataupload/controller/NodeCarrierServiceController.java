package com.hbc.dataupload.controller;

import com.hbc.common.base.PagePayload;
import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.common.pojo.PageParams;
import com.hbc.common.response.BaseResponse;
import com.hbc.common.util.PaginationUtil;
import com.hbc.dataupload.domain.dto.NodeCarrierServiceResponse;
import com.hbc.dataupload.domain.pojo.NodeCarrierServicePageProperties;
import com.hbc.dataupload.service.NodeCarrierServiceDetailsService;
import com.hbc.jobs.framework.common.domain.pojo.DefaultPageProperties;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ui/node-carrier-service")
public class NodeCarrierServiceController {

  private final Logger logger = LoggerFactory.getLogger(NodeCarrierServiceController.class);
  private final NodeCarrierServiceDetailsService nodeCarrierServiceDetailsService;
  private final NodeCarrierServicePageProperties pageProperties;
  private final DefaultPageProperties defaultPageProperties;
  private static final String PAGINATION_URL =
      "/data-upload/ui/node-carrier-service/%s?pageNo=%d&pageSize=%d&sortBy=%s&sortOrder=%s";

  @GetMapping("/{orgId}")
  public ResponseEntity<BaseResponse<PagePayload<NodeCarrierServiceResponse>>>
      getNodeServiceOptionDetails(@PathVariable @NotBlank String orgId, PageParams pageParams) {
    logger.debug("Processing get list of nodes and carrier service details");

    PagePayload<NodeCarrierServiceResponse> nodeCarrierServiceResponse =
        nodeCarrierServiceDetailsService.getNodeCarrierServiceDetails(
            orgId,
            pageParams.getPageNo().orElse(defaultPageProperties.getPageNo()),
            pageParams.getPageSize().orElse(defaultPageProperties.getPageSize()),
            pageParams.getSortBy().orElse(pageProperties.getSortBy()),
            pageParams.getSortOrder().orElse(pageProperties.getSortOrder()));

    int currentPage = nodeCarrierServiceResponse.getPagination().getCurrentPage();
    String nextUri =
        PaginationUtil.buildUriForPagination(
            currentPage,
            nodeCarrierServiceResponse.getPagination().getTotalPages(),
            "next",
            String.format(
                PAGINATION_URL,
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
            String.format(
                PAGINATION_URL,
                orgId,
                currentPage - 1,
                pageParams.getPageSize().orElse(defaultPageProperties.getPageSize()),
                nodeCarrierServiceResponse.getPagination().getSortBy(),
                nodeCarrierServiceResponse.getPagination().getSortOrder()));

    nodeCarrierServiceResponse.getPagination().setNext(nextUri);
    nodeCarrierServiceResponse.getPagination().setPrevious(previousUri);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node and Carrier Service List fetched successfully")
            .payload(nodeCarrierServiceResponse)
            .build());
  }
}
