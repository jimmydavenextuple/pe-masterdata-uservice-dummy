package com.nextuple.carrier.exception;

import com.nextuple.carrier.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CarrierServiceDomainExceptionTest {

  @Test
  @DisplayName("Testing CarrierServiceDomainException")
  void constructTest() {
    CarrierServiceDomainException carrierServiceDomainException =
        new CarrierServiceDomainException(
            "test", TestUtil.CARRIER_ID, TestUtil.CARRIER_SERVICE_ID, TestUtil.ORG_ID);
    Assertions.assertEquals("test", carrierServiceDomainException.getMessage());
    Assertions.assertEquals(TestUtil.ORG_ID, carrierServiceDomainException.getOrgId());
    Assertions.assertEquals(TestUtil.CARRIER_ID, carrierServiceDomainException.getCarrierId());
    Assertions.assertEquals(
        TestUtil.CARRIER_SERVICE_ID, carrierServiceDomainException.getCarrierServiceId());
  }
}
