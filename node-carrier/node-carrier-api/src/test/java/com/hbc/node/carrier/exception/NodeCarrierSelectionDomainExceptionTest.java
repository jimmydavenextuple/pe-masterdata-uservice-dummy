package com.hbc.node.carrier.exception;

import com.hbc.node.carrier.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NodeCarrierSelectionDomainExceptionTest {

  @Test
  @DisplayName("Testing node carrier selection domain exception")
  void constructTest() {
    NodeCarrierSelectionDomainException nodeCarrierSelectionDomainException =
        new NodeCarrierSelectionDomainException(
            "Error while fetching the node carrier selection",
            TestUtil.ORG_ID,
            TestUtil.SERVICE_OPTION,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE);

    Assertions.assertEquals(
        "Error while fetching the node carrier selection",
        nodeCarrierSelectionDomainException.getMessage());
    Assertions.assertEquals(
        TestUtil.SERVICE_OPTION, nodeCarrierSelectionDomainException.getServiceOption());
    Assertions.assertEquals(TestUtil.ORG_ID, nodeCarrierSelectionDomainException.getOrgId());
    Assertions.assertEquals(
        TestUtil.SOURCE_GEOZONE, nodeCarrierSelectionDomainException.getSourceGeozone());
    Assertions.assertEquals(
        TestUtil.DESTINATION_GEOZONE, nodeCarrierSelectionDomainException.getDestinationGeozone());
  }
}
