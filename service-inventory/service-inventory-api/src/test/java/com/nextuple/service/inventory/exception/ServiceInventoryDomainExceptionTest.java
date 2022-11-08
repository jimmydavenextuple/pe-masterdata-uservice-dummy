package com.nextuple.service.inventory.exception;

import static org.junit.jupiter.api.Assertions.*;

import com.nextuple.service.inventory.TestUtil;
import org.junit.jupiter.api.Test;

class ServiceInventoryDomainExceptionTest {

  @Test
  void constructTest() {
    ServiceInventoryDomainException exception =
        new ServiceInventoryDomainException("error", TestUtil.ORG_ID, TestUtil.SERVICE_OPTION);

    assertEquals("error", exception.getMessage());
    assertEquals(TestUtil.ORG_ID, exception.getOrgId());
    assertEquals(TestUtil.SERVICE_OPTION, exception.getServiceOption());
  }
}
