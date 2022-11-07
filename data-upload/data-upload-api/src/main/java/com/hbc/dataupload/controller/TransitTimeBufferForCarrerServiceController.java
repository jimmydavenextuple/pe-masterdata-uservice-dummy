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
import com.hbc.transit.domain.outbound.TransitBufferConfigResponse;
import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ui/v1/transit-buffer")
@RequiredArgsConstructor
public class TransitTimeBufferForCarrerServiceController {

  private final TransitTimeBufferService transitTimeBufferService;
  private final TransitTimeBufferPageProperties transitTimeBufferPageProperties;
  private final DefaultPageProperties defaultPageProperties;
  private final Logger logger =
      LoggerFactory.getLogger(TransitTimeBufferForCarrerServiceController.class);
  private static final String PAGINATION_URL =
      "/data-upload/ui/v1/transit-buffer/%s?pageNo=%d&pageSize=%d&sortBy=%s&sortOrder=%s";

  @GetMapping("/{orgId}")
  public ResponseEntity<BaseResponse<PagePayload<TransitBufferDetailsResponse>>>
      getTransitTimeBufferDetailsForCarrierServices(
          @PathVariable String orgId, PageParams pageParams) {
    logger.debug("Processing get transit time buffer details");

    PagePayload<TransitBufferDetailsResponse> pagePayload =
        transitTimeBufferService.getTransitTimeBufferDetailsForCarrierServices(
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

  @GetMapping("/request/{orgId}")
  public ResponseEntity<BaseResponse<List<TransitBufferConfigResponse>>>
      getTransitBufferConfigRequests(
          @PathVariable String orgId,
          @NotBlank(message = "carrierServiceId can't be blank") @RequestParam
              String carrierServiceId) {
    logger.debug("Processing get transit buffer config requests");
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Transit Buffer Configuration fetched successfully.")
            .payload(
                transitTimeBufferService.getTransitBufferConfigRequests(orgId, carrierServiceId))
            .build());
  }
}
