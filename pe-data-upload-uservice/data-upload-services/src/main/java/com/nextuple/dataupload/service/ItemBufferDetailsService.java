/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.item.domain.feign.ItemBufferUIFeign;
import com.nextuple.item.domain.outbound.ItemBufferResponse;
import com.nextuple.item.domain.outbound.PageResponseForItemBuffer;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemBufferDetailsService {

  @Autowired ItemBufferUIFeign itemBufferUIFeign;

  public PageResponseForItemBuffer getItemsListWithConfiguredBuffers(
      String orgId, String itemIds, Integer pageNo, Integer pageSize) {
    PageResponseForItemBuffer pageResponseForItemBuffer = null;
    try {
      ResponseEntity<BaseResponse<PageResponseForItemBuffer>> itemBuffersResponseEntity =
          ObjectUtils.isEmpty(itemIds)
              ? itemBufferUIFeign.getItemsListWithConfiguredBuffers(orgId, pageNo, pageSize)
              : itemBufferUIFeign.getItemsListWithConfiguredBuffersV1(
                  orgId, itemIds, pageNo, pageSize);

      if (itemBuffersResponseEntity != null) {
        var responseBody = itemBuffersResponseEntity.getBody();
        if (responseBody != null) {
          pageResponseForItemBuffer = responseBody.getPayload();
        }
      }
    } catch (Exception e) {
      log.error("Error fetching item details Response.", e);
    }
    return pageResponseForItemBuffer;
  }

  public List<ItemBufferResponse> getItemBuffersList(String orgId, String itemId, String uom) {
    List<ItemBufferResponse> itemBufferResponse = null;
    try {
      ResponseEntity<BaseResponse<List<ItemBufferResponse>>> itemBuffersResponseEntity =
          itemBufferUIFeign.getItemBuffersByOrgIdAndItemIdAndUom(orgId, itemId, uom);

      if (itemBuffersResponseEntity != null) {
        var responseBody = itemBuffersResponseEntity.getBody();
        if (responseBody != null) {
          itemBufferResponse = responseBody.getPayload();
        }
      }
    } catch (Exception e) {
      log.error("Error fetching item buffer details response.", e);
    }
    return itemBufferResponse;
  }
}
