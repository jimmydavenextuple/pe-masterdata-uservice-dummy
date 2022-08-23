package com.hbc.dataupload.controller;

import com.hbc.common.base.PagePayload;
import com.hbc.common.pojo.PageParams;
import com.hbc.common.pojo.PageProperties;
import com.hbc.common.response.BaseResponse;
import com.hbc.common.util.PaginationUtil;
import com.hbc.dataupload.domian.dto.NodeServiceOptionDto;
import com.hbc.dataupload.service.NodeServiceOptionService;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ui/node-service-option")
@Slf4j
@RequiredArgsConstructor
public class NodeServiceOptionController {
  private static final String DEFAULT_SORT_BY = "nodeId";
  private static final String DEFAULT_SORT_ORDER = "ASC";
  private final NodeServiceOptionService nodeServiceOptionService;
  private final PageProperties pageProperties;

  @GetMapping("/orgId/{orgId}")
  public ResponseEntity<BaseResponse<PagePayload<NodeServiceOptionDto>>> getNodeServiceOption(
      @PathVariable @NotBlank String orgId, PageParams pageParams) {

    PagePayload<NodeServiceOptionDto> nodeServiceOptionDto =
        nodeServiceOptionService.getNodeServiceOption(
            orgId,
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            pageParams.getPageSize().orElse(pageProperties.getPageSize()),
            pageParams.getSortBy().orElse(DEFAULT_SORT_BY),
            pageParams.getSortOrder().orElse(DEFAULT_SORT_ORDER));

    String nextUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            nodeServiceOptionDto.getPagination().getTotalPages(),
            "next",
            String.format(
                "/data-upload/ui/node-service-option/orgId/%s?pageNo=%d&pageSize=%d",
                orgId,
                (pageParams.getPageNo().orElse(pageProperties.getPageNo()) + 1),
                pageParams.getPageSize().orElse(pageProperties.getPageSize())));

    String previousUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            nodeServiceOptionDto.getPagination().getTotalPages(),
            "previous",
            String.format(
                "/data-upload/ui/node-service-option/orgId/%s?pageNo=%d&pageSize=%d",
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
