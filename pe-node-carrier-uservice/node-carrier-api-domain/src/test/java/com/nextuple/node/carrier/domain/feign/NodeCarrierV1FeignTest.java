/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.domain.feign;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.node.carrier.domain.TestUtil;
import com.nextuple.node.carrier.domain.feign.impl.NodeCarrierV1Feign;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NodeCarrierV1FeignTest {
  @Mock NodeCarrierFeign nodeCarrierFeign;
  @InjectMocks TestUtil testUtil;
  @InjectMocks NodeCarrierV1Feign nodeCarrierV1Feign;

  @Test
  @DisplayName("Successful Execution - Create Node Carrier")
  void createNodeCarrier() {
    BaseResponse<NodeCarrierResponse> expectedResponse =
        testUtil.getBaseResponseOfNodeCarrierResponse();
    when(nodeCarrierFeign.createNodeCarrier(any())).thenReturn(expectedResponse);
    BaseResponse<NodeCarrierResponse> actualResponse =
        nodeCarrierV1Feign.createNodeCarrier(testUtil.getNodeCarrierRequest());
    assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
    assertEquals(
        expectedResponse.getPayload().getCarrierServiceId(),
        actualResponse.getPayload().getCarrierServiceId());
    verify(nodeCarrierFeign, times(1)).createNodeCarrier(any());
  }

  @Test
  @DisplayName("Successful Execution - Update Node Carrier")
  void updateNodeCarrier() {
    BaseResponse<NodeCarrierResponse> expectedResponse =
        testUtil.getBaseResponseOfNodeCarrierResponse();
    when(nodeCarrierFeign.updateNodeCarrier(any(), any(), any(), any(), any()))
        .thenReturn(expectedResponse);
    BaseResponse<NodeCarrierResponse> actualResponse =
        nodeCarrierV1Feign.updateNodeCarrier(
            TestUtil.NODE_ID,
            TestUtil.ORG_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SERVICE_OPTION,
            testUtil.getNodeCarrierUpdateRequest());
    assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
    assertEquals(
        expectedResponse.getPayload().getCarrierServiceId(),
        actualResponse.getPayload().getCarrierServiceId());
    verify(nodeCarrierFeign, times(1)).updateNodeCarrier(any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("Successful Execution - Get Node Carrier")
  void getNodeCarrier() {
    BaseResponse<NodeCarrierResponse> expectedResponse =
        testUtil.getBaseResponseOfNodeCarrierResponse();
    when(nodeCarrierFeign.getNodeCarrier(any(), any(), any(), any())).thenReturn(expectedResponse);
    BaseResponse<NodeCarrierResponse> actualResponse =
        nodeCarrierV1Feign.getNodeCarrier(
            TestUtil.NODE_ID,
            TestUtil.ORG_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SERVICE_OPTION);
    assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
    assertEquals(
        expectedResponse.getPayload().getCarrierServiceId(),
        actualResponse.getPayload().getCarrierServiceId());
    verify(nodeCarrierFeign, times(1)).getNodeCarrier(any(), any(), any(), any());
  }

  @Test
  @DisplayName("Successful Execution - Delete Node Carrier")
  void deleteNodeCarrier() {
    BaseResponse<NodeCarrierResponse> expectedResponse =
        testUtil.getBaseResponseOfNodeCarrierResponse();
    when(nodeCarrierFeign.deleteNodeCarrier(any(), any(), any(), any()))
        .thenReturn(expectedResponse);
    BaseResponse<NodeCarrierResponse> actualResponse =
        nodeCarrierV1Feign.deleteNodeCarrier(
            TestUtil.NODE_ID,
            TestUtil.ORG_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SERVICE_OPTION);
    assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
    assertEquals(
        expectedResponse.getPayload().getCarrierServiceId(),
        actualResponse.getPayload().getCarrierServiceId());
    verify(nodeCarrierFeign, times(1)).deleteNodeCarrier(any(), any(), any(), any());
  }

  @Test
  @DisplayName("Successful Execution - Get Node Carrier List")
  void getNodeCarrierList() {
    BaseResponse<List<NodeCarrierResponse>> expectedResponse =
        testUtil.getBaseResponseOfNodeCarrierResponseList();
    when(nodeCarrierFeign.getNodeCarrierList(any(), any())).thenReturn(expectedResponse);
    BaseResponse<List<NodeCarrierResponse>> actualResponse =
        nodeCarrierV1Feign.getNodeCarrierList(TestUtil.NODE_ID, TestUtil.ORG_ID);
    assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
    assertEquals(expectedResponse.getPayload().size(), actualResponse.getPayload().size());
    verify(nodeCarrierFeign, times(1)).getNodeCarrierList(any(), any());
  }

  @Test
  @DisplayName("Successful Execution - Update Buffer")
  void updateBuffer() {
    BaseResponse<NodeCarrierResponse> expectedResponse =
        testUtil.getBaseResponseOfNodeCarrierResponse();
    when(nodeCarrierFeign.updateBuffer(any())).thenReturn(expectedResponse);
    BaseResponse<NodeCarrierResponse> actualResponse =
        nodeCarrierV1Feign.createBuffer(testUtil.getNodeCarrierBufferRequest());
    assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
    assertEquals(
        expectedResponse.getPayload().getCarrierServiceId(),
        actualResponse.getPayload().getCarrierServiceId());
    verify(nodeCarrierFeign, times(1)).updateBuffer(any());
  }

  @Test
  @DisplayName("Successful Execution - Delete Buffer")
  void deleteBuffer() throws CommonServiceException {
    Exception e =
        assertThrows(
            CommonServiceException.class,
            () ->
                nodeCarrierV1Feign.deleteBuffer(
                    testUtil.getNodeServiceOptionBufferDeleteRequest()));
    assertEquals("Delete buffer is not supported in node carrier version 1", e.getMessage());
  }

  @Test
  @DisplayName("Successful Execution - Update Processing lead time")
  void updateProcessingLeadTime() {
    BaseResponse<NodeCarrierResponse> expectedResponse =
        testUtil.getBaseResponseOfNodeCarrierResponse();
    when(nodeCarrierFeign.updateProcessingLeadTime(any())).thenReturn(expectedResponse);
    BaseResponse<NodeCarrierResponse> actualResponse =
        nodeCarrierV1Feign.createProcessingLeadTime(testUtil.getNodeCarrierRequest());
    assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
    assertEquals(
        expectedResponse.getPayload().getCarrierServiceId(),
        actualResponse.getPayload().getCarrierServiceId());
    verify(nodeCarrierFeign, times(1)).updateProcessingLeadTime(any());
  }

  @Test
  @DisplayName("Successful Execution - Delete Node Carrier By OrgId NodeId And ServiceOption")
  void deleteNodeCarrierByOrgIdNodeIdAndServiceOption() {
    BaseResponse<NodeCarrierResponse> expectedResponse =
        testUtil.getBaseResponseOfNodeCarrierResponse();
    when(nodeCarrierFeign.deleteNodeCarrierByOrgIdNodeIdAndServiceOption(
            any(), any(), any(), any()))
        .thenReturn(expectedResponse);
    BaseResponse<NodeCarrierResponse> actualResponse =
        nodeCarrierV1Feign.deleteNodeCarrierByOrgIdNodeIdAndServiceOption(
            TestUtil.NODE_ID,
            TestUtil.ORG_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SERVICE_OPTION);
    assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
    assertEquals(
        expectedResponse.getPayload().getCarrierServiceId(),
        actualResponse.getPayload().getCarrierServiceId());
    verify(nodeCarrierFeign, times(1))
        .deleteNodeCarrierByOrgIdNodeIdAndServiceOption(any(), any(), any(), any());
  }

  @Test
  @DisplayName("Successful Execution - Get Unique Node Carrier Service List")
  void getUniqueNodeCarrierServiceList() {
    BaseResponse<NodeCarrierResponse> expectedResponse =
        testUtil.getBaseResponseOfNodeCarrierResponse();
    when(nodeCarrierFeign.getUniqueNodeCarrierServiceList(any(), any()))
        .thenReturn(
            BaseResponse.builder()
                .payload(List.of(expectedResponse.getPayload().getCarrierServiceId()))
                .build());
    BaseResponse<List<String>> actualResponse =
        nodeCarrierV1Feign.getUniqueNodeCarrierServiceList(TestUtil.NODE_ID, TestUtil.ORG_ID);
    verify(nodeCarrierFeign, times(1)).getUniqueNodeCarrierServiceList(any(), any());
  }

  @Test
  @DisplayName("Successful Execution - Get NodeCarrier List With Last PickUp Time Details")
  void getNodeCarrierListWithLastPickUpTimeDetails() {
    BaseResponse<List<NodeCarrierResponse>> expectedResponse =
        testUtil.getBaseResponseOfNodeCarrierResponseList();
    when(nodeCarrierFeign.getNodeCarrierListWithLastPickUpTimeDetails(any(), any()))
        .thenReturn(expectedResponse);
    BaseResponse<List<NodeCarrierResponse>> actualResponse =
        nodeCarrierV1Feign.getNodeCarrierListWithLastPickUpTimeDetails(
            TestUtil.NODE_ID, TestUtil.ORG_ID);
    assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
    assertEquals(
        expectedResponse.getPayload().getFirst().getCarrierServiceId(),
        actualResponse.getPayload().getFirst().getCarrierServiceId());
    verify(nodeCarrierFeign, times(1)).getNodeCarrierListWithLastPickUpTimeDetails(any(), any());
  }

  @Test
  @DisplayName("Successful Execution - Get Buffers By OrgId And NodeId And ServiceOption")
  void getBuffersByOrgIdAndNodeIdAndServiceOption() {
    BaseResponse<List<NodeCarrierResponse>> expectedResponse =
        testUtil.getBaseResponseOfNodeCarrierResponseList();
    when(nodeCarrierFeign.getNodeCarrierList(any(), any())).thenReturn(expectedResponse);
    BaseResponse<List<NodeCarrierResponse>> actualResponse =
        nodeCarrierV1Feign.getBuffersByOrgIdAndNodeIdAndServiceOption(
            TestUtil.NODE_ID, TestUtil.ORG_ID, TestUtil.SERVICE_OPTION);
    assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
    assertEquals(
        expectedResponse.getPayload().getFirst().getCarrierServiceId(),
        actualResponse.getPayload().getFirst().getCarrierServiceId());
    verify(nodeCarrierFeign, times(1)).getNodeCarrierList(any(), any());
  }

  @Test
  @DisplayName("Successful Execution - Get NodeCarrier List With Carrier Service Id")
  void getAllNodeCarriersByOrgIdCarrierServiceId() {
    BaseResponse<List<NodeCarrierResponse>> expectedResponse =
        testUtil.getBaseResponseOfNodeCarrierResponseList();
    when(nodeCarrierFeign.getAllNodeCarriersByOrgIdCarrierServiceId(any(), any()))
        .thenReturn(expectedResponse);
    BaseResponse<List<NodeCarrierResponse>> actualResponse =
        nodeCarrierV1Feign.getAllNodeCarriersByOrgIdCarrierServiceId(
            TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID);
    assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
    assertEquals(
        expectedResponse.getPayload().getFirst().getCarrierServiceId(),
        actualResponse.getPayload().getFirst().getCarrierServiceId());
    verify(nodeCarrierFeign, times(1)).getAllNodeCarriersByOrgIdCarrierServiceId(any(), any());
  }

  @Test
  @DisplayName("Failure Execution - Get NodeCarrier List With Carrier Service Id")
  void getAllNodeCarriersByOrgIdCarrierServiceIdException() {
    when(nodeCarrierFeign.getAllNodeCarriersByOrgIdCarrierServiceId(any(), any()))
        .thenThrow(
            new RuntimeException(
                "Error while fetching node carrier details for orgId and carrierServiceId"));
    RuntimeException actualResponse =
        Assertions.assertThrows(
            RuntimeException.class,
            () -> {
              nodeCarrierV1Feign.getAllNodeCarriersByOrgIdCarrierServiceId(
                  TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID);
            });
    assertEquals(
        "Error while fetching node carrier details for orgId and carrierServiceId",
        actualResponse.getMessage());
    verify(nodeCarrierFeign, times(1)).getAllNodeCarriersByOrgIdCarrierServiceId(any(), any());
  }

  @Test
  @DisplayName("Successful Execution - Get NodeCarrier List by OrgId NodeId and Carrier Service ID")
  void getAllNodeCarriersByOrgIdNodeIdAndCarrierServiceId() {
    BaseResponse<List<NodeCarrierResponse>> expectedResponse =
        testUtil.getBaseResponseOfNodeCarrierResponseList();
    when(nodeCarrierFeign.getAllNodeCarriersByOrgIdNodeIdAndCarrierServiceId(any(), any(), any()))
        .thenReturn(expectedResponse);
    BaseResponse<List<NodeCarrierResponse>> actualResponse =
        nodeCarrierV1Feign.getAllNodeCarriersByOrgIdNodeIdAndCarrierServiceId(
            TestUtil.ORG_ID, TestUtil.NODE_ID, TestUtil.CARRIER_SERVICE_ID);
    assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
    assertEquals(
        expectedResponse.getPayload().getFirst().getCarrierServiceId(),
        actualResponse.getPayload().getFirst().getCarrierServiceId());
    assertEquals(
        expectedResponse.getPayload().getFirst().getNodeId(),
        actualResponse.getPayload().getFirst().getNodeId());
    verify(nodeCarrierFeign, times(1))
        .getAllNodeCarriersByOrgIdNodeIdAndCarrierServiceId(any(), any(), any());
  }

  @Test
  @DisplayName("Failure Execution - Get NodeCarrier List by OrgId NodeId and Carrier Service ID")
  void getAllNodeCarriersByOrgIdNodeIdAndCarrierServiceIdException() {
    when(nodeCarrierFeign.getAllNodeCarriersByOrgIdNodeIdAndCarrierServiceId(any(), any(), any()))
        .thenThrow(
            new RuntimeException(
                "Error while fetching node carrier details for orgId, nodeId and carrierServiceId"));
    RuntimeException actualResponse =
        Assertions.assertThrows(
            RuntimeException.class,
            () -> {
              nodeCarrierV1Feign.getAllNodeCarriersByOrgIdNodeIdAndCarrierServiceId(
                  TestUtil.ORG_ID, TestUtil.NODE_ID, TestUtil.CARRIER_SERVICE_ID);
            });
    assertEquals(
        "Error while fetching node carrier details for orgId, nodeId and carrierServiceId",
        actualResponse.getMessage());
    verify(nodeCarrierFeign, times(1))
        .getAllNodeCarriersByOrgIdNodeIdAndCarrierServiceId(any(), any(), any());
  }
}
