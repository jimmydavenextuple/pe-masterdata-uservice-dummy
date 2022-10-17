package com.hbc.csvdownload.service;

import com.hbc.common.base.PagePayload;
import com.hbc.node.domain.dto.NodeDto;
import com.hbc.node.domain.feign.NodeFeign;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NodeService {
  private final NodeFeign nodeFeign;

  @Value("${download-page-size.node}")
  private int pageSize;

  public List<NodeDto> getNodeList(String orgId) {
    PagePayload<NodeDto> nodeDtoPagePayload =
        nodeFeign.getNodeList(orgId, null, pageSize, null, null).getPayload();
    int totalPages = nodeDtoPagePayload.getPagination().getTotalPages();
    int currentPageNo = nodeDtoPagePayload.getPagination().getCurrentPage();
    List<NodeDto> nodeDtoList = new ArrayList<>(nodeDtoPagePayload.getData());

    while (currentPageNo <= totalPages) {
      currentPageNo += 1;
      PagePayload<NodeDto> nodeDtoPagePayload1 =
          nodeFeign.getNodeList(orgId, currentPageNo, pageSize, null, null).getPayload();
      nodeDtoList.addAll(nodeDtoPagePayload1.getData());
    }
    return nodeDtoList;
  }
}
