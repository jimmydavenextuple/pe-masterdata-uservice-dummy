package com.hbc.node.carrier.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.node.carrier.TestUtil;
import com.hbc.node.carrier.domain.NodeCarrierDomain;
import com.hbc.node.carrier.domain.entity.NodeCarrierEntity;
import com.hbc.node.carrier.domain.inbound.NodeCarrierRequest;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
import com.hbc.node.carrier.exception.InvalidDataException;
import com.hbc.node.carrier.exception.NodeCarrierDomainException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NodeCarrierServiceTest {

  @InjectMocks NodeCarrierService nodeCarrierService;

  @Mock NodeCarrierDomain nodeCarrierDomain;

  @InjectMocks TestUtil testUtil;

  @Test
  @DisplayName("When node carrier is created successfully")
  void createNodeCarrierTest()
      throws NodeCarrierDomainException, CommonServiceException, InvalidDataException {
    NodeCarrierRequest nodeCarrierRequest = testUtil.getNodeCarrierRequest();
    when(nodeCarrierDomain.findNodeCarrierDetails(any(), any(), any(), any()))
        .thenReturn(Optional.empty());
    when(nodeCarrierDomain.saveNodeCarrierEntity(any()))
        .thenReturn(testUtil.getNodeCarrierEntity());

    NodeCarrierResponse nodeCarrierResponse =
        nodeCarrierService.createNodeCarrier(nodeCarrierRequest);

    Assertions.assertEquals(
        nodeCarrierRequest.getCarrierServiceId(), nodeCarrierResponse.getCarrierServiceId());
    verify(nodeCarrierDomain, times(1)).saveNodeCarrierEntity(any());
  }

  @Test
  @DisplayName("When bufferEndDate is before current date")
  void createNodeCarrierBufferEndDateExceptionTest()
          throws NodeCarrierDomainException, CommonServiceException, InvalidDataException {
    NodeCarrierRequest nodeCarrierRequest = testUtil.getNodeCarrierRequest2();
    Exception ex =
            Assertions.assertThrows(
                    CommonServiceException.class,
                    () -> nodeCarrierService.createNodeCarrier(nodeCarrierRequest));

    Assertions.assertEquals(
            "bufferEndDate cannot be before the current date.", ex.getMessage());
  }

  @Test
  @DisplayName("When node carrier to be created already exists")
  void createNodeCarrierTestException() throws NodeCarrierDomainException {
    NodeCarrierRequest nodeCarrierRequest = testUtil.getNodeCarrierRequest();
    when(nodeCarrierDomain.findNodeCarrierDetails(any(), any(), any(), any()))
        .thenReturn(Optional.of(testUtil.getNodeCarrierEntity()));

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> nodeCarrierService.createNodeCarrier(nodeCarrierRequest));

    Assertions.assertEquals("Node Carrier already exists for the given details", ex.getMessage());
    verify(nodeCarrierDomain, times(1)).findNodeCarrierDetails(any(), any(), any(), any());
    verify(nodeCarrierDomain, times(0)).saveNodeCarrierEntity(any());
  }

  @Test
  @DisplayName("When node carrier details is fetched successfully")
  void getNodeCarrierDetailsTest() throws NodeCarrierDomainException, CommonServiceException {
    when(nodeCarrierDomain.filterAndGetNodeCarrierDetails(any(), any(), any(), any()))
        .thenReturn(List.of(testUtil.getNodeCarrierEntity()));

    NodeCarrierResponse nodeCarrierResponse =
        nodeCarrierService.getNodeCarrierDetails(
            TestUtil.NODE_ID,
            TestUtil.ORG_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SERVICE_OPTION);

    Assertions.assertEquals(testUtil.getNodeCarrierResponse(), nodeCarrierResponse);
    verify(nodeCarrierDomain, times(1)).filterAndGetNodeCarrierDetails(any(), any(), any(), any());
  }

  @Test
  @DisplayName("When node carrier to be fetched is not found")
  void getNodeCarrierDetailsNotFoundTest() throws NodeCarrierDomainException {
    List<NodeCarrierEntity> nodeCarrierEntityList = Collections.<NodeCarrierEntity>emptyList();
    when(nodeCarrierDomain.filterAndGetNodeCarrierDetails(any(), any(), any(), any()))
        .thenReturn(nodeCarrierEntityList);

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                nodeCarrierService.getNodeCarrierDetails(
                    TestUtil.NODE_ID,
                    TestUtil.ORG_ID,
                    TestUtil.CARRIER_SERVICE_ID,
                    TestUtil.SERVICE_OPTION));

    Assertions.assertEquals("Node Carrier not found for given details", ex.getMessage());
    verify(nodeCarrierDomain, times(1)).filterAndGetNodeCarrierDetails(any(), any(), any(), any());
  }

  @Test
  void getNodeCarrierDetailsInvalidServiceOptionTestException() throws NodeCarrierDomainException {
    when(nodeCarrierDomain.filterAndGetNodeCarrierDetails(any(), any(), any(), any()))
        .thenReturn(List.of(testUtil.getNodeCarrierEntity()));

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                nodeCarrierService.getNodeCarrierDetails(
                    TestUtil.NODE_ID, TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID, "Standard"));

    Assertions.assertEquals("Node Carrier not found for given details", ex.getMessage());
    verify(nodeCarrierDomain, times(1)).filterAndGetNodeCarrierDetails(any(), any(), any(), any());
  }

  @Test
  @DisplayName("When node carrier is updated successfully")
  void updateNodeCarrierTest()
      throws NodeCarrierDomainException, CommonServiceException, InvalidDataException {
    when(nodeCarrierDomain.findNodeCarrierDetails(any(), any(), any(), any()))
        .thenReturn(Optional.ofNullable(testUtil.getNodeCarrierEntity()));
    when(nodeCarrierDomain.saveNodeCarrierEntity(any()))
        .thenReturn(testUtil.getNodeCarrierEntity());

    NodeCarrierResponse nodeCarrierResponse =
        nodeCarrierService.updateNodeCarrier(
            TestUtil.NODE_ID,
            TestUtil.ORG_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SERVICE_OPTION,
            testUtil.getNodeCarrierUpdateRequest());

    Assertions.assertEquals(testUtil.getNodeCarrierResponse(), nodeCarrierResponse);

    verify(nodeCarrierDomain, times(1)).findNodeCarrierDetails(any(), any(), any(), any());
    verify(nodeCarrierDomain, times(1)).saveNodeCarrierEntity(any());
  }

  @Test
  @DisplayName("When bufferEndDate is before current date")
  void updateNodeCarrierBufferEndDateExceptionTest()
          throws NodeCarrierDomainException, CommonServiceException, InvalidDataException {
    NodeCarrierRequest nodeCarrierRequest = testUtil.getNodeCarrierRequest2();
    Exception ex =
            Assertions.assertThrows(
                    CommonServiceException.class,
                    () -> nodeCarrierService.updateNodeCarrier(testUtil.NODE_ID,
                            testUtil.ORG_ID,
                            testUtil.CARRIER_SERVICE_ID,
                            testUtil.SERVICE_OPTION,
                            testUtil.getNodeCarrierUpdateRequest2()));

    Assertions.assertEquals(
            "bufferEndDate cannot be before the current date.", ex.getMessage());
  }
  @Test
  @DisplayName("When node carrier to be updated is not found")
  void updateNodeCarrierNotFoundTest() throws NodeCarrierDomainException {
    when(nodeCarrierDomain.findNodeCarrierDetails(any(), any(), any(), any()))
        .thenReturn(Optional.empty());

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                nodeCarrierService.updateNodeCarrier(
                    TestUtil.NODE_ID,
                    TestUtil.ORG_ID,
                    TestUtil.CARRIER_SERVICE_ID,
                    TestUtil.SERVICE_OPTION,
                    testUtil.getNodeCarrierUpdateRequest()));

    Assertions.assertEquals("Node Carrier not found for given details", ex.getMessage());
    verify(nodeCarrierDomain, times(1)).findNodeCarrierDetails(any(), any(), any(), any());
    verify(nodeCarrierDomain, times(0)).saveNodeCarrierEntity(any());
  }

  @Test
  @DisplayName("When node carrier is deleted successfully")
  void deleteNodeCarrierTest() throws NodeCarrierDomainException, CommonServiceException {
    when(nodeCarrierDomain.findNodeCarrierDetails(any(), any(), any(), any()))
        .thenReturn(Optional.ofNullable(testUtil.getNodeCarrierEntity()));
    doNothing().when(nodeCarrierDomain).deleteNodeCarrierEntity(any());

    NodeCarrierResponse nodeCarrierResponse =
        nodeCarrierService.deleteNodeCarrier(
            TestUtil.NODE_ID,
            TestUtil.ORG_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SERVICE_OPTION);

    Assertions.assertEquals(testUtil.getNodeCarrierResponse(), nodeCarrierResponse);
    verify(nodeCarrierDomain, times(1)).findNodeCarrierDetails(any(), any(), any(), any());
    verify(nodeCarrierDomain, times(1)).deleteNodeCarrierEntity(any());
  }

  @Test
  @DisplayName("When node carrier to be deleted is not found")
  void deleteNodeCarrierNotFoundTestException() throws NodeCarrierDomainException {
    when(nodeCarrierDomain.findNodeCarrierDetails(any(), any(), any(), any()))
        .thenReturn(Optional.empty());

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                nodeCarrierService.deleteNodeCarrier(
                    TestUtil.NODE_ID,
                    TestUtil.ORG_ID,
                    TestUtil.CARRIER_SERVICE_ID,
                    TestUtil.SERVICE_OPTION));

    Assertions.assertEquals("Node Carrier not found for given details", ex.getMessage());
    verify(nodeCarrierDomain, times(1)).findNodeCarrierDetails(any(), any(), any(), any());
    verify(nodeCarrierDomain, times(0)).deleteNodeCarrierEntity(any());
  }

  @Test
  void getNodeCarrierForNodeIdAOrgIdAndServiceOptionTest()
      throws NodeCarrierDomainException, CommonServiceException {
    List<NodeCarrierEntity> nodeCarrierEntityList = testUtil.getNodeCarrierEntityList();
    when(nodeCarrierDomain.findNodeCarrierByNodeIdAOrgIdAndServiceOption(any(), any(), any()))
        .thenReturn(nodeCarrierEntityList);

    List<NodeCarrierResponse> nodeCarrierResponseList =
        nodeCarrierService.getNodeCarrierForNodeIdAOrgIdAndServiceOption(
            TestUtil.NODE_ID, TestUtil.ORG_ID, TestUtil.SERVICE_OPTION);

    Assertions.assertEquals(2, nodeCarrierResponseList.size());
    Assertions.assertEquals(testUtil.getNodeCarrierDtoList(), nodeCarrierResponseList);
    verify(nodeCarrierDomain, times(1))
        .findNodeCarrierByNodeIdAOrgIdAndServiceOption(any(), any(), any());
  }

  @Test
  @DisplayName("When node carrier list to be fetched is not found")
  void getNodeCarrierForNodeIdAOrgIdAndServiceOptionNotFoundTest()
      throws NodeCarrierDomainException {
    List<NodeCarrierEntity> entityList = Collections.<NodeCarrierEntity>emptyList();
    when(nodeCarrierDomain.findNodeCarrierByNodeIdAOrgIdAndServiceOption(any(), any(), any()))
        .thenReturn(entityList);

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                nodeCarrierService.getNodeCarrierForNodeIdAOrgIdAndServiceOption(
                    TestUtil.NODE_ID, TestUtil.ORG_ID, TestUtil.SERVICE_OPTION));

    Assertions.assertEquals("Node Carrier not found for given details", ex.getMessage());
    verify(nodeCarrierDomain, times(1))
        .findNodeCarrierByNodeIdAOrgIdAndServiceOption(any(), any(), any());
  }

  @Test
  void validateLastPickupTimeTest() {
    String lastPickUpTime = "14:89";
    Exception ex =
        Assertions.assertThrows(
            InvalidDataException.class,
            () -> nodeCarrierService.validateLastPickupTime(lastPickUpTime));
    Assertions.assertEquals("LastPickupTime is invalid", ex.getMessage());
  }
}
