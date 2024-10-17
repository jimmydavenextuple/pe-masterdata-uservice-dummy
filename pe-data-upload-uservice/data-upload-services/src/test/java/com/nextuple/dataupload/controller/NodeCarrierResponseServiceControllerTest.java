/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.controller;

import static org.mockito.Mockito.*;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.domain.dto.NodeCarrierServiceResponse;
import com.nextuple.dataupload.domain.pojo.NodeCarrierServicePageProperties;
import com.nextuple.dataupload.service.NodeCarrierServiceDetailsService;
import com.nextuple.dataupload.util.TestUtil;
import com.nextuple.jobs.framework.common.domain.pojo.DefaultPageProperties;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

@ExtendWith(MockitoExtension.class)
class NodeCarrierResponseServiceControllerTest {

  @Mock private NodeCarrierServiceDetailsService nodeCarrierServiceDetailsService;
  @Mock private NodeCarrierServicePageProperties pageProperties;
  @Mock private DefaultPageProperties defaultPageProperties;
  @InjectMocks private TestUtil testUtil;
  @InjectMocks private NodeCarrierServiceController nodeCarrierServiceController;

  @Test
  void getNodeServiceOptionDetails() {
    when(pageProperties.getSortBy()).thenReturn("nodeId");
    when(pageProperties.getSortOrder()).thenReturn("ASC");
    when(defaultPageProperties.getPageSize()).thenReturn(15);
    when(defaultPageProperties.getPageNo()).thenReturn(1);
    when(nodeCarrierServiceDetailsService.getNodeCarrierServiceDetails(
            any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeCarrierServicePagePayload(1));

    ResponseEntity<BaseResponse<PagePayload<NodeCarrierServiceResponse>>> response =
        nodeCarrierServiceController.getNodeCarrierServiceDetails(
            TestUtil.ORG_ID,
            testUtil.getPageParams(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getPayload());
    Assertions.assertFalse(CollectionUtils.isEmpty(response.getBody().getPayload().getData()));
    verify(nodeCarrierServiceDetailsService, times(1))
        .getNodeCarrierServiceDetails(any(), any(), any(), any(), any());
  }
}
