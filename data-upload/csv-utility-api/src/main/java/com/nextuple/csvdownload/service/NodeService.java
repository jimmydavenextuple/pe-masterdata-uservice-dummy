package com.nextuple.csvdownload.service;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.node.domain.dto.NodeDto;
import com.nextuple.node.domain.feign.NodeFeign;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NodeService {

  private final Logger logger = LoggerFactory.getLogger(NodeService.class);
  private final NodeFeign nodeFeign;

  @Value("${download-page-size.node}")
  private int pageSize;

  public List<NodeDto> getNodeList(String orgId) {
    logger.debug("Processing get nodes list from pagination");
    PagePayload<NodeDto> pagePayload =
        nodeFeign.getNodeList(orgId, 1, pageSize, null, null).getPayload();

    int totalPages = pagePayload.getPagination().getTotalPages();
    int currentPageNo = pagePayload.getPagination().getCurrentPage();

    List<NodeDto> nodeDtoList = new ArrayList<>(pagePayload.getData());

    while (currentPageNo < totalPages) {
      currentPageNo = currentPageNo + 1;
      PagePayload<NodeDto> pagePayload1 =
          nodeFeign.getNodeList(orgId, currentPageNo, pageSize, null, null).getPayload();
      nodeDtoList.addAll(pagePayload1.getData());
    }

    return nodeDtoList;
  }
}
