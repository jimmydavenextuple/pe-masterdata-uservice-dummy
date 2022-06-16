package com.nextuple.node.exception;

import com.nextuple.node.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NodeDomainExceptionTest {

  @Test
  @DisplayName("Testing NodeDomainException")
  void constructTest() {
    NodeDomainException nodeDomainException =
        new NodeDomainException("test", TestUtil.NODE_ID, TestUtil.ORG_ID);
    Assertions.assertEquals("test", nodeDomainException.getMessage());
    Assertions.assertEquals(TestUtil.NODE_ID, nodeDomainException.getNodeId());
    Assertions.assertEquals(TestUtil.ORG_ID, nodeDomainException.getOrgId());
  }
}
