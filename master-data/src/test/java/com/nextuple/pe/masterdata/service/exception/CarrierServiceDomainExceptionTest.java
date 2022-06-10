package com.nextuple.pe.masterdata.service.exception;

import com.nextuple.pe.masterdata.exception.CarrierServiceDomainException;
import com.nextuple.pe.masterdata.service.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CarrierServiceDomainExceptionTest {

  @Test
  @DisplayName("Testing  CarrierServiceDomainException")
  void constructTest() {
    CarrierServiceDomainException carrierServiceDomainException =
        new CarrierServiceDomainException(
            "test", TestUtil.CARRIER_ID, TestUtil.SERVICE_ID, TestUtil.ORG_ID);
    Assertions.assertEquals("test", carrierServiceDomainException.getMessage());
    Assertions.assertEquals(TestUtil.ORG_ID, carrierServiceDomainException.getOrgId());
    Assertions.assertEquals(TestUtil.CARRIER_ID, carrierServiceDomainException.getCarrierId());
    Assertions.assertEquals(TestUtil.SERVICE_ID, carrierServiceDomainException.getServiceId());
  }
}
