package com.hbc.carrier.domain.entity;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.hbc.carrier.TestUtil;
import org.junit.jupiter.api.Test;

class CarrierEntityTest {

  @Test
  void carrierServiceEntityTest() {
    CarrierServiceEntity carrierServiceEntity = new CarrierServiceEntity();
    assertNull(carrierServiceEntity.getOrgId());
    assertNull(carrierServiceEntity.getCarrierId());
    assertNull(carrierServiceEntity.getCarrierServiceId());
    assertNull(carrierServiceEntity.getCarrierName());
    assertNull(carrierServiceEntity.getServiceName());
    assertNull(carrierServiceEntity.getServiceOptions());
  }

  @Test
  void carrierServiceIdNullDataTest() {
    CarrierServiceId carrierServiceId = new CarrierServiceId();
    assertNull(carrierServiceId.getCarrierServiceId());
    assertNull(carrierServiceId.getCarrierId());
    assertNull(carrierServiceId.getOrgId());
  }

  @Test
  void carrierServiceIdTest() {
    CarrierServiceId carrierServiceId =
        new CarrierServiceId(TestUtil.ORG_ID, TestUtil.CARRIER_ID, TestUtil.CARRIER_SERVICE_ID);
    assertNotNull(carrierServiceId.getCarrierServiceId());
    assertNotNull(carrierServiceId.getCarrierId());
    assertNotNull(carrierServiceId.getOrgId());
  }
}
