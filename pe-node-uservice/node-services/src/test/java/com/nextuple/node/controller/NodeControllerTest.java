/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.pojo.PageProperties;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.node.TestUtil;
import com.nextuple.node.domain.dto.NodeCacheKeyDto;
import com.nextuple.node.domain.dto.NodeDto;
import com.nextuple.node.domain.inbound.NodeBaseRequest;
import com.nextuple.node.domain.inbound.NodeRequest;
import com.nextuple.node.domain.outbound.NodeResponse;
import com.nextuple.node.domain.outbound.NodeTypesResponse;
import com.nextuple.node.persistence.exception.NodeDomainException;
import com.nextuple.node.service.NodeService;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class NodeControllerTest {

  @InjectMocks private NodeController nodeController;

  @InjectMocks private TestUtil testUtil;

  @Mock private NodeService nodeService;
  @Mock private PageProperties pageProperties;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createNodeTest() throws CommonServiceException, NodeDomainException, PromiseEngineException {
    NodeRequest nodeRequest = testUtil.getNodeRequest();
    when(nodeService.createNode(any(NodeRequest.class))).thenReturn(testUtil.getNodeResponse());

    ResponseEntity<BaseResponse<NodeResponse>> responseEntity =
        nodeController.createNode(nodeRequest);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(testUtil.getNodeResponse(), responseEntity.getBody().getPayload());

    verify(nodeService, times(1)).createNode(any());
  }

  @Test
  void createNodeExceptionTest()
      throws NodeDomainException, CommonServiceException, NodeDomainException {
    NodeRequest nodeRequest = testUtil.getNodeRequest();
    when(nodeService.createNode(any(NodeRequest.class)))
        .thenThrow(new RuntimeException("Failed to create node"));

    Exception exception =
        Assertions.assertThrows(Exception.class, () -> nodeController.createNode(nodeRequest));
    Assertions.assertEquals("Failed to create node", exception.getMessage());

    verify(nodeService, times(1)).createNode(any());
  }

  @Test
  void getNodeDetailsByNodeIdAndOrgIdTest() throws NodeDomainException, CommonServiceException {
    NodeResponse nodeResponse = testUtil.getNodeResponse();
    when(nodeService.getNodeDetails(any(), any())).thenReturn(nodeResponse);

    ResponseEntity<BaseResponse<NodeResponse>> responseEntity =
        nodeController.getNodeDetails(TestUtil.NODE_ID, TestUtil.ORG_ID);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(nodeResponse, responseEntity.getBody().getPayload());

    verify(nodeService, times(1)).getNodeDetails(any(), any());
  }

  @Test
  void getNodeDetailsByNodeIdAndOrgIdExceptionTest()
      throws NodeDomainException, CommonServiceException {
    when(nodeService.getNodeDetails(any(), any()))
        .thenThrow(new RuntimeException("Unable to fetch node details"));

    Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () -> nodeController.getNodeDetails(TestUtil.NODE_ID, TestUtil.ORG_ID));
    Assertions.assertEquals("Unable to fetch node details", exception.getMessage());
    verify(nodeService, times(1)).getNodeDetails(any(), any());
  }

  @Test
  void updateNodeTest() throws NodeDomainException, CommonServiceException {
    NodeBaseRequest nodeBaseRequest = testUtil.getNodeUpdationRequest();
    when(nodeService.updateNodeDetails(anyString(), anyString(), any(NodeBaseRequest.class)))
        .thenReturn(testUtil.getUpdatedNodeResponse());

    ResponseEntity<BaseResponse<NodeResponse>> responseEntity =
        nodeController.updateNodeDetails(TestUtil.NODE_ID, TestUtil.ORG_ID, nodeBaseRequest);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(
        testUtil.getUpdatedNodeResponse(), responseEntity.getBody().getPayload());

    verify(nodeService, times(1)).updateNodeDetails(anyString(), anyString(), any());
  }

  @Test
  void updateNodeExceptionTest() throws NodeDomainException, CommonServiceException {
    NodeBaseRequest nodeBaseRequest = testUtil.getNodeUpdationRequest();
    when(nodeService.updateNodeDetails(anyString(), anyString(), any(NodeBaseRequest.class)))
        .thenThrow(new RuntimeException("Unable to update the node details"));

    Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () ->
                nodeController.updateNodeDetails(
                    TestUtil.NODE_ID, TestUtil.ORG_ID, nodeBaseRequest));
    Assertions.assertEquals("Unable to update the node details", exception.getMessage());

    verify(nodeService, times(1)).updateNodeDetails(anyString(), anyString(), any());
  }

  @Test
  void deleteNodeTest() throws NodeDomainException, CommonServiceException {
    when(nodeService.deleteNode(any(), any())).thenReturn(testUtil.getNodeResponse());

    ResponseEntity<BaseResponse<NodeResponse>> responseEntity =
        nodeController.deleteNode(TestUtil.NODE_ID, TestUtil.ORG_ID);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(testUtil.getNodeResponse(), responseEntity.getBody().getPayload());

    verify(nodeService, times(1)).deleteNode(any(), any());
  }

  @Test
  void deleteNodeExceptionTest() throws NodeDomainException, CommonServiceException {
    when(nodeService.deleteNode(any(), any()))
        .thenThrow(new RuntimeException("Error while deleting node"));

    Exception exception =
        Assertions.assertThrows(
            Exception.class, () -> nodeController.deleteNode(TestUtil.NODE_ID, TestUtil.ORG_ID));
    Assertions.assertEquals("Error while deleting node", exception.getMessage());

    verify(nodeService, times(1)).deleteNode(any(), any());
  }

  @Test
  void getNodeListTest() throws NodeDomainException, CommonServiceException {
    List<NodeDto> nodeDtoList = testUtil.getNodeDtoList();

    when(nodeService.getNodeListByOrgId(any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeDtoPage(2, nodeDtoList, nodeDtoList.size()));

    ResponseEntity<BaseResponse<PagePayload<NodeDto>>> response =
        nodeController.getNodeList(
            TestUtil.ORG_ID,
            testUtil.getPageParams(
                Optional.of(2),
                Optional.of(1),
                Optional.of(TestUtil.SORT_BY),
                Optional.of(TestUtil.SORT_ORDER_DESC)));

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "Success response");
    Assertions.assertEquals(
        2,
        (int) response.getBody().getPayload().getPagination().getTotalPages(),
        "Pagination Total pages");
    Assertions.assertEquals(
        nodeDtoList.size(),
        (int) response.getBody().getPayload().getPagination().getTotalRecords(),
        "Total Elements");
    Assertions.assertEquals(
        2,
        (int) response.getBody().getPayload().getPagination().getCurrentPage(),
        "Current page number");
    Assertions.assertEquals(
        nodeDtoList.size(), response.getBody().getPayload().getData().size(), "Paginated data");
    Assertions.assertEquals(
        "", response.getBody().getPayload().getPagination().getNext(), "Next Uri should be empty");
    Assertions.assertEquals(
        Boolean.TRUE,
        Objects.nonNull(response.getBody().getPayload().getPagination().getPrevious()),
        "Previous Uri should not be null");

    verify(nodeService, times(1)).getNodeListByOrgId(any(), any(), any(), any());
  }

  @Test
  @DisplayName("Get node list test v2")
  void getNodeListV2Test() throws NodeDomainException, CommonServiceException {
    List<NodeDto> nodeDtoList = testUtil.getNodeDtoList();

    when(nodeService.getNodeListByOrgId(any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeDtoPage(2, nodeDtoList, nodeDtoList.size()));

    ResponseEntity<BaseResponse<PagePayload<NodeDto>>> response =
        nodeController.getNodeListV2(
            TestUtil.ORG_ID,
            "node-01,node-02",
            "MFC",
            testUtil.getPageParams(
                Optional.of(2),
                Optional.of(1),
                Optional.of(TestUtil.SORT_BY),
                Optional.of(TestUtil.SORT_ORDER_DESC)));

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "Success response");
    Assertions.assertEquals(
        2,
        (int) response.getBody().getPayload().getPagination().getTotalPages(),
        "Pagination Total pages");
    Assertions.assertEquals(
        nodeDtoList.size(),
        (int) response.getBody().getPayload().getPagination().getTotalRecords(),
        "Total Elements");
    Assertions.assertEquals(
        2,
        (int) response.getBody().getPayload().getPagination().getCurrentPage(),
        "Current page number");
    Assertions.assertEquals(
        nodeDtoList.size(), response.getBody().getPayload().getData().size(), "Paginated data");
    Assertions.assertEquals(
        "", response.getBody().getPayload().getPagination().getNext(), "Next Uri should be empty");
    Assertions.assertEquals(
        Boolean.TRUE,
        Objects.nonNull(response.getBody().getPayload().getPagination().getPrevious()),
        "Previous Uri should not be null");

    verify(nodeService, times(1)).getNodeListByOrgId(any(), any(), any(), any());
  }

  @Test
  void getNodeListDefaultTest() throws NodeDomainException, CommonServiceException {
    List<NodeDto> nodeDtoList = testUtil.getNodeDtoList();

    when(pageProperties.getPageNo()).thenReturn(1);
    when(pageProperties.getPageSize()).thenReturn(15);
    when(nodeService.getNodeListByOrgId(any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeDtoPage(2, nodeDtoList, nodeDtoList.size()));

    ResponseEntity<BaseResponse<PagePayload<NodeDto>>> response =
        nodeController.getNodeList(
            TestUtil.ORG_ID,
            testUtil.getPageParams(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "Success response");
    Assertions.assertEquals(
        2,
        (int) response.getBody().getPayload().getPagination().getTotalPages(),
        "Pagination Total pages");
    Assertions.assertEquals(
        nodeDtoList.size(),
        (int) response.getBody().getPayload().getPagination().getTotalRecords(),
        "Total Elements");
    Assertions.assertEquals(
        1,
        (int) response.getBody().getPayload().getPagination().getCurrentPage(),
        "Current page number");
    Assertions.assertEquals(
        nodeDtoList.size(), response.getBody().getPayload().getData().size(), "Paginated data");
    Assertions.assertEquals(
        "",
        response.getBody().getPayload().getPagination().getPrevious(),
        "Previous Uri should be empty");
    Assertions.assertEquals(
        Boolean.TRUE,
        Objects.nonNull(response.getBody().getPayload().getPagination().getNext()),
        "Next Uri should not be null");

    verify(nodeService, times(1)).getNodeListByOrgId(any(), any(), any(), any());
  }

  @Test
  void getNodeCacheKeysTest() throws NodeDomainException {
    List<NodeCacheKeyDto> nodeCacheKeyDtoList = testUtil.getNodeCacheKeysDtoList();
    List<NodeDto> nodeDtoList = testUtil.getNodeDtoList();

    when(nodeService.getAllNodeCacheKeys(any())).thenReturn(nodeCacheKeyDtoList);

    ResponseEntity<BaseResponse<List<NodeCacheKeyDto>>> response =
        nodeController.getNodeCacheKeys(2);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(nodeCacheKeyDtoList, response.getBody().getPayload());

    verify(nodeService, times(1)).getAllNodeCacheKeys(any());
  }

  @Test
  void getAllNodesListTest() throws NodeDomainException {

    List<NodeDto> nodeDtoList = testUtil.getNodeDtoList();

    when(nodeService.getAllNodes(any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeDtoPage(2, nodeDtoList, nodeDtoList.size()));

    ResponseEntity<BaseResponse<PagePayload<NodeDto>>> response =
        nodeController.getAllNodesList(
            testUtil.getPageParams(
                Optional.of(2),
                Optional.of(1),
                Optional.of(TestUtil.SORT_BY),
                Optional.of(TestUtil.SORT_ORDER_DESC)));

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "Success response");
    Assertions.assertEquals(
        2,
        (int) response.getBody().getPayload().getPagination().getTotalPages(),
        "Pagination Total pages");
    Assertions.assertEquals(
        nodeDtoList.size(),
        (int) response.getBody().getPayload().getPagination().getTotalRecords(),
        "Total Elements");
    Assertions.assertEquals(
        2,
        (int) response.getBody().getPayload().getPagination().getCurrentPage(),
        "Current page number");
    Assertions.assertEquals(
        nodeDtoList.size(), response.getBody().getPayload().getData().size(), "Paginated data");
    Assertions.assertEquals(
        "", response.getBody().getPayload().getPagination().getNext(), "Next Uri should be empty");
    Assertions.assertEquals(
        Boolean.TRUE,
        Objects.nonNull(response.getBody().getPayload().getPagination().getPrevious()),
        "Previous Uri should not be null");

    verify(nodeService, times(1)).getAllNodes(any(), any(), any(), any());
  }

  @Test
  void getNodeListTestV1() throws NodeDomainException, CommonServiceException {
    List<NodeDto> nodeDtoList = testUtil.getNodeDtoListV1();

    when(nodeService.getNodeListByOrgIdV1(any(), any()))
        .thenReturn(testUtil.getNodeDtoPage(4, nodeDtoList, nodeDtoList.size()));

    ResponseEntity<BaseResponse<PagePayload<NodeDto>>> response =
        nodeController.getNodeListV1(
            TestUtil.ORG_ID,
            testUtil.getPageParams(
                Optional.of(2),
                Optional.of(1),
                Optional.of(TestUtil.SORT_BY),
                Optional.of(TestUtil.SORT_ORDER_DESC)));

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "Success response");
    Assertions.assertEquals(
        4,
        (int) response.getBody().getPayload().getPagination().getTotalPages(),
        "Pagination Total pages");
    Assertions.assertEquals(
        nodeDtoList.size(),
        (int) response.getBody().getPayload().getPagination().getTotalRecords(),
        "Total Elements");
    Assertions.assertEquals(
        2,
        (int) response.getBody().getPayload().getPagination().getCurrentPage(),
        "Current page number");
    Assertions.assertEquals(
        nodeDtoList.size(), response.getBody().getPayload().getData().size(), "Paginated data");
    Assertions.assertEquals(
        Boolean.TRUE,
        Objects.nonNull(response.getBody().getPayload().getPagination().getPrevious()),
        "Previous Uri should not be null");

    verify(nodeService, times(1)).getNodeListByOrgIdV1(any(), any());
  }

  @Test
  void getNodeListTestV1WithPageSizeEmpty() throws NodeDomainException, CommonServiceException {
    List<NodeDto> nodeDtoList = testUtil.getNodeDtoListV1();

    when(nodeService.getNodeListByOrgIdV1(any(), any()))
        .thenReturn(testUtil.getNodeDtoPage(1, nodeDtoList, nodeDtoList.size()));

    ResponseEntity<BaseResponse<PagePayload<NodeDto>>> response =
        nodeController.getNodeListV1(
            TestUtil.ORG_ID,
            testUtil.getPageParamsWithoutPageSize(
                Optional.of(2),
                Optional.of(TestUtil.SORT_BY),
                Optional.of(TestUtil.SORT_ORDER_DESC)));

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "Success response");
    Assertions.assertEquals(
        1,
        (int) response.getBody().getPayload().getPagination().getTotalPages(),
        "Pagination Total pages");
    Assertions.assertEquals(
        nodeDtoList.size(),
        (int) response.getBody().getPayload().getPagination().getTotalRecords(),
        "Total Elements");
    Assertions.assertEquals(
        2,
        (int) response.getBody().getPayload().getPagination().getCurrentPage(),
        "Current page number");
    Assertions.assertEquals(
        nodeDtoList.size(), response.getBody().getPayload().getData().size(), "Paginated data");
    Assertions.assertEquals(
        "", response.getBody().getPayload().getPagination().getNext(), "Next Uri should be empty");
    Assertions.assertEquals(
        Boolean.TRUE,
        Objects.nonNull(response.getBody().getPayload().getPagination().getPrevious()),
        "Previous Uri should not be null");

    verify(nodeService, times(1)).getNodeListByOrgIdV1(any(), any());
  }

  @Test
  @DisplayName("Get all unique node types")
  void getNodeTypesTest() throws CommonServiceException {
    when(nodeService.getAllNodeTypesByOrgId(any()))
        .thenReturn(NodeTypesResponse.builder().nodeTypes(List.of("DC", "MFC")).build());
    ResponseEntity<BaseResponse<NodeTypesResponse>> response =
        nodeController.getAllNodeTypes(TestUtil.ORG_ID);
    Assertions.assertEquals(List.of("DC", "MFC"), response.getBody().getPayload().getNodeTypes());
    verify(nodeService, times(1)).getAllNodeTypesByOrgId(any());
  }

  @Test
  @DisplayName("Upsert node - should create or update node successfully")
  void upsertNodeSuccessTest() throws NodeDomainException, CommonServiceException {
    NodeRequest nodeRequest = testUtil.getNodeRequest();
    NodeResponse expectedResponse = testUtil.getNodeResponse();

    when(nodeService.upsertNode(any(NodeRequest.class))).thenReturn(expectedResponse);

    ResponseEntity<BaseResponse<NodeResponse>> response = nodeController.upsertNode(nodeRequest);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "Should return HTTP 200");
    Assertions.assertNotNull(response.getBody(), "Response body should not be null");
    Assertions.assertEquals(
        expectedResponse, response.getBody().getPayload(), "Payload should match expected");
    Assertions.assertEquals(
        "Node saved successfully", response.getBody().getMessage(), "Success message should match");

    verify(nodeService, times(1)).upsertNode(any(NodeRequest.class));
  }

  @Test
  @DisplayName("Upsert node - should throw exception when node creation/updation fails")
  void upsertNodeFailureTest() throws NodeDomainException, CommonServiceException {
    NodeRequest nodeRequest = testUtil.getNodeRequest();

    when(nodeService.upsertNode(any(NodeRequest.class)))
        .thenThrow(new RuntimeException("Failed to create or update node"));

    Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () -> nodeController.upsertNode(nodeRequest),
            "Should throw exception when service fails");

    Assertions.assertEquals(
        "Failed to create or update node",
        exception.getMessage(),
        "Exception message should match");

    verify(nodeService, times(1)).upsertNode(any(NodeRequest.class));
  }
}
