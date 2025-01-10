/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.common.config.TenantDatabaseConfig;
import com.nextuple.dataupload.domain.dto.NodeServiceOptionDto;
import com.nextuple.dataupload.util.TestUtil;
import com.nextuple.node.carrier.domain.feign.impl.NodeCarrierV2Feign;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import com.nextuple.node.domain.dto.NodeDto;
import com.nextuple.node.domain.feign.NodeFeign;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class NodeServiceOptionResponseServiceTest {

  @InjectMocks private NodeServiceOptionService nodeServiceOptionService;

  @InjectMocks private TestUtil testUtil;

  @Mock private NodeFeign nodeFeign;

  @Mock private NodeCarrierV2Feign nodeCarrierFeign;
  @Mock private TenantDatabaseConfig tenantDatabaseConfig;

  @BeforeEach
  void setup() {
    ReflectionTestUtils.setField(nodeServiceOptionService, "nodeCarrierFeign", nodeCarrierFeign);
  }

  @Test
  void getNodeServiceOptionTest() throws CommonServiceException {
    when(nodeFeign.getNodeList(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeListPaginationBaseResponse());
    when(nodeCarrierFeign.getNodeCarrierListWithLastPickUpTimeDetails(any(), any()))
        .thenReturn(testUtil.getBaseResponseOfNodeCarrierResponseList());
    BaseResponse<List<NodeCarrierResponse>> nodeCarrierResponse =
        testUtil.getBaseResponseOfNodeCarrierListResponse();
    nodeCarrierResponse.getPayload().get(1).setServiceOption("SDND");
    when(nodeCarrierFeign.getNodeCarrierList(any(), any())).thenReturn(nodeCarrierResponse);
    when(tenantDatabaseConfig.getCurrentTenantServiceOptionsUnmodified())
        .thenReturn(TestUtil.extendedTenantServiceOptionExpected.toArray(new String[0]));

    PagePayload<NodeServiceOptionDto> response =
        nodeServiceOptionService.getNodeServiceOption(TestUtil.ORG_ID, 1, 1, "nodeId", "ASC");

    assertEquals(2, response.getPagination().getTotalPages());
    assertEquals(1, response.getPagination().getCurrentPage());
    assertEquals(2, response.getPagination().getTotalRecords());
    assertNotNull(response.getPagination().getNext());
    assertNull(response.getPagination().getPrevious());
    assertTrue(response.getData().get((0)).getIsActive());
    assertEquals(2, response.getData().get((0)).getServiceOptions().size());
    assertNotNull(response.getData().get(0).getProcessingTime());
    assertEquals(10.0, response.getData().get(0).getProcessingTime().get("SDND"));

    verify(nodeFeign, times(1)).getNodeList(any(), any(), any(), any(), any());
    verify(nodeCarrierFeign, times(2)).getNodeCarrierList(any(), any());
  }

  @Test
  void getNodeServiceOptionTest2() throws CommonServiceException {
    when(nodeFeign.getNodeList(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeListPaginationBaseResponse());

    when(nodeCarrierFeign.getNodeCarrierListWithLastPickUpTimeDetails(any(), any()))
        .thenReturn(testUtil.getBaseResponseOfNodeCarrierResponseList());

    BaseResponse<List<NodeCarrierResponse>> nodeCarrierListResponse =
        testUtil.getBaseResponseOfNodeCarrierListResponse();
    nodeCarrierListResponse.setPayload(Collections.emptyList());

    when(nodeFeign.getNodeList(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeListPaginationBaseResponse());

    when(nodeCarrierFeign.getNodeCarrierList(any(), any())).thenReturn(nodeCarrierListResponse);
    when(tenantDatabaseConfig.getCurrentTenantServiceOptionsUnmodified())
        .thenReturn(TestUtil.extendedTenantServiceOptionExpected.toArray(new String[0]));

    PagePayload<NodeServiceOptionDto> response =
        nodeServiceOptionService.getNodeServiceOption(TestUtil.ORG_ID, 1, 1, "nodeId", "ASC");

    assertEquals(2, response.getPagination().getTotalPages());
    assertEquals(1, response.getPagination().getCurrentPage());
    assertEquals(2, response.getPagination().getTotalRecords());
    assertNotNull(response.getPagination().getNext());
    assertNull(response.getPagination().getPrevious());
    assertTrue(response.getData().get((0)).getIsActive());
    assertEquals(2, response.getData().get((0)).getServiceOptions().size());
    assertEquals(2, response.getData().get(0).getProcessingTime().size());

    verify(nodeFeign, times(1)).getNodeList(any(), any(), any(), any(), any());
    verify(nodeCarrierFeign, times(2)).getNodeCarrierList(any(), any());
  }

  @Test
  void getNodeServiceOptionTestWithEmptyNodeCarrierResponse() throws CommonServiceException {
    when(nodeFeign.getNodeList(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeListPaginationBaseResponse());

    BaseResponse<List<NodeCarrierResponse>> nodeCarrierListResponse1 =
        testUtil.getBaseResponseOfNodeCarrierResponseList();
    nodeCarrierListResponse1.setPayload(Collections.emptyList());

    BaseResponse<List<NodeCarrierResponse>> nodeCarrierListResponse2 =
        testUtil.getBaseResponseOfNodeCarrierListResponse();
    nodeCarrierListResponse2.setPayload(Collections.emptyList());

    when(nodeFeign.getNodeList(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeListPaginationBaseResponse());

    when(nodeCarrierFeign.getNodeCarrierListWithLastPickUpTimeDetails(any(), any()))
        .thenReturn(nodeCarrierListResponse1);
    when(nodeCarrierFeign.getNodeCarrierList(any(), any())).thenReturn(nodeCarrierListResponse2);
    when(tenantDatabaseConfig.getCurrentTenantServiceOptionsUnmodified())
        .thenReturn(TestUtil.extendedTenantServiceOptionExpected.toArray(new String[0]));

    PagePayload<NodeServiceOptionDto> response =
        nodeServiceOptionService.getNodeServiceOption(TestUtil.ORG_ID, 1, 1, "nodeId", "ASC");

    assertEquals(2, response.getPagination().getTotalPages());
    assertEquals(1, response.getPagination().getCurrentPage());
    assertEquals(2, response.getPagination().getTotalRecords());
    assertNotNull(response.getPagination().getNext());
    assertNull(response.getPagination().getPrevious());
    assertTrue(response.getData().get((0)).getIsActive());
    assertEquals(2, response.getData().get((0)).getServiceOptions().size());
    assertEquals(2, response.getData().get(0).getProcessingTime().size());
    assertEquals(0.0, response.getData().get(0).getProcessingTime().get("SDND"));
    assertEquals(0.0, response.getData().get(0).getProcessingTime().get("express"));
    verify(nodeFeign, times(1)).getNodeList(any(), any(), any(), any(), any());
    verify(nodeCarrierFeign, times(2)).getNodeCarrierList(any(), any());
  }

  @Test
  @DisplayName(
      "When no service options are provided from tenant config, but node has service options assigned")
  void getNodeServiceOptionsNoServiceOptionsInTenantConfig() throws CommonServiceException {
    when(nodeFeign.getNodeList(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeListPaginationBaseResponse());
    BaseResponse<List<NodeCarrierResponse>> nodeCarrierListResponse2 =
        testUtil.getBaseResponseOfNodeCarrierListResponse();
    nodeCarrierListResponse2.getPayload().get(0).setServiceOption("express");
    nodeCarrierListResponse2.getPayload().get(1).setServiceOption("upsNextDayAirJared");
    BaseResponse<PagePayload<NodeDto>> nodeListResponse =
        testUtil.getNodeListPaginationBaseResponse();
    nodeListResponse
        .getPayload()
        .getData()
        .getFirst()
        .setServiceOptionEligibilities(Map.of("express", true, "upsNextDayAirJared", true));
    when(nodeFeign.getNodeList(any(), any(), any(), any(), any())).thenReturn(nodeListResponse);
    when(nodeCarrierFeign.getNodeCarrierListWithLastPickUpTimeDetails(any(), any()))
        .thenReturn(testUtil.getBaseResponseOfNodeCarrierResponseList());
    when(nodeCarrierFeign.getNodeCarrierList(any(), any())).thenReturn(nodeCarrierListResponse2);
    when(tenantDatabaseConfig.getCurrentTenantServiceOptionsUnmodified())
        .thenReturn(List.of().toArray(new String[0]));
    PagePayload<NodeServiceOptionDto> response =
        nodeServiceOptionService.getNodeServiceOption(TestUtil.ORG_ID, 1, 1, "nodeId", "ASC");
    assertEquals(2, response.getPagination().getTotalPages());
    assertEquals(1, response.getPagination().getCurrentPage());
    assertEquals(2, response.getPagination().getTotalRecords());
    assertNotNull(response.getPagination().getNext());
    assertNull(response.getPagination().getPrevious());
    assertFalse(response.getData().get((0)).getIsActive());
    assertEquals(0, response.getData().get((0)).getServiceOptions().size());
    assertEquals(0, response.getData().get(0).getProcessingTime().size());
    verify(nodeFeign, times(1)).getNodeList(any(), any(), any(), any(), any());
    verify(nodeCarrierFeign, times(2)).getNodeCarrierList(any(), any());
  }

  @Test
  @DisplayName("When the service option provided in node are in camel case or in lower case")
  void getNodeServiceOptionInCamelCaseOrLowerCaseTestWithNodeCarrierResponse()
      throws CommonServiceException {
    when(nodeFeign.getNodeList(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeListPaginationBaseResponse());
    BaseResponse<List<NodeCarrierResponse>> nodeCarrierListResponse1 =
        testUtil.getBaseResponseOfNodeCarrierResponseList();
    nodeCarrierListResponse1.setPayload(Collections.emptyList());
    BaseResponse<List<NodeCarrierResponse>> nodeCarrierListResponse2 =
        testUtil.getBaseResponseOfNodeCarrierListResponse();
    nodeCarrierListResponse2.getPayload().get(0).setServiceOption("express");
    nodeCarrierListResponse2.getPayload().get(1).setServiceOption("upsNextDayAirJared");
    BaseResponse<PagePayload<NodeDto>> nodeListResponse =
        testUtil.getNodeListPaginationBaseResponse();
    nodeListResponse
        .getPayload()
        .getData()
        .getFirst()
        .setServiceOptionEligibilities(Map.of("express", true, "upsNextDayAirJared", true));
    when(nodeFeign.getNodeList(any(), any(), any(), any(), any())).thenReturn(nodeListResponse);
    when(nodeCarrierFeign.getNodeCarrierListWithLastPickUpTimeDetails(any(), any()))
        .thenReturn(nodeCarrierListResponse1);
    when(nodeCarrierFeign.getNodeCarrierList(any(), any())).thenReturn(nodeCarrierListResponse2);
    when(tenantDatabaseConfig.getCurrentTenantServiceOptionsUnmodified())
        .thenReturn(TestUtil.extendedTenantServiceOptionExpected.toArray(new String[0]));
    PagePayload<NodeServiceOptionDto> response =
        nodeServiceOptionService.getNodeServiceOption(TestUtil.ORG_ID, 1, 1, "nodeId", "ASC");
    assertEquals(2, response.getPagination().getTotalPages());
    assertEquals(1, response.getPagination().getCurrentPage());
    assertEquals(2, response.getPagination().getTotalRecords());
    assertNotNull(response.getPagination().getNext());
    assertNull(response.getPagination().getPrevious());
    assertTrue(response.getData().get((0)).getIsActive());
    assertEquals(2, response.getData().get((0)).getServiceOptions().size());
    assertEquals(2, response.getData().get(0).getProcessingTime().size());
    assertEquals(10.0, response.getData().get(0).getProcessingTime().get("upsNextDayAirJared"));
    assertEquals(10.0, response.getData().get(0).getProcessingTime().get("express"));
    verify(nodeFeign, times(1)).getNodeList(any(), any(), any(), any(), any());
    verify(nodeCarrierFeign, times(2)).getNodeCarrierList(any(), any());
  }

  @Test
  @DisplayName("When the service option provided in node are in random case or upper case")
  void getNodeServiceOptionInRandomCaseOrUpperCaseTestWithNodeCarrierResponse()
      throws CommonServiceException {
    when(nodeFeign.getNodeList(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeListPaginationBaseResponse());
    BaseResponse<List<NodeCarrierResponse>> nodeCarrierListResponse1 =
        testUtil.getBaseResponseOfNodeCarrierResponseList();
    nodeCarrierListResponse1.setPayload(Collections.emptyList());
    BaseResponse<List<NodeCarrierResponse>> nodeCarrierListResponse2 =
        testUtil.getBaseResponseOfNodeCarrierListResponse();
    nodeCarrierListResponse2.getPayload().get(0).setServiceOption("express");
    nodeCarrierListResponse2.getPayload().get(1).setServiceOption("upsNextDayAirJared");
    BaseResponse<PagePayload<NodeDto>> nodeListResponse =
        testUtil.getNodeListPaginationBaseResponse();
    nodeListResponse
        .getPayload()
        .getData()
        .getFirst()
        .setServiceOptionEligibilities(Map.of("EXPRESS", true, "UpsNeXtDayAiRJared", true));
    when(nodeFeign.getNodeList(any(), any(), any(), any(), any())).thenReturn(nodeListResponse);
    when(nodeCarrierFeign.getNodeCarrierListWithLastPickUpTimeDetails(any(), any()))
        .thenReturn(nodeCarrierListResponse1);
    when(nodeCarrierFeign.getNodeCarrierList(any(), any())).thenReturn(nodeCarrierListResponse2);
    when(tenantDatabaseConfig.getCurrentTenantServiceOptionsUnmodified())
        .thenReturn(TestUtil.extendedTenantServiceOptionExpected.toArray(new String[0]));
    PagePayload<NodeServiceOptionDto> response =
        nodeServiceOptionService.getNodeServiceOption(TestUtil.ORG_ID, 1, 1, "nodeId", "ASC");
    assertEquals(2, response.getPagination().getTotalPages());
    assertEquals(1, response.getPagination().getCurrentPage());
    assertEquals(2, response.getPagination().getTotalRecords());
    assertNotNull(response.getPagination().getNext());
    assertNull(response.getPagination().getPrevious());
    assertTrue(response.getData().get((0)).getIsActive());
    assertEquals(2, response.getData().get((0)).getServiceOptions().size());
    assertEquals(2, response.getData().get(0).getProcessingTime().size());
    assertEquals(10.0, response.getData().get(0).getProcessingTime().get("upsNextDayAirJared"));
    assertEquals(10.0, response.getData().get(0).getProcessingTime().get("express"));

    verify(nodeFeign, times(1)).getNodeList(any(), any(), any(), any(), any());
    verify(nodeCarrierFeign, times(2)).getNodeCarrierList(any(), any());
  }

  @Test
  @DisplayName("When there are no service option eligibilities for the nodes")
  void getNodeServiceOptionTestWithNoServiceOptionEligibilities() throws CommonServiceException {
    BaseResponse<PagePayload<NodeDto>> nodeResponse = testUtil.getNodeListPaginationBaseResponse();
    nodeResponse.getPayload().getData().stream()
        .forEach(nodeDto -> nodeDto.setServiceOptionEligibilities(Map.of()));
    when(nodeFeign.getNodeList(any(), any(), any(), any(), any())).thenReturn(nodeResponse);
    when(nodeCarrierFeign.getNodeCarrierListWithLastPickUpTimeDetails(any(), any()))
        .thenReturn(testUtil.getBaseResponseOfNodeCarrierResponseList());
    when(nodeCarrierFeign.getNodeCarrierList(any(), any()))
        .thenReturn(testUtil.getBaseResponseOfNodeCarrierListResponse());
    when(tenantDatabaseConfig.getCurrentTenantServiceOptionsUnmodified())
        .thenReturn(TestUtil.extendedTenantServiceOptionExpected.toArray(new String[0]));

    PagePayload<NodeServiceOptionDto> response =
        nodeServiceOptionService.getNodeServiceOption(TestUtil.ORG_ID, 1, 1, "nodeId", "ASC");

    assertEquals(2, response.getPagination().getTotalPages());
    assertEquals(1, response.getPagination().getCurrentPage());
    assertEquals(2, response.getPagination().getTotalRecords());
    assertNotNull(response.getPagination().getNext());
    assertNull(response.getPagination().getPrevious());
    assertFalse(response.getData().get((0)).getIsActive());
    assertEquals(0, response.getData().get((0)).getServiceOptions().size());
    assertEquals(Map.of(), response.getData().get(0).getProcessingTime());

    verify(nodeFeign, times(1)).getNodeList(any(), any(), any(), any(), any());
    verify(nodeCarrierFeign, times(2)).getNodeCarrierList(any(), any());
  }
}
