package com.hbc.dataupload.controller;

import static com.hbc.common.constants.CommonConstants.DEFAULT_SORT_ORDER;
import static com.hbc.common.constants.CommonConstants.NODE_DEFAULT_SORT_BY;

import com.hbc.common.base.PagePayload;
import com.hbc.common.pojo.PageParams;
import com.hbc.common.pojo.PageProperties;
import com.hbc.common.response.BaseResponse;
import com.hbc.common.util.PaginationUtil;
import com.hbc.dataupload.domain.dto.NodeServiceOptionDto;
import com.hbc.dataupload.service.NodeServiceOptionService;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Validated
@Controller
@RequestMapping("/ui/node-service-option")
@Slf4j
@RequiredArgsConstructor
public class NodeServiceOptionController {
  private static final String PAGINATION_URL =
      "/data-upload/ui/node-service-option/orgId/%s?pageNo=%d&pageSize=%d";
  private final NodeServiceOptionService nodeServiceOptionService;
  private final PageProperties pageProperties;

  @GetMapping("/orgId/{orgId}")
  public ResponseEntity<BaseResponse<PagePayload<NodeServiceOptionDto>>> getNodeServiceOption(
      @PathVariable @NotBlank(message = "orgId can't be empty") String orgId,
      PageParams pageParams) {

    PagePayload<NodeServiceOptionDto> nodeServiceOptionDto =
        nodeServiceOptionService.getNodeServiceOption(
            orgId,
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            pageParams.getPageSize().orElse(pageProperties.getPageSize()),
            pageParams.getSortBy().orElse(NODE_DEFAULT_SORT_BY),
            pageParams.getSortOrder().orElse(DEFAULT_SORT_ORDER));

    String nextUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            nodeServiceOptionDto.getPagination().getTotalPages(),
            "next",
            String.format(
                PAGINATION_URL,
                orgId,
                (pageParams.getPageNo().orElse(pageProperties.getPageNo()) + 1),
                pageParams.getPageSize().orElse(pageProperties.getPageSize())));

    String previousUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            nodeServiceOptionDto.getPagination().getTotalPages(),
            "previous",
            String.format(
                PAGINATION_URL,
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
