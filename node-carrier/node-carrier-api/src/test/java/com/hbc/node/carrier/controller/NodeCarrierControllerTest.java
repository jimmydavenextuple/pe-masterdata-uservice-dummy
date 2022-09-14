package com.hbc.node.carrier.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.node.carrier.TestUtil;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
import com.hbc.node.carrier.domain.outbound.NodeCarrierSelectionResponse;
import com.hbc.node.carrier.exception.InvalidDataException;
import com.hbc.node.carrier.exception.NodeCarrierDomainException;
import com.hbc.node.carrier.service.NodeCarrierService;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class NodeCarrierControllerTest {

  @InjectMocks NodeCarrierController nodeCarrierController;

  @Mock NodeCarrierService nodeCarrierService;

  @InjectMocks TestUtil testUtil;

  @Test
  @DisplayName("When node carrier is created successfully and response is 200 OK")
  void createNodeCarrierTest()
      throws NodeCarrierDomainException, CommonServiceException, InvalidDataException {
    when(nodeCarrierService.createNodeCarrier(any())).thenReturn(testUtil.getNodeCarrierResponse());

    ResponseEntity<BaseResponse<NodeCarrierResponse>> response =
        nodeCarrierController.createNodeCarrier(testUtil.getNodeCarrierRequest());

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(
        TestUtil.NODE_ID, Objects.requireNonNull(response.getBody()).getPayload().getNodeId());
    Assertions.assertEquals(
        TestUtil.ORG_ID, Objects.requireNonNull(response.getBody()).getPayload().getOrgId());
    verify(nodeCarrierService, times(1)).createNodeCarrier(any());
  }

  @Test
  @DisplayName("When there is somme error in creating node carrier")
  void createNodeCarrierExceptionTest()
      throws NodeCarrierDomainException, CommonServiceException, InvalidDataException {
    when(nodeCarrierService.createNodeCarrier(any()))
        .thenThrow(new RuntimeException("Failed to create node carrier details"));

    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () -> nodeCarrierController.createNodeCarrier(testUtil.getNodeCarrierRequest()));

    Assertions.assertEquals("Failed to create node carrier details", ex.getMessage());
    verify(nodeCarrierService, times(1)).createNodeCarrier(any());
  }

  @Test
  @DisplayName("When node carrier is updated successfully with buffer data and response is 200 OK")
  void createOrUpdateBufferTest() throws NodeCarrierDomainException, CommonServiceException {
    when(nodeCarrierService.updateBufferData(any())).thenReturn(testUtil.getNodeCarrierResponse2());

    ResponseEntity<BaseResponse<NodeCarrierResponse>> response =
        nodeCarrierController.updateBuffer(testUtil.getNodeCarrierBufferRequest2());

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(
        TestUtil.NODE_ID, Objects.requireNonNull(response.getBody()).getPayload().getNodeId());
    Assertions.assertEquals(
        TestUtil.ORG_ID, Objects.requireNonNull(response.getBody()).getPayload().getOrgId());
    verify(nodeCarrierService, times(1)).updateBufferData(any());
  }

  @Test
  @DisplayName("When there is somme error in updating buffer data in node carrier")
  void createOrUpdateBufferExceptionTest()
      throws NodeCarrierDomainException, CommonServiceException {
    when(nodeCarrierService.updateBufferData(any()))
        .thenThrow(new RuntimeException("Failed to update node carrier buffer details"));

    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () -> nodeCarrierController.updateBuffer(testUtil.getNodeCarrierBufferRequest2()));

    Assertions.assertEquals("Failed to update node carrier buffer details", ex.getMessage());
    verify(nodeCarrierService, times(1)).updateBufferData(any());
  }

  @Test
  @DisplayName("When node carrier is fetched successfully and response is 200 OK")
  void getNodeCarrierTest() throws NodeCarrierDomainException, CommonServiceException {
    when(nodeCarrierService.getNodeCarrierDetails(any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeCarrierResponse());

    ResponseEntity<BaseResponse<NodeCarrierResponse>> response =
        nodeCarrierController.getNodeCarrier(
            TestUtil.NODE_ID,
            TestUtil.ORG_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SERVICE_OPTION);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(
        TestUtil.NODE_ID, Objects.requireNonNull(response.getBody()).getPayload().getNodeId());
    Assertions.assertEquals(
        TestUtil.ORG_ID, Objects.requireNonNull(response.getBody()).getPayload().getOrgId());
    verify(nodeCarrierService, times(1)).getNodeCarrierDetails(any(), any(), any(), any());
  }

  @Test
  @DisplayName("When there is somme error in fetching node carrier")
  void getNodeCarrierExceptionTest() throws NodeCarrierDomainException, CommonServiceException {
    when(nodeCarrierService.getNodeCarrierDetails(any(), any(), any(), any()))
        .thenThrow(new RuntimeException("Failed to get node carrier details"));

    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () ->
                nodeCarrierController.getNodeCarrier(
                    TestUtil.NODE_ID,
                    TestUtil.ORG_ID,
                    TestUtil.CARRIER_SERVICE_ID,
                    TestUtil.SERVICE_OPTION));

    Assertions.assertEquals("Failed to get node carrier details", ex.getMessage());
    verify(nodeCarrierService, times(1)).getNodeCarrierDetails(any(), any(), any(), any());
  }

  @Test
  @DisplayName("When node carrier is updated successfully and response is 200 OK")
  void updateNodeCarrierTest()
      throws NodeCarrierDomainException, CommonServiceException, InvalidDataException {
    when(nodeCarrierService.updateNodeCarrier(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeCarrierResponse());

    ResponseEntity<BaseResponse<NodeCarrierResponse>> response =
        nodeCarrierController.updateNodeCarrier(
            TestUtil.NODE_ID,
            TestUtil.ORG_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SERVICE_OPTION,
            testUtil.getNodeCarrierUpdateRequest());

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(
        TestUtil.NODE_ID, Objects.requireNonNull(response.getBody()).getPayload().getNodeId());
    Assertions.assertEquals(
        TestUtil.ORG_ID, Objects.requireNonNull(response.getBody()).getPayload().getOrgId());
    verify(nodeCarrierService, times(1)).updateNodeCarrier(any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("When there is somme error in updating node carrier")
  void updateNodeCarrierExceptionTest()
      throws NodeCarrierDomainException, CommonServiceException, InvalidDataException {
    when(nodeCarrierService.updateNodeCarrier(any(), any(), any(), any(), any()))
        .thenThrow(new RuntimeException("Failed to update node carrier details"));

    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () ->
                nodeCarrierController.updateNodeCarrier(
                    TestUtil.NODE_ID,
                    TestUtil.ORG_ID,
                    TestUtil.CARRIER_SERVICE_ID,
                    TestUtil.SERVICE_OPTION,
                    testUtil.getNodeCarrierUpdateRequest()));

    Assertions.assertEquals("Failed to update node carrier details", ex.getMessage());
    verify(nodeCarrierService, times(1)).updateNodeCarrier(any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("When node carrier is deleted successfully and response is 200 OK")
  void deleteNodeCarrierTest() throws NodeCarrierDomainException, CommonServiceException {
    when(nodeCarrierService.deleteNodeCarrier(any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeCarrierResponse());

    ResponseEntity<BaseResponse<NodeCarrierResponse>> response =
        nodeCarrierController.deleteNodeCarrier(
            TestUtil.NODE_ID,
            TestUtil.ORG_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SERVICE_OPTION);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(
        TestUtil.NODE_ID, Objects.requireNonNull(response.getBody()).getPayload().getNodeId());
    Assertions.assertEquals(
        TestUtil.ORG_ID, Objects.requireNonNull(response.getBody()).getPayload().getOrgId());
    verify(nodeCarrierService, times(1)).deleteNodeCarrier(any(), any(), any(), any());
  }

  @Test
  @DisplayName("When there is somme error in deleting node carrier")
  void deleteNodeCarrierExceptionTest() throws NodeCarrierDomainException, CommonServiceException {
    when(nodeCarrierService.deleteNodeCarrier(any(), any(), any(), any()))
        .thenThrow(new RuntimeException("Failed to delete node carrier details"));

    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () ->
                nodeCarrierController.deleteNodeCarrier(
                    TestUtil.NODE_ID,
                    TestUtil.ORG_ID,
                    TestUtil.CARRIER_SERVICE_ID,
                    TestUtil.SERVICE_OPTION));

    Assertions.assertEquals("Failed to delete node carrier details", ex.getMessage());
    verify(nodeCarrierService, times(1)).deleteNodeCarrier(any(), any(), any(), any());
  }

  @Test
  @DisplayName("When node carrier list is fetched successfully and response is 200 OK")
  void getNodeCarrierListTest() throws NodeCarrierDomainException, CommonServiceException {
    when(nodeCarrierService.getNodeCarrierForNodeIdAOrgIdAndServiceOption(any(), any(), any()))
        .thenReturn(testUtil.getNodeCarrierDtoList());

    ResponseEntity<BaseResponse<List<NodeCarrierResponse>>> response =
        nodeCarrierController.getNodeCarrier(
            TestUtil.NODE_ID, TestUtil.ORG_ID, TestUtil.SERVICE_OPTION);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(
        TestUtil.NODE_ID,
        Objects.requireNonNull(response.getBody()).getPayload().get(0).getNodeId());
    Assertions.assertEquals(
        TestUtil.ORG_ID, Objects.requireNonNull(response.getBody()).getPayload().get(0).getOrgId());
    verify(nodeCarrierService, times(1))
        .getNodeCarrierForNodeIdAOrgIdAndServiceOption(any(), any(), any());
  }

  @Test
  @DisplayName("When there is somme error in fetching node carrier details list")
  void getNodeCarrierListExceptionTest() throws NodeCarrierDomainException, CommonServiceException {
    when(nodeCarrierService.getNodeCarrierForNodeIdAOrgIdAndServiceOption(any(), any(), any()))
        .thenThrow(new RuntimeException("Failed to fetch node carrier details list"));

    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () ->
                nodeCarrierController.getNodeCarrier(
                    TestUtil.NODE_ID, TestUtil.ORG_ID, TestUtil.SERVICE_OPTION));

    Assertions.assertEquals("Failed to fetch node carrier details list", ex.getMessage());
    verify(nodeCarrierService, times(1))
        .getNodeCarrierForNodeIdAOrgIdAndServiceOption(any(), any(), any());
  }

  @Test
  void getNodeCarrierList() throws NodeCarrierDomainException {
    when(nodeCarrierService.getNodeCarrierForNodeIdAndOrgId(anyString(), anyString()))
        .thenReturn(testUtil.getNodeCarrierDtoList2());

    ResponseEntity<BaseResponse<List<NodeCarrierResponse>>> response =
        nodeCarrierController.getNodeCarrierList(TestUtil.NODE_ID, TestUtil.ORG_ID);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(
        TestUtil.NODE_ID,
        Objects.requireNonNull(response.getBody()).getPayload().get(0).getNodeId());
    Assertions.assertEquals(
        TestUtil.ORG_ID, Objects.requireNonNull(response.getBody()).getPayload().get(0).getOrgId());
    verify(nodeCarrierService, times(1)).getNodeCarrierForNodeIdAndOrgId(anyString(), anyString());
  }

  @Test
  void updateProcessingLeadTime() throws NodeCarrierDomainException {
    when(nodeCarrierService.updateProcessingLeadTime(testUtil.getNodeCarrierRequest()))
        .thenReturn(testUtil.getNodeCarrierResponse());
    ResponseEntity<BaseResponse<NodeCarrierResponse>> response =
        nodeCarrierController.updateProcessingLeadTime(testUtil.getNodeCarrierRequest());
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getPayload());
    verify(nodeCarrierService, times(1)).updateProcessingLeadTime(any());
  }

  @Test
  @DisplayName("When node carrier selection is created successfully and response is 200 OK")
  void createNodeCarrierSelectionTest() {
    when(nodeCarrierService.addNodeCarrierSelectionPriority(any()))
        .thenReturn(testUtil.getNodeCarrierSelectionResponse());

    ResponseEntity<BaseResponse<NodeCarrierSelectionResponse>> response =
        nodeCarrierController.addNodeCarrierSelectionPriority(
            testUtil.getNodeCarrierSelectionRequest());

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(
        TestUtil.SERVICE_OPTION,
        Objects.requireNonNull(response.getBody()).getPayload().getServiceOption());
    Assertions.assertEquals(
        TestUtil.ORG_ID, Objects.requireNonNull(response.getBody()).getPayload().getOrgId());
    verify(nodeCarrierService, times(1)).addNodeCarrierSelectionPriority(any());
  }

  @Test
  @DisplayName("When there is somme error in creating node carrier details selection")
  void saveNodeCarrierSelectionExceptionTest() {
    when(nodeCarrierService.addNodeCarrierSelectionPriority(any()))
        .thenThrow(new RuntimeException("Failed to fetch node carrier details list"));

    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () ->
                nodeCarrierController.addNodeCarrierSelectionPriority(
                    testUtil.getNodeCarrierSelectionRequest()));

    Assertions.assertEquals("Failed to fetch node carrier details list", ex.getMessage());
    verify(nodeCarrierService, times(1)).addNodeCarrierSelectionPriority(any());
  }

  @Test
  void getNodeCarrierSelectionList() {
    when(nodeCarrierService.getNodeCarrierSelectionDetails(anyString(), anyString(), anyString()))
        .thenReturn(List.of(testUtil.getNodeCarrierSelectionResponse()));

    ResponseEntity<BaseResponse<List<NodeCarrierSelectionResponse>>> response =
        nodeCarrierController.getNodeCarrierSelectionDetails(
            TestUtil.ORG_ID, TestUtil.SOURCE_GEOZONE, TestUtil.DESTINATION_GEOZONE);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(
        TestUtil.SERVICE_OPTION,
        Objects.requireNonNull(response.getBody()).getPayload().get(0).getServiceOption());
    Assertions.assertEquals(
        TestUtil.ORG_ID, Objects.requireNonNull(response.getBody()).getPayload().get(0).getOrgId());
    verify(nodeCarrierService, times(1))
        .getNodeCarrierSelectionDetails(anyString(), anyString(), anyString());
  }

  @Test
  @DisplayName("When there is somme error in fetching node carrier details selection")
  void getNodeCarrierSelectionExceptionTest() {
    when(nodeCarrierService.getNodeCarrierSelectionDetails(any(), any(), any()))
        .thenThrow(new RuntimeException("Failed to fetch node carrier details list"));

    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () ->
                nodeCarrierController.getNodeCarrierSelectionDetails(
                    TestUtil.ORG_ID, TestUtil.SERVICE_OPTION, TestUtil.DESTINATION_GEOZONE));

    Assertions.assertEquals("Failed to fetch node carrier details list", ex.getMessage());
    verify(nodeCarrierService, times(1)).getNodeCarrierSelectionDetails(any(), any(), any());
  }

  @Test
  @DisplayName("When node carrier is deleted successfully and response is 200 OK")
  void deleteNodeCarrierByOrgIdNodeIdAndServiceOption()
      throws NodeCarrierDomainException, CommonServiceException {
    when(nodeCarrierService.deleteNodeCarrier(any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeCarrierResponse());

    ResponseEntity<BaseResponse<NodeCarrierResponse>> response =
        nodeCarrierController.deleteNodeCarrierByOrgIdNodeIdAndServiceOption(
            TestUtil.NODE_ID, TestUtil.ORG_ID, null, TestUtil.SERVICE_OPTION);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(
        TestUtil.NODE_ID, Objects.requireNonNull(response.getBody()).getPayload().getNodeId());
    Assertions.assertEquals(
        TestUtil.ORG_ID, Objects.requireNonNull(response.getBody()).getPayload().getOrgId());
    verify(nodeCarrierService, times(1)).deleteNodeCarrier(any(), any(), any(), any());
  }
}
