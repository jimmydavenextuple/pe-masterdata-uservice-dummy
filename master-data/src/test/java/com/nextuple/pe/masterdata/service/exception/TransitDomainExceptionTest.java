package com.nextuple.pe.masterdata.service.exception;

import com.nextuple.pe.masterdata.exception.TransitDomainException;
import com.nextuple.pe.masterdata.service.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TransitDomainExceptionTest {

  @Test
  @DisplayName("Testing TransitDomainException")
  void constructTest() {
    TransitDomainException transitDomainException =
        new TransitDomainException(
            "test",
            TestUtil.ORG_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE,
            TestUtil.CARRIER_SERVICE_ID);
    Assertions.assertEquals("test", transitDomainException.getMessage());
    Assertions.assertEquals(TestUtil.ORG_ID, transitDomainException.getOrgId());
    Assertions.assertEquals(TestUtil.SOURCE_GEOZONE, transitDomainException.getSourceGeozone());
    Assertions.assertEquals(
        TestUtil.DESTINATION_GEOZONE, transitDomainException.getDestinationGeozone());
    Assertions.assertEquals(
        TestUtil.CARRIER_SERVICE_ID, transitDomainException.getCarrierServiceId());
  }
}
