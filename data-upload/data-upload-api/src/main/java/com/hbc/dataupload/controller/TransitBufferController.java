package com.hbc.dataupload.controller;

import com.hbc.common.base.PagePayload;
import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.common.pojo.PageParams;
import com.hbc.common.response.BaseResponse;
import com.hbc.common.util.PaginationUtil;
import com.hbc.dataupload.common.outbound.TransitBufferDetailsResponse;
import com.hbc.dataupload.domain.pojo.TransitTimeBufferPageProperties;
import com.hbc.dataupload.service.TransitTimeBufferService;
import com.hbc.jobs.framework.common.domain.pojo.DefaultPageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ui/transit-time-buffer-page")
@RequiredArgsConstructor
public class TransitBufferController {

  private final TransitTimeBufferService transitTimeBufferService;
  private final TransitTimeBufferPageProperties transitTimeBufferPageProperties;
  private final DefaultPageProperties defaultPageProperties;
  private final Logger logger = LoggerFactory.getLogger(TransitBufferController.class);
  private static final String PAGINATION_URL =
      "/data-upload/ui/transit-time-buffer/%s?pageNo=%d&pageSize=%d&sortBy=%s&sortOrder=%s";

  @GetMapping("/{orgId}")
  public ResponseEntity<BaseResponse<PagePayload<TransitBufferDetailsResponse>>>
      getTransitTimeBufferDetails(@PathVariable String orgId, PageParams pageParams) {
    logger.debug("Processing get transit time buffer details");

    PagePayload<TransitBufferDetailsResponse> pagePayload =
        transitTimeBufferService.getTransitTimeBufferDetails(
            orgId,
            pageParams.getPageNo().orElse(defaultPageProperties.getPageNo()),
            pageParams.getPageSize().orElse(defaultPageProperties.getPageSize()),
            pageParams.getSortBy().orElse(transitTimeBufferPageProperties.getSortBy()),
            pageParams.getSortOrder().orElse(transitTimeBufferPageProperties.getSortOrder()));

    updatePaginationDetails(pagePayload, orgId, pageParams);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Transit time buffers fetched successfully.")
            .payload(pagePayload)
            .build());
  }

  private void updatePaginationDetails(
      PagePayload<TransitBufferDetailsResponse> pagePayload, String orgId, PageParams pageParams) {
    int currentPage = pagePayload.getPagination().getCurrentPage();
    String nextUri =
        PaginationUtil.buildUriForPagination(
            currentPage,
            pagePayload.getPagination().getTotalPages(),
            "next",
            String.format(
                PAGINATION_URL,
                orgId,
                currentPage + 1,
                pageParams.getPageSize().orElse(defaultPageProperties.getPageSize()),
                pagePayload.getPagination().getSortBy(),
                pagePayload.getPagination().getSortOrder()));

    String previousUri =
        PaginationUtil.buildUriForPagination(
            currentPage,
            pagePayload.getPagination().getTotalPages(),
            "previous",
            String.format(
                PAGINATION_URL,
                orgId,
                currentPage - 1,
                pageParams.getPageSize().orElse(defaultPageProperties.getPageSize()),
                pagePayload.getPagination().getSortBy(),
                pagePayload.getPagination().getSortOrder()));

    pagePayload.getPagination().setNext(nextUri);
    pagePayload.getPagination().setPrevious(previousUri);
  }
}
