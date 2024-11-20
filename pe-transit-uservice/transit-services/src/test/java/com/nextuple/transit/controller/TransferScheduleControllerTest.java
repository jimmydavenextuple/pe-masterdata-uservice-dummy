/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.pojo.PageProperties;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.transit.TestUtil;
import com.nextuple.transit.domain.inbound.FetchTransferScheduleRequest;
import com.nextuple.transit.domain.inbound.TransferScheduleCreationRequest;
import com.nextuple.transit.domain.inbound.TransferScheduleRequest;
import com.nextuple.transit.domain.outbound.TransferScheduleResponse;
import com.nextuple.transit.service.TransferScheduleService;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;

class TransferScheduleControllerTest {

  @InjectMocks private TransferScheduleController controller;
  @InjectMocks private TestUtil testUtil;

  @Mock TransferScheduleService transferScheduleService;
  @Mock private PageProperties pageProperties;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    when(pageProperties.getPageNo()).thenReturn(1);
    // Initialize the controller with the mocked pageProperties
    controller = new TransferScheduleController(transferScheduleService, pageProperties);
  }

  @Test
  @DisplayName("Create transfer schedule")
  void createTransferScheduleTest() throws CommonServiceException, PromiseEngineException {
    TransferScheduleCreationRequest transferScheduleCreationRequest =
        testUtil.getTransferScheduleCreationRequest();
    when(transferScheduleService.createTransferSchedule(any()))
        .thenReturn(testUtil.getTransferScheduleResponse());
    ResponseEntity<BaseResponse<TransferScheduleResponse>> response =
        controller.createTransferSchedule(transferScheduleCreationRequest);
    Assertions.assertEquals(200, response.getStatusCode().value());
    Assertions.assertEquals(
        TestUtil.SOURCE_NODE, response.getBody().getPayload().getSourceNodeId());
    verify(transferScheduleService, times(1)).createTransferSchedule(any());
  }

  @Test
  @DisplayName("Fetch transfer schedule")
  void fetchTransferScheduleTest() {
    when(transferScheduleService.fetchTransferSchedules(any(), any()))
        .thenReturn(List.of(testUtil.getTransferScheduleResponse()));
    ResponseEntity<BaseResponse<List<TransferScheduleResponse>>> response =
        controller.getTransferSchedules(TestUtil.ORG_ID, TestUtil.DROPOFF_NODE);
    Assertions.assertEquals(200, response.getStatusCode().value());
    Assertions.assertEquals(
        TestUtil.SOURCE_NODE, response.getBody().getPayload().get(0).getSourceNodeId());
    verify(transferScheduleService, times(1)).fetchTransferSchedules(any(), any());
  }

  @Test
  @DisplayName("Delete transfer schedule")
  void deleteTransferScheduleTest() throws PromiseEngineException, CommonServiceException {
    TransferScheduleRequest transferScheduleCreationRequest = testUtil.getTransferScheduleRequest();
    when(transferScheduleService.deleteTransferSchedule(any()))
        .thenReturn(testUtil.getTransferScheduleResponse());
    ResponseEntity<BaseResponse<TransferScheduleResponse>> response =
        controller.deleteTransferSchedule(transferScheduleCreationRequest);
    Assertions.assertEquals(200, response.getStatusCode().value());
    Assertions.assertEquals(
        TestUtil.SOURCE_NODE, response.getBody().getPayload().getSourceNodeId());
    verify(transferScheduleService, times(1)).deleteTransferSchedule(any());
  }

  @Test
  @DisplayName("Fetch paginated transfer schedule list")
  void fetchPaginatedTransferScheduleListTest()
      throws CommonServiceException, PromiseEngineException {
    Page<TransferScheduleResponse> mockPage =
        new PageImpl<>(List.of(testUtil.getTransferScheduleResponse()));
    FetchTransferScheduleRequest request = testUtil.getFetchTransferScheduleRequest();

    when(transferScheduleService.fetchTransferScheduleList(any(), anyBoolean(), any(), any()))
        .thenReturn(mockPage);

    ResponseEntity<BaseResponse<PagePayload<TransferScheduleResponse>>> response =
        controller.fetchTransferScheduleList(
            TestUtil.ORG_ID, true, 1, 10, "sourceNodeId", "ASC", request);

    Assertions.assertEquals(200, response.getStatusCode().value());
    Assertions.assertNotNull(response.getBody().getPayload());
    Assertions.assertEquals(
        TestUtil.SOURCE_NODE, response.getBody().getPayload().getData().get(0).getSourceNodeId());

    verify(transferScheduleService, times(1))
        .fetchTransferScheduleList(any(), anyBoolean(), any(), any());
  }
}
