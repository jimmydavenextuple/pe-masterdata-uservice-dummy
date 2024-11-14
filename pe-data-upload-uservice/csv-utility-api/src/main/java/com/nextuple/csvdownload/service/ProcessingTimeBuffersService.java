/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.service;

import com.nextuple.common.base.PagePayload;
import com.nextuple.dataupload.common.feign.DataUploadFeign;
import com.nextuple.dataupload.common.outbound.ProcessingTimeBufferResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessingTimeBuffersService {
  private final DataUploadFeign dataUploadFeign;

  @Value("${download-page-size.node}")
  private int pageSize;

  public List<ProcessingTimeBufferResponse> getProcessingTimeBuffers(String orgId, String nodeIds) {
    PagePayload<ProcessingTimeBufferResponse> pagePayload =
        dataUploadFeign
            .getProcessingTimeBufferDetailsV1(orgId, nodeIds, null, pageSize, null, null)
            .getPayload();
    int totalPages = pagePayload.getPagination().getTotalPages();
    int currentPageNo = pagePayload.getPagination().getCurrentPage();
    List<ProcessingTimeBufferResponse> processingTimeBufferResponseList =
        new ArrayList<>(pagePayload.getData());

    while (currentPageNo < totalPages) {
      currentPageNo = currentPageNo + 1;
      PagePayload<ProcessingTimeBufferResponse> pagePayload1 =
          dataUploadFeign
              .getProcessingTimeBufferDetailsV1(orgId, nodeIds, currentPageNo, pageSize, null, null)
              .getPayload();
      processingTimeBufferResponseList.addAll(pagePayload1.getData());
    }
    return processingTimeBufferResponseList;
  }
}
