/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.base.PagePayload;
import com.nextuple.dataupload.common.outbound.NodeCarrierServiceAndServiceOptionResponse;
import com.nextuple.dataupload.util.TestUtil;
import com.nextuple.node.carrier.domain.feign.impl.NodeCarrierV2Feign;
import com.nextuple.node.domain.feign.NodeFeign;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.CollectionUtils;

@ExtendWith(MockitoExtension.class)
class NodeCarrierServiceAndServiceOptionResponseResponseServiceTest {

  @Mock private NodeFeign nodeFeign;
  @Mock private NodeCarrierV2Feign nodeCarrierFeign;

  @InjectMocks
  private NodeCarrierServiceAndServiceOptionService nodeCarrierServiceAndServiceOptionService;

  @InjectMocks private TestUtil testUtil;

  @Test
  void getListOfNodeCarrierServiceAndServiceOptionDetails() {
    ReflectionTestUtils.setField(
        nodeCarrierServiceAndServiceOptionService, "nodeCarrierFeign", nodeCarrierFeign);
    var nodeResponse = testUtil.getNodeListPaginationBaseResponse();
    when(nodeFeign.getNodeList(any(), any(), any(), any(), any())).thenReturn(nodeResponse);
    when(nodeCarrierFeign.getNodeCarrierListWithLastPickUpTimeDetails(any(), any()))
        .thenReturn(testUtil.getBaseResponseOfNodeCarrierListResponse());

    PagePayload<NodeCarrierServiceAndServiceOptionResponse> payload =
        nodeCarrierServiceAndServiceOptionService
            .getListOfNodeCarrierServiceAndServiceOptionDetails(
                TestUtil.ORG_ID, 1, 15, "nodeId", "ASC");

    Assertions.assertNotNull(payload);
    Assertions.assertFalse(CollectionUtils.isEmpty(payload.getData()));
    Assertions.assertEquals(
        nodeResponse.getPayload().getData().get(0).getNodeType(),
        payload.getData().get(0).getNodeType());
    verify(nodeCarrierFeign, times(2)).getNodeCarrierListWithLastPickUpTimeDetails(any(), any());
    verify(nodeFeign, times(1)).getNodeList(any(), any(), any(), any(), any());
  }
}
