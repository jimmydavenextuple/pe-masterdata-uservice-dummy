package com.nextuple.service.inventory.domain.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.nextuple.service.inventory.TestUtil;
import org.junit.jupiter.api.Test;

class ServiceToInventoryIdTest {

  @Test
  void serviceToInventoryIdNullTest() {
    ServiceToInventoryId serviceToInventoryId = new ServiceToInventoryId();
    assertNull(serviceToInventoryId.getServiceOption());
    assertNull(serviceToInventoryId.getOrgId());
  }

  @Test
  void serviceToInventoryIdTest() {
    ServiceToInventoryId serviceToInventoryId =
        new ServiceToInventoryId(TestUtil.ORG_ID, TestUtil.SERVICE_OPTION);
    assertNotNull(serviceToInventoryId.getServiceOption());
    assertNotNull(serviceToInventoryId.getOrgId());
  }
}
