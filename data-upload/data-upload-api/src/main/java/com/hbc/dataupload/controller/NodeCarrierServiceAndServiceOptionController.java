package com.hbc.dataupload.controller;

import com.hbc.common.base.PagePayload;
import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.common.pojo.PageParams;
import com.hbc.common.response.BaseResponse;
import com.hbc.common.util.PaginationUtil;
import com.hbc.dataupload.common.outbound.NodeCarrierServiceAndServiceOptionResponse;
import com.hbc.dataupload.domain.pojo.NodeCarrierServicePageProperties;
import com.hbc.dataupload.service.NodeCarrierServiceAndServiceOptionService;
import com.hbc.jobs.framework.common.domain.pojo.DefaultPageProperties;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ui/node-carrier-service-option")
@RequiredArgsConstructor
public class NodeCarrierServiceAndServiceOptionController {

  private final NodeCarrierServicePageProperties pageProperties;
  private final DefaultPageProperties defaultPageProperties;
  private final NodeCarrierServiceAndServiceOptionService nodeCarrierServiceAndServiceOptionService;
  private final Logger logger =
      LoggerFactory.getLogger(NodeCarrierServiceAndServiceOptionController.class);
  private static final String PAGINATION_URL =
      "/data-upload/ui/node-carrier-service-option/%s?pageNo=%d&pageSize=%d&sortBy=%s&sortOrder=%s";

  @GetMapping("/{orgId}")
  public ResponseEntity<BaseResponse<PagePayload<NodeCarrierServiceAndServiceOptionResponse>>>
      getListOfNodeCarrierServiceAndServiceOptionDetails(
          @NotBlank @PathVariable String orgId, PageParams pageParams) {

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
  }
}
