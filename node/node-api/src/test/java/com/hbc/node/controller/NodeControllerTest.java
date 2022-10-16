package com.hbc.node.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.hbc.common.base.PagePayload;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.pojo.PageProperties;
import com.hbc.common.response.BaseResponse;
import com.hbc.node.TestUtil;
import com.hbc.node.domain.dto.NodeCacheKeyDto;
import com.hbc.node.domain.dto.NodeDto;
import com.hbc.node.domain.inbound.NodeRequest;
import com.hbc.node.domain.inbound.NodeUpdationRequest;
import com.hbc.node.domain.outbound.NodeResponse;
import com.hbc.node.exception.NodeDomainException;
import com.hbc.node.service.NodeService;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
  void createNodeTest() throws NodeDomainException, CommonServiceException {
    NodeRequest nodeRequest = testUtil.getNodeRequest();
    when(nodeService.createNode(any(NodeRequest.class))).thenReturn(testUtil.getNodeResponse());

    ResponseEntity<BaseResponse<NodeResponse>> responseEntity =
        nodeController.createNode(nodeRequest);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(testUtil.getNodeResponse(), responseEntity.getBody().getPayload());

    verify(nodeService, times(1)).createNode(any());
  }

  @Test
  void createNodeExceptionTest() throws NodeDomainException, CommonServiceException {
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
    NodeUpdationRequest nodeUpdationRequest = testUtil.getNodeUpdationRequest();
    when(nodeService.updateNodeDetails(anyString(), anyString(), any(NodeUpdationRequest.class)))
        .thenReturn(testUtil.getUpdatedNodeResponse());

    ResponseEntity<BaseResponse<NodeResponse>> responseEntity =
        nodeController.updateNodeDetails(TestUtil.NODE_ID, TestUtil.ORG_ID, nodeUpdationRequest);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(
        testUtil.getUpdatedNodeResponse(), responseEntity.getBody().getPayload());

    verify(nodeService, times(1)).updateNodeDetails(anyString(), anyString(), any());
  }

  @Test
  void updateNodeExceptionTest() throws NodeDomainException, CommonServiceException {
    NodeUpdationRequest nodeUpdationRequest = testUtil.getNodeUpdationRequest();
    when(nodeService.updateNodeDetails(anyString(), anyString(), any(NodeUpdationRequest.class)))
        .thenThrow(new RuntimeException("Unable to update the node details"));

    Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () ->
                nodeController.updateNodeDetails(
                    TestUtil.NODE_ID, TestUtil.ORG_ID, nodeUpdationRequest));
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

    when(nodeService.getNodeListByOrgId(any(), any(), any(), any(), any()))
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

    verify(nodeService, times(1)).getNodeListByOrgId(any(), any(), any(), any(), any());
  }

  @Test
  void getNodeListDefaultTest() throws NodeDomainException, CommonServiceException {
    List<NodeDto> nodeDtoList = testUtil.getNodeDtoList();

    when(pageProperties.getPageNo()).thenReturn(1);
    when(pageProperties.getPageSize()).thenReturn(15);
    when(nodeService.getNodeListByOrgId(any(), any(), any(), any(), any()))
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

    verify(nodeService, times(1)).getNodeListByOrgId(any(), any(), any(), any(), any());
  }

  @Test
  void getNodeCacheKeysTest() throws NodeDomainException {
    List<NodeCacheKeyDto> nodeCacheKeyDtoList = testUtil.getNodeCacheKeysDtoList();

    when(nodeService.getAllNodeCacheKeys(any())).thenReturn(nodeCacheKeyDtoList);

    ResponseEntity<BaseResponse<List<NodeCacheKeyDto>>> response =
        nodeController.getNodeCacheKeys(2);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(nodeCacheKeyDtoList, response.getBody().getPayload());

    verify(nodeService, times(1)).getAllNodeCacheKeys(any());
  }

  @Test
  void getAllNodesListTest() throws NodeDomainException{
    List<NodeResponse> nodeResponseList = List.of(testUtil.getNodeResponse());

    when(nodeService.getAllNodes(any(), any(), any(), any()))
            .thenReturn(testUtil.getNodeResponsePage(2, nodeResponseList, nodeResponseList.size()));

    ResponseEntity<BaseResponse<PagePayload<NodeResponse>>> response =
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
            nodeResponseList.size(),
            (int) response.getBody().getPayload().getPagination().getTotalRecords(),
            "Total Elements");
    Assertions.assertEquals(
            2,
            (int) response.getBody().getPayload().getPagination().getCurrentPage(),
            "Current page number");
    Assertions.assertEquals(
            nodeResponseList.size(), response.getBody().getPayload().getData().size(), "Paginated data");
    Assertions.assertEquals(
            "", response.getBody().getPayload().getPagination().getNext(), "Next Uri should be empty");
    Assertions.assertEquals(
            Boolean.TRUE,
            Objects.nonNull(response.getBody().getPayload().getPagination().getPrevious()),
            "Previous Uri should not be null");

    verify(nodeService, times(1)).getAllNodes(any(), any(), any(), any());
  }

}
