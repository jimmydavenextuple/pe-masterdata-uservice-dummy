/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.service;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.node.domain.dto.NodeDto;
import com.nextuple.node.domain.feign.NodeFeign;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NodeResponseService {

  private final Logger logger = LoggerFactory.getLogger(NodeResponseService.class);
  private final NodeFeign nodeFeign;

  @Value("${download-page-size.node}")
  private int pageSize;

  public List<NodeDto> getNodeList(String orgId, String nodeIds, String nodeType) {
    logger.debug("Processing get nodes list from pagination");
    PageParams pageParams =
        new PageParams(Optional.of(1), Optional.of(pageSize), Optional.empty(), Optional.empty());
    PagePayload<NodeDto> pagePayload = getNodeDtoPagePayload(orgId, nodeIds, nodeType, pageParams);
    int totalPages = pagePayload.getPagination().getTotalPages();
    int currentPageNo = pagePayload.getPagination().getCurrentPage();

    List<NodeDto> nodeDtoList = new ArrayList<>(pagePayload.getData());

    while (currentPageNo < totalPages) {
      currentPageNo = currentPageNo + 1;
      PageParams params =
          new PageParams(
              Optional.of(currentPageNo),
              Optional.of(pageSize),
              Optional.empty(),
              Optional.empty());
      PagePayload<NodeDto> pagePayloadForConsecutivePage =
          getNodeDtoPagePayload(orgId, nodeIds, nodeType, params);
      nodeDtoList.addAll(pagePayloadForConsecutivePage.getData());
    }

    return nodeDtoList;
  }

  private PagePayload<NodeDto> getNodeDtoPagePayload(
      String orgId, String nodeIds, String nodeType, PageParams pageParams) {
    Integer pageNo = pageParams.getPageNo().orElse(1);
    Integer sizePage = pageParams.getPageSize().orElse(pageSize);
    String sortBy = pageParams.getSortBy().orElse(null);
    String sortOrder = pageParams.getSortOrder().orElse(null);
    return ObjectUtils.isEmpty(nodeIds) && ObjectUtils.isEmpty(nodeType)
        ? nodeFeign.getNodeList(orgId, pageNo, sizePage, sortBy, sortOrder).getPayload()
        : nodeFeign
            .getNodeListV2(orgId, nodeIds, nodeType, pageNo, sizePage, sortBy, sortOrder)
            .getPayload();
  }
}
