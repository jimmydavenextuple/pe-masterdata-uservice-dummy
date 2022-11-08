package com.nextuple.node.carrier.exception;

import com.nextuple.node.carrier.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NodeCarrierDomainExceptionTest {

  @Test
  @DisplayName("Testing node carrier domain exception")
  void constructTest() {
    NodeCarrierDomainException nodeCarrierDomainException =
        new NodeCarrierDomainException(
            "Error while saving the node carrier",
            TestUtil.NODE_ID,
            TestUtil.ORG_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SERVICE_OPTION);

    Assertions.assertEquals(
        "Error while saving the node carrier", nodeCarrierDomainException.getMessage());
    Assertions.assertEquals(TestUtil.NODE_ID, nodeCarrierDomainException.getNodeId());
    Assertions.assertEquals(TestUtil.ORG_ID, nodeCarrierDomainException.getOrgId());
    Assertions.assertEquals(
        TestUtil.CARRIER_SERVICE_ID, nodeCarrierDomainException.getCarrierServiceId());
    Assertions.assertEquals(TestUtil.SERVICE_OPTION, nodeCarrierDomainException.getServiceOption());
  }
}
