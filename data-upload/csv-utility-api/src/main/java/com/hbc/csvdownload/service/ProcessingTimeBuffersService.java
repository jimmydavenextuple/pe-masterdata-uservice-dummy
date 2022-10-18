package com.hbc.csvdownload.service;

import com.hbc.common.base.PagePayload;
import com.hbc.dataupload.common.feign.DataUploadFeign;
import com.hbc.dataupload.common.outbound.ProcessingTimeBufferResponse;
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

  public List<ProcessingTimeBufferResponse> getProcessingTimeBuffers(String orgId) {
    PagePayload<ProcessingTimeBufferResponse> pagePayload =
        dataUploadFeign
            .getProcessingTimeBufferDetails(orgId, null, pageSize, null, null)
            .getPayload();
    int totalPages = pagePayload.getPagination().getTotalPages();
    int currentPageNo = pagePayload.getPagination().getCurrentPage();
    List<ProcessingTimeBufferResponse> processingTimeBufferResponseList =
        new ArrayList<>(pagePayload.getData());

    while (currentPageNo < totalPages) {
      currentPageNo = currentPageNo + 1;
      PagePayload<ProcessingTimeBufferResponse> pagePayload1 =
          dataUploadFeign
              .getProcessingTimeBufferDetails(orgId, currentPageNo, pageSize, null, null)
              .getPayload();
      processingTimeBufferResponseList.addAll(pagePayload1.getData());
    }
    return processingTimeBufferResponseList;
  }
}
