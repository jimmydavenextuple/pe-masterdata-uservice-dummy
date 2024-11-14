/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.service;

import static com.nextuple.dataupload.controller.TransferSchedulesController.TRANSFER_SCHEDULE_DEFAULT_SORT_BY;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.util.PaginationUtil;
import com.nextuple.transit.domain.feign.TransferScheduleFeign;
import com.nextuple.transit.domain.inbound.FetchTransferScheduleRequest;
import com.nextuple.transit.domain.outbound.TransferScheduleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransferScheduleService {
  private final TransferScheduleFeign transferScheduleFeign;

  private static final String PAGINATION_URL =
      "transfer-schedule/ui/orgId/%s?pageNo=%d&pageSize=%d";

  public PagePayload<TransferScheduleResponse> getTransferScheduleList(
      String orgId, PageParams pageParams, FetchTransferScheduleRequest request) {
    Integer pageNo = pageParams.getPageNo().orElse(1);
    Integer pageSize = pageParams.getPageSize().orElse(10);
    String sortBy = pageParams.getSortBy().orElse(TRANSFER_SCHEDULE_DEFAULT_SORT_BY);
    String sortOrder = pageParams.getSortOrder().orElse("ASC");

    BaseResponse<PagePayload<TransferScheduleResponse>> response =
        transferScheduleFeign.fetchTransferSchedule(
            orgId, true, pageNo, pageSize, sortBy, sortOrder, request);
    updatePaginationUrls(orgId, pageParams, response);

    return response.getPayload();
  }

  private static void updatePaginationUrls(
      String orgId,
      PageParams pageParams,
      BaseResponse<PagePayload<TransferScheduleResponse>> response) {
    String nextUri =
        PaginationUtil.buildUriForPagination(
            response.getPayload().getPagination().getCurrentPage(),
            response.getPayload().getPagination().getTotalPages(),
            "next",
            PAGINATION_URL.formatted(
                orgId,
                response.getPayload().getPagination().getCurrentPage() + 1,
                pageParams.getPageSize().orElse(10)));
    String previousUri =
        PaginationUtil.buildUriForPagination(
            response.getPayload().getPagination().getCurrentPage(),
            response.getPayload().getPagination().getTotalPages(),
            "previous",
            PAGINATION_URL.formatted(
                orgId,
                response.getPayload().getPagination().getCurrentPage() - 1,
                pageParams.getPageSize().orElse(10)));

    response.getPayload().getPagination().setNext(nextUri);
    response.getPayload().getPagination().setPrevious(previousUri);
  }
}
