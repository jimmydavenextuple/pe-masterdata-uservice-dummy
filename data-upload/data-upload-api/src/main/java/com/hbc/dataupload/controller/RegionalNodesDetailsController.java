package com.hbc.dataupload.controller;

import static com.hbc.common.constants.CommonConstants.DEFAULT_SORT_ORDER;
import static com.hbc.common.constants.CommonConstants.NODE_DEFAULT_SORT_BY;

import com.hbc.common.base.PagePayload;
import com.hbc.common.pojo.PageParams;
import com.hbc.common.pojo.PageProperties;
import com.hbc.common.response.BaseResponse;
import com.hbc.common.util.PaginationUtil;
import com.hbc.dataupload.domain.dto.NodeListDto;
import com.hbc.dataupload.service.RegionalNodesDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/data-upload/ui")
@RequiredArgsConstructor
@Slf4j
public class RegionalNodesDetailsController {
  private static final String PAGINATION_URL =
      "/data-upload/ui/regions-nodes/nodes/orgId/%s?pageNo=%d&pageSize=%d";
  private final PageProperties pageProperties;

  private final RegionalNodesDetailsService regionalNodesDetailsService;

  @GetMapping("/regions-nodes/nodes/orgId/{orgId}")
  public ResponseEntity<BaseResponse<PagePayload<NodeListDto>>> getNodesList(
      @PathVariable String orgId, PageParams pageParams) {
    PagePayload<NodeListDto> nodeListDto =
        regionalNodesDetailsService.getNodesList(
            orgId,
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            pageParams.getPageSize().orElse(pageProperties.getPageSize()),
            pageParams.getSortBy().orElse(NODE_DEFAULT_SORT_BY),
            pageParams.getSortOrder().orElse(DEFAULT_SORT_ORDER));

    String nextUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            nodeListDto.getPagination().getTotalPages(),
            "next",
            String.format(
                PAGINATION_URL,
                orgId,
                (pageParams.getPageNo().orElse(pageProperties.getPageNo()) + 1),
                pageParams.getPageSize().orElse(pageProperties.getPageSize())));

    String previousUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            nodeListDto.getPagination().getTotalPages(),
            "previous",
            String.format(
                PAGINATION_URL,
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
