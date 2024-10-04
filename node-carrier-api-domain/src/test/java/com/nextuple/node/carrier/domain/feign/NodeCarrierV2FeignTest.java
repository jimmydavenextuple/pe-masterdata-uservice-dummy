/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.domain.feign;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.node.carrier.domain.TestUtil;
import com.nextuple.node.carrier.domain.feign.impl.NodeCarrierV2Feign;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import com.nextuple.node.carrier.domain.outbound.NodeCarriersResponse;
import com.nextuple.node.carrier.domain.outbound.NodeServiceOptionBufferResponse;
import com.nextuple.node.carrier.domain.outbound.NodeServiceOptionResponse;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NodeCarrierV2FeignTest {
  @Mock NodeCarriersFeign nodeCarriersFeign;
  @Mock NodeServiceOptionsFeign nodeServiceOptionsFeign;
  @Mock NodeServiceOptionBufferFeign nodeServiceOptionBufferFeign;
  @InjectMocks TestUtil testUtil;
  @InjectMocks NodeCarrierV2Feign nodeCarrierV2Feign;

  @Test
  @DisplayName("Successful Execution - Create Node Carrier")
  void createNodeCarrier() {
    BaseResponse<NodeCarriersResponse> expectedResponse = testUtil.getNodeCarriersResponse();
    when(nodeCarriersFeign.createNodeCarrier(any())).thenReturn(expectedResponse);
    BaseResponse<NodeCarrierResponse> actualResponse =
        nodeCarrierV2Feign.createNodeCarrier(testUtil.getNodeCarrierRequest());
    assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
    assertEquals(
        expectedResponse.getPayload().getCarrierServiceId(),
        actualResponse.getPayload().getCarrierServiceId());
    verify(nodeCarriersFeign, times(1)).createNodeCarrier(any());
  }

  @Test
  @DisplayName("Successful Execution - Update Node Carrier")
  void updateNodeCarrier() {
    BaseResponse<NodeCarriersResponse> expectedResponse = testUtil.getNodeCarriersResponse();
    when(nodeCarriersFeign.updateNodeCarrier(any(), any(), any(), any(), any()))
        .thenReturn(expectedResponse);
    BaseResponse<NodeCarrierResponse> actualResponse =
        nodeCarrierV2Feign.updateNodeCarrier(
            TestUtil.NODE_ID,
            TestUtil.ORG_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SERVICE_OPTION,
            testUtil.getNodeCarrierUpdateRequest());
    assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
    assertEquals(
        expectedResponse.getPayload().getCarrierServiceId(),
        actualResponse.getPayload().getCarrierServiceId());
    verify(nodeCarriersFeign, times(1)).updateNodeCarrier(any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("Successful Execution - Get Node Carrier")
  void getNodeCarrier() {
    BaseResponse<NodeCarriersResponse> expectedResponse = testUtil.getNodeCarriersResponse();
    when(nodeCarriersFeign.getNodeCarrier(any(), any(), any(), any())).thenReturn(expectedResponse);
    BaseResponse<NodeCarrierResponse> actualResponse =
        nodeCarrierV2Feign.getNodeCarrier(
            TestUtil.NODE_ID,
            TestUtil.ORG_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SERVICE_OPTION);
    assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
    assertEquals(
        expectedResponse.getPayload().getCarrierServiceId(),
        actualResponse.getPayload().getCarrierServiceId());
    verify(nodeCarriersFeign, times(1)).getNodeCarrier(any(), any(), any(), any());
  }

  @Test
  @DisplayName("Successful Execution - Delete Node Carrier")
  void deleteNodeCarrier() {
    BaseResponse<NodeCarriersResponse> expectedResponse = testUtil.getNodeCarriersResponse();
    when(nodeCarriersFeign.deleteNodeCarrier(any(), any(), any(), any()))
        .thenReturn(expectedResponse);
    BaseResponse<NodeCarrierResponse> actualResponse =
        nodeCarrierV2Feign.deleteNodeCarrier(
            TestUtil.NODE_ID,
            TestUtil.ORG_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SERVICE_OPTION);
    assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
    assertEquals(
        expectedResponse.getPayload().getCarrierServiceId(),
        actualResponse.getPayload().getCarrierServiceId());
    verify(nodeCarriersFeign, times(1)).deleteNodeCarrier(any(), any(), any(), any());
  }

  @Test
  @DisplayName("Successful Execution - Get Node Carrier List")
  void getNodeCarrierList() {
    BaseResponse<List<NodeServiceOptionResponse>> expectedResponse =
        testUtil.getListOfNodeServiceOptionResponse();
    when(nodeServiceOptionsFeign.getNodeServiceOptionsList(any(), any()))
        .thenReturn(expectedResponse);
    BaseResponse<List<NodeCarrierResponse>> actualResponse =
        nodeCarrierV2Feign.getNodeCarrierList(TestUtil.NODE_ID, TestUtil.ORG_ID);
    assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
    assertEquals(expectedResponse.getPayload().size(), actualResponse.getPayload().size());
    verify(nodeServiceOptionsFeign, times(1)).getNodeServiceOptionsList(any(), any());
  }

  @Test
  @DisplayName("Successful Execution - Update buffer")
  void updateBuffer() {
    BaseResponse<NodeServiceOptionBufferResponse> expectedResponse =
        testUtil.getNodeServiceOptionBufferResponse();
    when(nodeServiceOptionBufferFeign.createNodeServiceOptionBuffer(any()))
        .thenReturn(expectedResponse);
    BaseResponse<NodeCarrierResponse> actualResponse =
        nodeCarrierV2Feign.createBuffer(testUtil.getNodeCarrierBufferRequest());
    assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
    assertEquals(
        expectedResponse.getPayload().getServiceOption(),
        actualResponse.getPayload().getServiceOption());
    verify(nodeServiceOptionBufferFeign, times(1)).createNodeServiceOptionBuffer(any());
  }

  @Test
  @DisplayName("Successful Execution - Delete buffer")
  void deleteBuffer() {
    BaseResponse<NodeServiceOptionBufferResponse> expectedResponse =
        testUtil.getNodeServiceOptionBufferResponse();
    when(nodeServiceOptionBufferFeign.deleteNodeServiceOptionBuffer(any()))
        .thenReturn(expectedResponse);
    BaseResponse<NodeServiceOptionBufferResponse> actualResponse =
        nodeCarrierV2Feign.deleteBuffer(testUtil.getNodeServiceOptionBufferDeleteRequest());
    assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
    assertEquals(
        expectedResponse.getPayload().getServiceOption(),
        actualResponse.getPayload().getServiceOption());
    verify(nodeServiceOptionBufferFeign, times(1)).deleteNodeServiceOptionBuffer(any());
  }

  @Test
  @DisplayName("Successful Execution - Update processing lead time")
  void updateProcessingLeadTime() {
    BaseResponse<NodeServiceOptionResponse> expectedResponse =
        testUtil.getNodeServiceOptionResponse();
    when(nodeServiceOptionsFeign.createNodeServiceOption(any())).thenReturn(expectedResponse);
    BaseResponse<NodeCarrierResponse> actualResponse =
        nodeCarrierV2Feign.createProcessingLeadTime(testUtil.getNodeCarrierRequest());
    assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
    assertEquals(
        expectedResponse.getPayload().getServiceOption(),
        actualResponse.getPayload().getServiceOption());
    verify(nodeServiceOptionsFeign, times(1)).createNodeServiceOption(any());
  }

  @Test
  @DisplayName("Successful Execution - Delete Node Carrier By OrgId NodeId And ServiceOption")
  void deleteNodeCarrierByOrgIdNodeIdAndServiceOption() {
    BaseResponse<NodeServiceOptionResponse> expectedResponse =
        testUtil.getNodeServiceOptionResponse();
    when(nodeServiceOptionsFeign.deleteNodeServiceOption(any(), any(), any()))
        .thenReturn(expectedResponse);
    BaseResponse<NodeCarrierResponse> actualResponse =
        nodeCarrierV2Feign.deleteNodeCarrierByOrgIdNodeIdAndServiceOption(
            TestUtil.NODE_ID,
            TestUtil.ORG_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SERVICE_OPTION);
    assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
    assertEquals(
        expectedResponse.getPayload().getServiceOption(),
        actualResponse.getPayload().getServiceOption());
    verify(nodeServiceOptionsFeign, times(1)).deleteNodeServiceOption(any(), any(), any());
  }

  @Test
  @DisplayName("Successful Execution - Get Unique Node Carrier Service List")
  void getUniqueNodeCarrierServiceList() {
    BaseResponse<NodeCarrierResponse> expectedResponse =
        testUtil.getBaseResponseOfNodeCarrierResponse();
    when(nodeCarriersFeign.getUniqueNodeCarrierServiceList(any(), any()))
        .thenReturn(
            BaseResponse.builder()
                .payload(List.of(expectedResponse.getPayload().getCarrierServiceId()))
                .build());
    BaseResponse<List<String>> actualResponse =
        nodeCarrierV2Feign.getUniqueNodeCarrierServiceList(TestUtil.NODE_ID, TestUtil.ORG_ID);
    verify(nodeCarriersFeign, times(1)).getUniqueNodeCarrierServiceList(any(), any());
  }

  @Test
  @DisplayName("Successful Execution - Get Node Carrier List With Last PickUp Time Details")
  void getNodeCarrierListWithLastPickUpTimeDetails() {
    BaseResponse<List<NodeCarriersResponse>> expectedResponse =
        testUtil.getListOfNodeCarriersResponse();
    when(nodeCarriersFeign.getNodeCarriersList(any(), any())).thenReturn(expectedResponse);
    BaseResponse<List<NodeCarrierResponse>> actualResponse =
        nodeCarrierV2Feign.getNodeCarrierListWithLastPickUpTimeDetails(
            TestUtil.NODE_ID, TestUtil.ORG_ID);
    assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
    assertEquals(
        expectedResponse.getPayload().getFirst().getCarrierServiceId(),
        actualResponse.getPayload().getFirst().getCarrierServiceId());
    verify(nodeCarriersFeign, times(1)).getNodeCarriersList(any(), any());
  }

  @Test
  @DisplayName("Successful Execution - Get Buffers By OrgId And NodeId And ServiceOption")
  void getBuffersByOrgIdAndNodeIdAndServiceOption() {
    BaseResponse<List<NodeServiceOptionBufferResponse>> expectedResponse =
        testUtil.getListOfNodeServiceOptionBufferResponse();
    when(nodeServiceOptionBufferFeign.getBuffersByOrgIdAndNodeIdAndServiceOption(
            any(), any(), any()))
        .thenReturn(expectedResponse);
    BaseResponse<List<NodeCarrierResponse>> actualResponse =
        nodeCarrierV2Feign.getBuffersByOrgIdAndNodeIdAndServiceOption(
            TestUtil.NODE_ID, TestUtil.ORG_ID, TestUtil.SERVICE_OPTION);
    assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
    assertEquals(
        expectedResponse.getPayload().getFirst().getServiceOption(),
        actualResponse.getPayload().getFirst().getServiceOption());
    verify(nodeServiceOptionBufferFeign, times(1))
        .getBuffersByOrgIdAndNodeIdAndServiceOption(any(), any(), any());
  }

  @Test
  @DisplayName("Successful Execution - Get NodeCarrier List With Carrier Service Id")
  void getAllNodeCarriersByOrgIdCarrierServiceId() {
    BaseResponse<List<NodeCarriersResponse>> expectedResponse =
        testUtil.getListOfNodeCarriersResponse();
    when(nodeCarriersFeign.getAllNodeCarriersByOrgIdCarrierServiceId(any(), any()))
        .thenReturn(expectedResponse);
    BaseResponse<List<NodeCarrierResponse>> actualResponse =
        nodeCarrierV2Feign.getAllNodeCarriersByOrgIdCarrierServiceId(
            TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID);
    assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
    assertEquals(
        expectedResponse.getPayload().getFirst().getCarrierServiceId(),
        actualResponse.getPayload().getFirst().getCarrierServiceId());
    verify(nodeCarriersFeign, times(1)).getAllNodeCarriersByOrgIdCarrierServiceId(any(), any());
  }

  @Test
  @DisplayName("Failure Execution - Get NodeCarrier List With Carrier Service Id")
  void getAllNodeCarriersByOrgIdCarrierServiceIdException() {
    when(nodeCarriersFeign.getAllNodeCarriersByOrgIdCarrierServiceId(any(), any()))
        .thenThrow(
            new RuntimeException(
                "Error while fetching node carrier details for orgId and carrierServiceId"));
    RuntimeException actualResponse =
        Assertions.assertThrows(
            RuntimeException.class,
            () -> {
              nodeCarrierV2Feign.getAllNodeCarriersByOrgIdCarrierServiceId(
                  TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID);
            });
    assertEquals(
        "Error while fetching node carrier details for orgId and carrierServiceId",
        actualResponse.getMessage());
    verify(nodeCarriersFeign, times(1)).getAllNodeCarriersByOrgIdCarrierServiceId(any(), any());
  }
}
