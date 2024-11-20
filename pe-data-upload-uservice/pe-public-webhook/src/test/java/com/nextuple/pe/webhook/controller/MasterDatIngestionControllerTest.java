/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.webhook.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.node.consumer.dto.NodeFeedDto;
import com.nextuple.pe.webhook.domain.dtos.MasterDataIngestionDto;
import com.nextuple.pe.webhook.domain.inbound.FeedRequest;
import com.nextuple.pe.webhook.service.MasterDataIngestionService;
import com.nextuple.pe.webhook.util.TestUtil;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

class MasterDatIngestionControllerTest {
  @Mock MasterDataIngestionService masterDataIngestionService;
  @InjectMocks MasterDataIngestionController masterDataIngestionController;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void publishMasterDataTest() throws CommonServiceException {
    Mockito.doNothing()
        .when(masterDataIngestionService)
        .processMasterDataIngestionData(any(), any(), any());
    FeedRequest<MasterDataIngestionDto<?>> feedRequest = new FeedRequest<>();
    feedRequest.setData(Collections.singletonList(new MasterDataIngestionDto<NodeFeedDto>()));
    ResponseEntity<BaseResponse<String>> responseEntity =
        masterDataIngestionController.publishMasterData(TestUtil.ORG_ID, "nodes", feedRequest);

    assertEquals("Master data feed published successfully", responseEntity.getBody().getMessage());

    Mockito.verify(masterDataIngestionService).processMasterDataIngestionData(any(), any(), any());
  }
}
