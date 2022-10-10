package com.hbc.dataupload.controller;

import static com.hbc.common.constants.CommonConstants.DEFAULT_SORT_ORDER;
import static com.hbc.common.constants.CommonConstants.NODE_DEFAULT_SORT_BY;

import com.hbc.common.base.PagePayload;
import com.hbc.common.pojo.PageParams;
import com.hbc.common.pojo.PageProperties;
import com.hbc.common.response.BaseResponse;
import com.hbc.common.util.PaginationUtil;
import com.hbc.dataupload.domain.dto.ProcessingTimeBufferDto;
import com.hbc.dataupload.service.ProcessingTimeBufferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ui/processing-time-buffer")
@RequiredArgsConstructor
@Slf4j
public class ProcessingTimeBufferController {

  private static final String PAGINATION_URL =
      "/data-upload/ui/processing-time-buffer/orgId/%s?pageNo=%d&pageSize=%d";
  private final PageProperties pageProperties;

  private final ProcessingTimeBufferService processingTimeBufferService;

  @GetMapping("/orgId/{orgId}")
  public ResponseEntity<BaseResponse<PagePayload<ProcessingTimeBufferDto>>>
      getProcessingTimeBufferDetails(@PathVariable String orgId, PageParams pageParams) {
    PagePayload<ProcessingTimeBufferDto> processingTimeBufferDtoPagePayload =
        processingTimeBufferService.getProcessingTimeBuffers(
            orgId,
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            pageParams.getPageSize().orElse(pageProperties.getPageSize()),
            pageParams.getSortBy().orElse(NODE_DEFAULT_SORT_BY),
            pageParams.getSortOrder().orElse(DEFAULT_SORT_ORDER));

    String nextUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            processingTimeBufferDtoPagePayload.getPagination().getTotalPages(),
            "next",
            String.format(
                PAGINATION_URL,
                orgId,
                (pageParams.getPageNo().orElse(pageProperties.getPageNo()) + 1),
                pageParams.getPageSize().orElse(pageProperties.getPageSize())));

    String previousUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            processingTimeBufferDtoPagePayload.getPagination().getTotalPages(),
            "previous",
            String.format(
                PAGINATION_URL,
                orgId,
                (pageParams.getPageNo().orElse(pageProperties.getPageNo()) - 1),
                pageParams.getPageSize().orElse(pageProperties.getPageSize())));

    processingTimeBufferDtoPagePayload.getPagination().setNext(nextUri);
    processingTimeBufferDtoPagePayload.getPagination().setPrevious(previousUri);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Processing Time Buffer list fetched successfully")
            .payload(processingTimeBufferDtoPagePayload)
            .build());
  }
}
